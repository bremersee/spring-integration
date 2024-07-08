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

import java.io.Serial;
import java.util.Collection;
import java.util.Optional;
import org.bremersee.ldaptive.serializable.SerLdapAttr;
import org.bremersee.ldaptive.serializable.SerLdapEntry;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

/**
 * The ldaptive authentication token.
 *
 * @author Christian Bremer
 */
public class LdaptiveAuthenticationToken
    extends AbstractAuthenticationToken
    implements LdaptiveAuthentication {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The username.
   */
  private final String username;

  /**
   * The ldap entry.
   */
  private final SerLdapEntry ldapEntry;

  /**
   * The properties.
   */
  private final LdaptiveAuthenticationProperties properties;

  /**
   * Creates a token with the supplied array of authorities.
   *
   * @param username the username
   * @param authorities the collection of {@code GrantedAuthority}s for the principal
   *     represented by this authentication object.
   * @param ldapEntry the ldap entry
   * @param properties the properties
   */
  public LdaptiveAuthenticationToken(
      String username,
      Collection<? extends GrantedAuthority> authorities,
      SerLdapEntry ldapEntry,
      LdaptiveAuthenticationProperties properties) {
    super(authorities);
    Assert.notNull(username, "Username must be present.");
    Assert.notNull(ldapEntry, "Ldap entry of user must be present.");
    Assert.notNull(ldapEntry, "Ldap properties must be present.");
    this.username = username;
    this.ldapEntry = ldapEntry;
    this.properties = properties;
  }

  @Override
  public SerLdapEntry getPrincipal() {
    return ldapEntry;
  }

  @Override
  public String getName() {
    return username;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public String getFirstName() {
    return Optional.ofNullable(properties.getFirstNameAttribute())
        .map(attr -> ldapEntry.getAttributes().get(attr))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

  @Override
  public String getLastName() {
    return Optional.ofNullable(properties.getLastNameAttribute())
        .map(attr -> ldapEntry.getAttributes().get(attr))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

  @Override
  public String getEmail() {
    return Optional.ofNullable(properties.getEmailAttribute())
        .map(attr -> ldapEntry.getAttributes().get(attr))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

}
