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

package org.bremersee.spring.security.oauth2.server.resource.authentication;

import java.util.Collection;
import org.bremersee.spring.security.core.NormalizedAuthentication;
import org.bremersee.spring.security.core.NormalizedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * The normalized jwt authentication token.
 *
 * @author Christian Bremer
 */
@Transient
public class NormalizedJwtAuthenticationToken extends JwtAuthenticationToken
    implements NormalizedAuthentication {

  /**
   * The principal.
   */
  private final NormalizedPrincipal principal;

  /**
   * Instantiates a new normalized jwt authentication token.
   *
   * @param jwt the jwt
   * @param principal the principal
   * @param authorities the authorities
   */
  public NormalizedJwtAuthenticationToken(
      Jwt jwt,
      NormalizedPrincipal principal,
      Collection<? extends GrantedAuthority> authorities) {
    super(jwt, authorities, principal.getName());
    this.principal = principal;
  }

  @Override
  public NormalizedPrincipal getPrincipal() {
    return principal;
  }

}
