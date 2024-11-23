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

package org.bremersee.spring.security.ldaptive.userdetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bremersee.ldaptive.serializable.SerLdapEntry;
import org.ldaptive.LdapEntry;
import org.springframework.security.core.GrantedAuthority;

/**
 * The ldaptive user.
 *
 * @author Christian Bremer
 */
@Getter
@ToString(exclude = {"password"})
@EqualsAndHashCode(callSuper = true, exclude = {"password"})
public class LdaptiveUser extends SerLdapEntry
    implements LdaptiveUserDetails {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The username.
   */
  private final String username;

  /**
   * The first name.
   */
  private final String firstName;

  /**
   * The last name.
   */
  private final String lastName;

  /**
   * The email address.
   */
  private final String email;

  /**
   * The granted authorities.
   */
  private final Collection<? extends GrantedAuthority> authorities;

  /**
   * The password.
   */
  private final String password;

  /**
   * Says whether the account is expired or not.
   */
  private final boolean accountNonExpired;

  /**
   * Says whether the account is locked or not.
   */
  private final boolean accountNonLocked;

  /**
   * Says whether the credentials are expired or not.
   */
  private final boolean credentialsNonExpired;

  /**
   * Says whether the account is enabled or not.
   */
  private final boolean enabled;

  /**
   * Instantiates a new ldaptive user.
   *
   * @param ldapEntry the ldap entry
   * @param username the username
   * @param authorities the authorities
   * @param password the password
   * @param accountNonExpired the account non expired
   * @param accountNonLocked the account non-locked
   * @param credentialsNonExpired the credentials non expired
   * @param enabled the enabled
   */
  public LdaptiveUser(
      LdapEntry ldapEntry,
      String username,
      String firstName,
      String lastName,
      String email,
      Collection<? extends GrantedAuthority> authorities,
      String password,
      boolean accountNonExpired,
      boolean accountNonLocked,
      boolean credentialsNonExpired,
      boolean enabled) {
    super(ldapEntry);
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.authorities = Optional.ofNullable(authorities).orElseGet(List::of);
    this.password = Optional.ofNullable(password)
        .orElseGet(() -> LdaptiveRememberMeTokenProvider.invalid().getRememberMeToken(ldapEntry));
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
  }

  @Override
  public String getName() {
    return getUsername();
  }

}
