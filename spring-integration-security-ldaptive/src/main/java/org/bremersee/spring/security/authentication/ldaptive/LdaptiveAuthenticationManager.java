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

package org.bremersee.spring.security.authentication.ldaptive;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bremersee.ldaptive.LdaptiveEntryMapper;
import org.bremersee.ldaptive.LdaptiveException;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.spring.security.authentication.AuthenticationSource;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.CompareRequest;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.FilterTemplate;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ResultCode;
import org.ldaptive.SearchRequest;
import org.ldaptive.transcode.StringValueTranscoder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
   * The constant USERNAME_PLACEHOLDER.
   */
  protected static final String USERNAME_PLACEHOLDER = "${username}";

  /**
   * The constant STRING_TRANSCODER.
   */
  protected static final StringValueTranscoder STRING_TRANSCODER = new StringValueTranscoder();

  @Getter(AccessLevel.PROTECTED)
  private final ConnectionConfig connectionConfig;

  @Getter(AccessLevel.PROTECTED)
  private final LdaptiveAuthenticationProperties authenticationProperties;

  @Getter(AccessLevel.PROTECTED)
  private Function<ConnectionFactory, LdaptiveTemplate> ldaptiveTemplateFn;

  @Getter(AccessLevel.PROTECTED)
  private EmailToUsernameConverter emailToUsernameConverter;

  @Getter(AccessLevel.PROTECTED)
  private UsernameToBindDnConverter usernameToBindDnConverter;

  @Getter(AccessLevel.PROTECTED)
  @Setter
  private PasswordEncoder passwordEncoder;

  @Getter(AccessLevel.PROTECTED)
  private AccountControlEvaluator accountControlEvaluator = new NoAccountControlEvaluator();

  @Getter(AccessLevel.PROTECTED)
  private Converter<AuthenticationSource<LdapEntry>, LdaptiveAuthentication> tokenConverter;

  /**
   * Instantiates a new ldaptive authentication manager.
   *
   * @param connectionConfig the connection config
   * @param authenticationProperties the authentication properties
   */
  public LdaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      LdaptiveAuthenticationProperties authenticationProperties) {
    this.connectionConfig = connectionConfig;
    this.authenticationProperties = authenticationProperties;
    this.ldaptiveTemplateFn = LdaptiveTemplate::new;
    this.emailToUsernameConverter = new EmailToUsernameConverterByLdapAttribute(
        authenticationProperties, connectionConfig);
    this.usernameToBindDnConverter = authenticationProperties
        .getUsernameToBindDnConverter()
        .apply(authenticationProperties);
    this.tokenConverter = new LdaptiveAuthenticationTokenConverter(authenticationProperties);
  }

  /**
   * Sets ldaptive template fn.
   *
   * @param ldaptiveTemplateFn the ldaptive template fn
   */
  public void setLdaptiveTemplateFn(
      Function<ConnectionFactory, LdaptiveTemplate> ldaptiveTemplateFn) {
    if (nonNull(ldaptiveTemplateFn)) {
      this.ldaptiveTemplateFn = ldaptiveTemplateFn;
    }
  }

  /**
   * Sets email to username converter.
   *
   * @param emailToUsernameConverter the email to username converter
   */
  public void setEmailToUsernameConverter(
      EmailToUsernameConverter emailToUsernameConverter) {
    if (nonNull(emailToUsernameConverter)) {
      this.emailToUsernameConverter = emailToUsernameConverter;
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
   * Sets authentication token converter.
   *
   * @param converter the converter
   */
  public void setAuthenticationTokenConverter(
      Converter<AuthenticationSource<LdapEntry>, LdaptiveAuthentication> converter) {
    if (nonNull(converter)) {
      this.tokenConverter = converter;
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
    return UsernamePasswordAuthenticationToken.class
        .isAssignableFrom(authentication);
  }

  /**
   * Changes the password of the user. Extended Operation(1.3.6.1.4.1.4203.1.11.1) must be
   * supported.
   *
   * @param username the username
   * @param currentRawPassword the current password
   * @param newRawPassword the new password
   */
  public void changePassword(String username, String currentRawPassword, String newRawPassword) {
    LdaptiveTemplate ldaptiveTemplate = getLdapTemplate(username, currentRawPassword);
    String dn = authenticate(username, currentRawPassword, ldaptiveTemplate)
        .getPrincipal()
        .getDn();
    ldaptiveTemplate.modifyUserPassword(dn, currentRawPassword, newRawPassword);
  }

  @Override
  public LdaptiveAuthentication authenticate(Authentication authentication)
      throws AuthenticationException {
    String username = getEmailToUsernameConverter()
        .getUsernameByEmail(authentication.getName())
        .orElseGet(authentication::getName);
    String password = String.valueOf(authentication.getCredentials());
    LdaptiveTemplate ldaptiveTemplate = getLdapTemplate(username, password);
    return authenticate(username, password, ldaptiveTemplate);
  }

  /**
   * Authenticate ldaptive authentication.
   *
   * @param username the username
   * @param password the password
   * @param ldaptiveTemplate the ldaptive template
   * @return the ldaptive authentication
   */
  protected LdaptiveAuthentication authenticate(
      String username,
      String password,
      LdaptiveTemplate ldaptiveTemplate) {

    LdapEntry user = getUser(ldaptiveTemplate, username);
    if (isNull(getUsername(user))) {
      user.addAttributes(LdapAttribute.builder()
          .name(getAuthenticationProperties().getUsernameAttribute())
          .values(username)
          .build());
    }
    checkPassword(ldaptiveTemplate, user, password);
    checkAccountControl(user);
    return getTokenConverter().convert(
        new LdaptiveAuthenticationSource(getUsername(user),
            getAuthorities(ldaptiveTemplate, user),
            user));
  }

  /**
   * Bind with authentication boolean.
   *
   * @return the boolean
   */
  protected boolean bindWithAuthentication() {
    return isNull(getAuthenticationProperties().getPasswordAttribute())
        || getAuthenticationProperties().getPasswordAttribute().isBlank();
  }

  /**
   * Gets connection factory.
   *
   * @param username the username
   * @param password the password
   * @return the connection factory
   */
  protected ConnectionFactory getConnectionFactory(String username, String password) {
    if (bindWithAuthentication()) {
      ConnectionConfig authConfig = ConnectionConfig.copy(getConnectionConfig());
      String bindDn = getUsernameToBindDnConverter().convert(username);
      authConfig.setConnectionInitializers(BindConnectionInitializer.builder()
          .dn(bindDn)
          .credential(password)
          .build());
      return new DefaultConnectionFactory(authConfig);
    }
    return new DefaultConnectionFactory(getConnectionConfig());
  }

  /**
   * Gets ldap template.
   *
   * @param username the username
   * @param password the password
   * @return the ldap template
   */
  protected LdaptiveTemplate getLdapTemplate(String username, String password) {
    return getLdaptiveTemplateFn().apply(getConnectionFactory(username, password));
  }

  /**
   * Gets user.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param username the username
   * @return the user
   * @throws UsernameNotFoundException the username not found exception
   */
  protected LdapEntry getUser(LdaptiveTemplate ldaptiveTemplate, String username)
      throws UsernameNotFoundException {

    try {
      return ldaptiveTemplate
          .findOne(
              SearchRequest.builder()
                  .dn(getAuthenticationProperties().getUserBaseDn())
                  .filter(FilterTemplate.builder()
                      .filter(getAuthenticationProperties().getUserFindOneFilter())
                      .parameters(username)
                      .build())
                  .scope(getAuthenticationProperties().getUserFindOneSearchScope())
                  .sizeLimit(1)
                  .build())
          .orElseThrow(() -> new UsernameNotFoundException(
              "User '" + username + "' was not found."));
    } catch (LdaptiveException le) {
      throw getBindException(le);
    }
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
      LdapEntry user,
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
  protected void checkAccountControl(LdapEntry user) {
    if (!getAccountControlEvaluator().isEnabled(user)) {
      throw new DisabledException("Account is disabled.");
    }
    if (!getAccountControlEvaluator().isAccountNonLocked(user)) {
      throw new LockedException("Account is locked.");
    }
    if (!getAccountControlEvaluator().isAccountNonExpired(user)) {
      throw new AccountExpiredException("Account is expired.");
    }
    if (!getAccountControlEvaluator().isCredentialsNonExpired(user)) {
      throw new CredentialsExpiredException("Credentials are expired.");
    }
  }

  /**
   * Gets authorities.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param user the user
   * @return the authorities
   */
  protected Collection<? extends String> getAuthorities(
      LdaptiveTemplate ldaptiveTemplate, LdapEntry user) {

    return switch (getAuthenticationProperties().getGroupFetchStrategy()) {
      case NONE -> Set.of();
      case USER_CONTAINS_GROUPS -> getAuthoritiesByGroupsInUser(user);
      case GROUP_CONTAINS_USERS -> getAuthoritiesByGroupsWithUser(ldaptiveTemplate, user);
    };
  }

  /**
   * Gets roles by groups in user.
   *
   * @param user the user
   * @return the roles by groups in user
   */
  protected Collection<? extends String> getAuthoritiesByGroupsInUser(LdapEntry user) {
    return LdaptiveEntryMapper.getAttributeValues(
            user, getAuthenticationProperties().getMemberAttribute(), STRING_TRANSCODER)
        .stream()
        .map(LdaptiveEntryMapper::getRdn)
        .collect(Collectors.toSet());
  }

  /**
   * Gets roles by groups with user.
   *
   * @param ldaptiveTemplate the ldaptive template
   * @param user the user
   * @return the roles by groups with user
   */
  protected Collection<? extends String> getAuthoritiesByGroupsWithUser(
      LdaptiveTemplate ldaptiveTemplate, LdapEntry user) {
    return ldaptiveTemplate
        .findAll(
            SearchRequest.builder()
                .dn(getAuthenticationProperties().getGroupBaseDn())
                .filter(FilterTemplate.builder()
                    .filter(getAuthorityFilter(user))
                    .build())
                .scope(getAuthenticationProperties().getGroupSearchScope())
                .build())
        .stream()
        .map(this::getAuthorityName)
        .collect(Collectors.toSet());
  }

  /**
   * Gets group filter.
   *
   * @param user the user
   * @return the group filter
   */
  protected String getAuthorityFilter(LdapEntry user) {
    String groupObjectClass = getAuthenticationProperties().getGroupObjectClass();
    String groupMemberAttribute = getAuthenticationProperties().getGroupMemberAttribute();
    String groupMemberValue;
    String groupMemberFormat = getAuthenticationProperties().getGroupMemberFormat();
    if (isEmpty(groupMemberFormat)) {
      groupMemberValue = user.getDn();
    } else {
      String username = getUsername(user);
      groupMemberValue = groupMemberFormat
          .replaceFirst(Pattern.quote(USERNAME_PLACEHOLDER), username);
    }
    return String.format("(&(objectClass=%s)(%s=%s))",
        groupObjectClass, groupMemberAttribute, groupMemberValue);
  }

  /**
   * Gets group name.
   *
   * @param group the group
   * @return the group name
   */
  protected String getAuthorityName(LdapEntry group) {
    String groupIdAttribute = getAuthenticationProperties().getGroupIdAttribute();
    String fallback = LdaptiveEntryMapper.getRdn(group.getDn());
    if (isEmpty(groupIdAttribute)) {
      return fallback;
    }
    return LdaptiveEntryMapper
        .getAttributeValue(group, groupIdAttribute, STRING_TRANSCODER, fallback);
  }

  /**
   * Gets username.
   *
   * @param user the user
   * @return the username
   */
  protected String getUsername(LdapEntry user) {
    return LdaptiveEntryMapper.getAttributeValue(
        user, getAuthenticationProperties().getUsernameAttribute(), STRING_TRANSCODER, null);
  }

}
