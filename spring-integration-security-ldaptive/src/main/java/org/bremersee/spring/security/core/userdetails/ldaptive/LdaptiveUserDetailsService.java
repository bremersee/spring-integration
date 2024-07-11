/*
 * Copyright 2024 the original author or authors.
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

package org.bremersee.spring.security.core.userdetails.ldaptive;

import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bremersee.ldaptive.LdaptiveEntryMapper;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.ldaptive.FilterTemplate;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * The ldaptive user details service.
 *
 * @author Christian Bremer
 */
@Getter(AccessLevel.PROTECTED)
public class LdaptiveUserDetailsService implements UserDetailsService {

  /**
   * The constant USERNAME_PLACEHOLDER.
   */
  protected static final String USERNAME_PLACEHOLDER = "${username}";

  /**
   * The logger.
   */
  private final Log logger = LogFactory.getLog(this.getClass());

  /**
   * The authentication properties.
   */
  private final LdaptiveAuthenticationProperties authenticationProperties;

  /**
   * The ldaptive template.
   */
  private final LdaptiveTemplate ldaptiveTemplate;

  /**
   * The granted authorities mapper.
   */
  private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

  /**
   * The account control evaluator.
   */
  private AccountControlEvaluator accountControlEvaluator = new NoAccountControlEvaluator();

  /**
   * The password provider.
   */
  private LdaptivePasswordProvider passwordProvider = LdaptivePasswordProvider.invalid();

  /**
   * Instantiates a ldaptive user details service.
   *
   * @param authenticationProperties the authentication properties
   * @param ldaptiveTemplate the ldaptive template
   */
  public LdaptiveUserDetailsService(
      LdaptiveAuthenticationProperties authenticationProperties,
      LdaptiveTemplate ldaptiveTemplate) {
    this.authenticationProperties = authenticationProperties;
    this.ldaptiveTemplate = ldaptiveTemplate;
    setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
    setAccountControlEvaluator(authenticationProperties.getAccountControlEvaluator().get());
  }

  /**
   * Sets granted authorities mapper.
   *
   * @param grantedAuthoritiesMapper the granted authorities mapper
   */
  public void setGrantedAuthoritiesMapper(
      GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
    if (nonNull(grantedAuthoritiesMapper)) {
      this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
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
   * Sets password provider.
   *
   * @param passwordProvider the password provider
   */
  public void setPasswordProvider(LdaptivePasswordProvider passwordProvider) {
    if (nonNull(passwordProvider)) {
      this.passwordProvider = passwordProvider;
    }
  }

  @Override
  public LdaptiveUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return loadUserByUsername(username, null);
  }

  /**
   * Load user by username ldaptive user details.
   *
   * @param username the username
   * @param password the password
   * @return the ldaptive user details
   * @throws UsernameNotFoundException the username not found exception
   */
  public LdaptiveUserDetails loadUserByUsername(String username, String password)
      throws UsernameNotFoundException {

    if (nonNull(password)) {
      logger.debug("Loading user '" + username + "' with password ...");
    } else {
      logger.debug("Loading user '" + username + "' ...");
    }
    LdapEntry ldapEntry = findUser(username)
        .orElseThrow(() -> new UsernameNotFoundException(String.format("%s not found.", username)));
    Collection<? extends GrantedAuthority> authorities = getAuthorities(ldapEntry);
    String pwd = Optional.ofNullable(password)
        .map(p -> getPasswordProvider().getPassword(ldapEntry, p))
        .orElseGet(() -> getPasswordProvider().getPassword(ldapEntry));
    return new LdaptiveUserDetails(
        ldapEntry,
        Optional.ofNullable(getAuthenticationProperties().getUsernameAttribute())
            .map(ldapEntry::getAttribute)
            .map(LdapAttribute::getStringValue)
            .orElse(username),
        authorities,
        pwd,
        getAccountControlEvaluator().isAccountNonExpired(ldapEntry),
        getAccountControlEvaluator().isAccountNonLocked(ldapEntry),
        getAccountControlEvaluator().isCredentialsNonExpired(ldapEntry),
        getAccountControlEvaluator().isEnabled(ldapEntry)
    );
  }

  /**
   * Is dn boolean.
   *
   * @param username the username
   * @return the boolean
   */
  protected boolean isDn(String username) {
    return Optional.ofNullable(username)
        .map(String::toLowerCase)
        .filter(name -> name.endsWith(getAuthenticationProperties().getUserBaseDn().toLowerCase()))
        .isPresent();
  }

  /**
   * Find user.
   *
   * @param username the username
   * @return the user
   */
  public Optional<LdapEntry> findUser(String username) {
    SearchRequest searchRequest = isDn(username)
        ? SearchRequest.objectScopeSearchRequest(username)
        : SearchRequest.builder()
            .dn(getAuthenticationProperties().getUserBaseDn())
            .filter(FilterTemplate.builder()
                .filter(getAuthenticationProperties().getUserFindOneFilter())
                .parameters(username)
                .build())
            .scope(getAuthenticationProperties().getUserFindOneSearchScope())
            .sizeLimit(1)
            .build();
    return getLdaptiveTemplate().findOne(searchRequest);
  }

  /**
   * Gets authorities.
   *
   * @param user the user
   * @return the authorities
   */
  public Collection<? extends GrantedAuthority> getAuthorities(LdapEntry user) {

    return switch (getAuthenticationProperties().getGroupFetchStrategy()) {
      case NONE -> Set.of();
      case USER_CONTAINS_GROUPS -> getAuthoritiesByGroupsInUser(user);
      case GROUP_CONTAINS_USERS -> getAuthoritiesByGroupsWithUser(user);
    };
  }

  /**
   * Gets roles by groups in user.
   *
   * @param user the user
   * @return the roles by groups in user
   */
  protected Collection<? extends GrantedAuthority> getAuthoritiesByGroupsInUser(LdapEntry user) {
    Collection<? extends GrantedAuthority> authorities = Stream.ofNullable(user)
        .map(entry -> entry.getAttribute(getAuthenticationProperties().getMemberAttribute()))
        .filter(Objects::nonNull)
        .map(LdapAttribute::getStringValues)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(LdaptiveEntryMapper::getRdn)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
    return getGrantedAuthoritiesMapper().mapAuthorities(authorities);
  }

  /**
   * Gets roles by groups with user.
   *
   * @param user the user
   * @return the roles by groups with user
   */
  protected Collection<? extends GrantedAuthority> getAuthoritiesByGroupsWithUser(LdapEntry user) {
    Collection<? extends GrantedAuthority> authorities = getLdaptiveTemplate()
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
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
    return getGrantedAuthoritiesMapper().mapAuthorities(authorities);
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
      String username = user.getAttribute(getAuthenticationProperties().getUsernameAttribute())
          .getStringValue();
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
    return Optional.ofNullable(group.getAttribute(groupIdAttribute))
        .map(LdapAttribute::getStringValue)
        .orElse(fallback);
  }

}
