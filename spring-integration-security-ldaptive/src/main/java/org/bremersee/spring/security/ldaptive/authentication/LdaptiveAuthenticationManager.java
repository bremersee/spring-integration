/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.spring.security.ldaptive.authentication;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElseGet;

import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bremersee.ldaptive.LdaptiveException;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.spring.security.core.EmailToUsernameResolver;
import org.bremersee.spring.security.ldaptive.authentication.provider.NoAccountControlEvaluator;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveRememberMeTokenProvider;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetails;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetailsService;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.CompareRequest;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.LdapException;
import org.ldaptive.ResultCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;

/**
 * The ldaptive authentication manager.
 *
 * @author Christian Bremer
 */
public class LdaptiveAuthenticationManager
    implements AuthenticationManager, AuthenticationProvider { // message source aware

  /**
   * The Logger.
   */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /**
   * The authentication properties.
   */
  @Getter(AccessLevel.PROTECTED)
  private final LdaptiveAuthenticationProperties authenticationProperties;

  /**
   * The application ldaptive template.
   */
  @Getter(AccessLevel.PROTECTED)
  private final LdaptiveTemplate applicationLdaptiveTemplate;

  /**
   * The email to username resolver.
   */
  @Getter(AccessLevel.PROTECTED)
  private EmailToUsernameResolver emailToUsernameResolver;

  /**
   * The username to bind-dn converter.
   */
  @Getter(AccessLevel.PROTECTED)
  private UsernameToBindDnConverter usernameToBindDnConverter;

  /**
   * The password encoder.
   */
  @Getter(AccessLevel.PROTECTED)
  @Setter
  private PasswordEncoder passwordEncoder;

  /**
   * The account control evaluator.
   */
  @Getter(AccessLevel.PROTECTED)
  private AccountControlEvaluator accountControlEvaluator;

  /**
   * The granted authorities mapper.
   */
  @Getter(AccessLevel.PROTECTED)
  @Setter
  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  /**
   * The remember-me token provider.
   */
  @Getter(AccessLevel.PROTECTED)
  @Setter
  private LdaptiveRememberMeTokenProvider passwordProvider;

  /**
   * The token converter.
   */
  @Getter(AccessLevel.PROTECTED)
  @Setter
  private Converter<LdaptiveUserDetails, LdaptiveAuthentication> tokenConverter;

  /**
   * Instantiates a new ldaptive authentication manager.
   *
   * @param connectionConfig the connection config
   * @param authenticationProperties the authentication properties
   */
  public LdaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      LdaptiveAuthenticationProperties authenticationProperties) {
    this(new DefaultConnectionFactory(connectionConfig), authenticationProperties);
  }

  /**
   * Instantiates a new ldaptive authentication manager.
   *
   * @param connectionFactory the connection factory
   * @param authenticationProperties the authentication properties
   */
  public LdaptiveAuthenticationManager(
      ConnectionFactory connectionFactory,
      LdaptiveAuthenticationProperties authenticationProperties) {
    this(new LdaptiveTemplate(connectionFactory), authenticationProperties);
  }

  /**
   * Instantiates a new ldaptive authentication manager.
   *
   * @param applicationLdaptiveTemplate the application ldaptive template
   * @param authenticationProperties the authentication properties
   */
  public LdaptiveAuthenticationManager(
      LdaptiveTemplate applicationLdaptiveTemplate,
      LdaptiveAuthenticationProperties authenticationProperties) {

    this.applicationLdaptiveTemplate = applicationLdaptiveTemplate;
    Assert.notNull(getApplicationLdaptiveTemplate(), "Application ldaptive template is required.");
    this.authenticationProperties = authenticationProperties;
    Assert.notNull(getAuthenticationProperties(), "Authentication properties are required.");

    // emailToUsernameResolver
    setEmailToUsernameResolver(new EmailToUsernameResolverByLdapAttribute(
        getAuthenticationProperties(), getApplicationLdaptiveTemplate()));

    // usernameToBindDnConverter
    Assert.notNull(getAuthenticationProperties().getUsernameToBindDnConverter(),
        "Username to bind dn converter is required.");
    setUsernameToBindDnConverter(getAuthenticationProperties().getUsernameToBindDnConverter()
        .apply(getAuthenticationProperties()));

    // accountControlEvaluator
    if (isNull(getAuthenticationProperties().getAccountControlEvaluator())) {
      setAccountControlEvaluator(new NoAccountControlEvaluator());
    } else {
      setAccountControlEvaluator(getAuthenticationProperties().getAccountControlEvaluator().get());
    }
  }

  /**
   * Sets email to username resolver.
   *
   * @param emailToUsernameResolver the email to username resolver
   */
  public void setEmailToUsernameResolver(
      EmailToUsernameResolver emailToUsernameResolver) {
    if (nonNull(emailToUsernameResolver)) {
      this.emailToUsernameResolver = emailToUsernameResolver;
    }
  }

  /**
   * Sets username to bind dn converter.
   *
   * @param usernameToBindDnConverter the username to bind dn converter
   */
  public void setUsernameToBindDnConverter(
      UsernameToBindDnConverter usernameToBindDnConverter) {
    if (nonNull(usernameToBindDnConverter)) {
      this.usernameToBindDnConverter = usernameToBindDnConverter;
    }
  }

  /**
   * Sets account control evaluator.
   *
   * @param accountControlEvaluator the account control evaluator
   */
  public void setAccountControlEvaluator(
      AccountControlEvaluator accountControlEvaluator) {
    if (nonNull(accountControlEvaluator)) {
      this.accountControlEvaluator = accountControlEvaluator;
    }
  }

  /**
   * Init.
   */
  public void init() {
    if (!bindWithAuthentication() && isNull(getPasswordEncoder())) {
      throw new IllegalStateException(String.format("A password attribute is set (%s) but no "
              + "password encoder is present. Either delete the password attribute to perform a "
              + "bind to authenticate or set a password encoder.",
          getAuthenticationProperties().getPasswordAttribute()));
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }

  /**
   * Determines whether the given authentication is a remember-me authentication.
   *
   * @param authentication the authentication
   * @return the boolean
   */
  protected boolean isRememberMeAuthenticationToken(Authentication authentication) {
    return authentication instanceof RememberMeAuthenticationToken
        && authentication.getPrincipal() instanceof LdaptiveUserDetails;
  }

  @Override
  public LdaptiveAuthentication authenticate(Authentication authentication)
      throws AuthenticationException {
    String name = getName(authentication);
    logger.debug("Authenticating user '" + name + "' ...");
    String username = getEmailToUsernameResolver()
        .getUsernameByEmail(name)
        .orElse(name);
    String password = Optional.ofNullable(authentication.getCredentials())
        .map(String::valueOf)
        .orElse(null);
    LdaptiveTemplate ldaptiveTemplate = getLdapTemplate(username, password);
    LdaptiveUserDetails userDetails = getUserDetails(ldaptiveTemplate, username);
    checkPassword(ldaptiveTemplate, userDetails, password);
    checkAccountControl(userDetails);
    if (nonNull(getTokenConverter())) {
      return getTokenConverter().convert(userDetails);
    }
    return new LdaptiveAuthenticationToken(userDetails);
  }

  /**
   * Gets name.
   *
   * @param authentication the authentication
   * @return the name
   */
  protected String getName(Authentication authentication) {
    Object principal = authentication.getPrincipal();
    if (principal instanceof LdaptiveUserDetails ldaptiveUserDetails) {
      return requireNonNullElseGet(ldaptiveUserDetails.getDn(), authentication::getName);
    }
    return authentication.getName();
  }

  /**
   * Determines whether to bind with username and password or with the application ldaptive
   * template.
   *
   * @return the boolean
   */
  protected boolean bindWithAuthentication() {
    return isNull(getAuthenticationProperties().getPasswordAttribute())
        || getAuthenticationProperties().getPasswordAttribute().isBlank();
  }

  /**
   * Gets ldap template.
   *
   * @param username the username
   * @param password the password
   * @return the ldap template
   */
  protected LdaptiveTemplate getLdapTemplate(String username, String password) {
    if (bindWithAuthentication()) {
      if (isNull(password)) {
        throw new BadCredentialsException("Password is required.");
      }
      ConnectionConfig authConfig = ConnectionConfig
          .copy(getApplicationLdaptiveTemplate().getConnectionFactory().getConnectionConfig());
      String bindDn = getUsernameToBindDnConverter().convert(username);
      authConfig.setConnectionInitializers(BindConnectionInitializer.builder()
          .dn(bindDn)
          .credential(password)
          .build());
      return new LdaptiveTemplate(new DefaultConnectionFactory(authConfig));
    }
    return getApplicationLdaptiveTemplate();
  }

  /**
   * Gets user details.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param username the username
   * @return the user details
   */
  protected LdaptiveUserDetails getUserDetails(LdaptiveTemplate ldaptiveTemplate, String username) {
    try {
      return getUserDetailsService(ldaptiveTemplate).loadUserByUsername(username);
    } catch (LdaptiveException le) {
      throw getBindException(le);
    }
  }

  /**
   * Gets user details service.
   *
   * @return the user details service
   */
  public LdaptiveUserDetailsService getUserDetailsService() {
    return getUserDetailsService(getApplicationLdaptiveTemplate());
  }

  /**
   * Gets user details service.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @return the user details service
   */
  protected LdaptiveUserDetailsService getUserDetailsService(LdaptiveTemplate ldaptiveTemplate) {
    LdaptiveUserDetailsService userDetailsService = new LdaptiveUserDetailsService(
        getAuthenticationProperties(), ldaptiveTemplate);
    userDetailsService.setAccountControlEvaluator(getAccountControlEvaluator());
    userDetailsService.setGrantedAuthoritiesMapper(getGrantedAuthoritiesMapper());
    userDetailsService.setRememberMeTokenProvider(getPasswordProvider());
    return userDetailsService;
  }

  private RuntimeException getBindException(LdaptiveException exception) {
    BadCredentialsException badCredentials = new BadCredentialsException("Password doesn't match.");
    if (isInvalidCredentialsException(exception.getLdapException())) {
      return badCredentials;
    }
    return exception;
  }

  private boolean isInvalidCredentialsException(LdapException exception) {
    if (Objects.isNull(exception)) {
      return false;
    }
    if (ResultCode.INVALID_CREDENTIALS.equals(exception.getResultCode())) {
      return true;
    }
    String message = Optional.ofNullable(exception.getMessage())
        .map(String::toLowerCase)
        .orElse("");
    String text = ("resultCode=" + ResultCode.INVALID_CREDENTIALS).toLowerCase();
    if (ResultCode.CONNECT_ERROR.equals(exception.getResultCode()) && message.contains(text)) {
      return true;
    }
    if (exception.getCause() instanceof LdapException cause) {
      return isInvalidCredentialsException(cause);
    }
    return false;
  }

  /**
   * Check password.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param user the user
   * @param password the password
   */
  protected void checkPassword(
      LdaptiveTemplate ldaptiveTemplate,
      LdaptiveUserDetails user,
      String password) {

    if (!bindWithAuthentication()) {
      Assert.notNull(getPasswordEncoder(), "No password encoder is present.");
      boolean matches = ldaptiveTemplate.compare(CompareRequest.builder()
          .dn(user.getDn())
          .name(getAuthenticationProperties().getPasswordAttribute())
          .value(getPasswordEncoder().encode(password))
          .build());
      if (!matches) {
        throw new BadCredentialsException("Password doesn't match.");
      }
    }
  }

  /**
   * Check account control.
   *
   * @param user the user
   */
  protected void checkAccountControl(LdaptiveUserDetails user) {
    if (!user.isEnabled()) {
      throw new DisabledException("Account is disabled.");
    }
    if (!user.isAccountNonLocked()) {
      throw new LockedException("Account is locked.");
    }
    if (!user.isAccountNonExpired()) {
      throw new AccountExpiredException("Account is expired.");
    }
    if (!user.isCredentialsNonExpired()) {
      throw new CredentialsExpiredException("Credentials are expired.");
    }
  }

}
