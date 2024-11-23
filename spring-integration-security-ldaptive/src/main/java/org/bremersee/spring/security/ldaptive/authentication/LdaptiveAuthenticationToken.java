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

import java.io.Serial;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetails;
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

  private final LdaptiveUserDetails userDetails;

  /**
   * Instantiates a new ldaptive authentication token.
   *
   * @param userDetails the user details
   */
  public LdaptiveAuthenticationToken(LdaptiveUserDetails userDetails) {
    super(userDetails.getAuthorities());
    this.userDetails = userDetails;
  }

  @Override
  public LdaptiveUserDetails getPrincipal() {
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

}
