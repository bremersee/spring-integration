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

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import org.bremersee.ldaptive.serializable.SerLdapEntry;
import org.ldaptive.LdapEntry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The ldaptive user details.
 *
 * @author Christian Bremer
 */
@ToString
public class LdaptiveUserDetails extends SerLdapEntry implements UserDetails {

  @Serial
  private static final long serialVersionUID = 1L;

  private final String username;

  @Getter
  private final Collection<? extends GrantedAuthority> authorities;

  @Getter
  private final String password;

  @Getter
  private final boolean accountNonExpired;

  @Getter
  private final boolean accountNonLocked;

  @Getter
  private final boolean credentialsNonExpired;

  @Getter
  private final boolean enabled;

  /**
   * Instantiates a new serializable ldap entry.
   *
   * @param ldapEntry the ldap entry
   * @param username the username
   * @param authorities the authorities
   * @param password the password
   * @param accountNonExpired the account non expired
   * @param accountNonLocked the account non locked
   * @param credentialsNonExpired the credentials non expired
   * @param enabled the enabled
   */
  public LdaptiveUserDetails(
      LdapEntry ldapEntry,
      String username,
      Collection<? extends GrantedAuthority> authorities,
      String password,
      boolean accountNonExpired,
      boolean accountNonLocked,
      boolean credentialsNonExpired,
      boolean enabled) {
    super(ldapEntry);
    this.username = username;
    this.authorities = Optional.ofNullable(authorities).orElseGet(List::of);
    this.password = Optional.ofNullable(password)
        .orElseGet(() -> LdaptivePasswordProvider.invalid().getPassword(ldapEntry));
    this.accountNonExpired = accountNonExpired;
    this.accountNonLocked = accountNonLocked;
    this.credentialsNonExpired = credentialsNonExpired;
    this.enabled = enabled;
  }

  @Override
  public String getUsername() {
    return username;
  }

}
