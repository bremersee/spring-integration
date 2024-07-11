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
import java.util.Optional;
import org.bremersee.ldaptive.serializable.SerLdapAttr;
import org.bremersee.ldaptive.serializable.SerLdapEntry;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;

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
   * The properties.
   */
  private final LdaptiveAuthenticationProperties properties;

  private final LdaptiveUserDetails userDetails;

  public LdaptiveAuthenticationToken(
      LdaptiveAuthenticationProperties properties,
      LdaptiveUserDetails userDetails) {
    super(userDetails.getAuthorities());
    this.properties = properties;
    this.userDetails = userDetails;
  }

  @Override
  public SerLdapEntry getPrincipal() {
    return userDetails;
  }

  @Override
  public String getName() {
    return userDetails.getUsername();
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public Object getCredentials() {
    return userDetails.getPassword();
  }

  @Override
  public String getFirstName() {
    return Optional.ofNullable(properties.getFirstNameAttribute())
        .map(attr -> userDetails.getAttributes().get(attr))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

  @Override
  public String getLastName() {
    return Optional.ofNullable(properties.getLastNameAttribute())
        .map(attr -> userDetails.getAttributes().get(attr))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

  @Override
  public String getEmail() {
    return Optional.ofNullable(properties.getEmailAttribute())
        .map(attr -> userDetails.getAttributes().get(attr))
        .map(SerLdapAttr::getStringValue)
        .orElse(null);
  }

}
