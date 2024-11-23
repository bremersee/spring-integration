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

package org.bremersee.spring.security.ldaptive.authentication;

import java.io.Serial;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetails;
import org.springframework.security.authentication.RememberMeAuthenticationToken;

/**
 * The ldaptive remember-me authentication token.
 *
 * @author Christian Bremer
 */
public class LdaptiveRememberMeAuthenticationToken
    extends RememberMeAuthenticationToken
    implements LdaptiveAuthentication {

  @Serial
  private static final long serialVersionUID = 1L;

  private final LdaptiveAuthentication delegate;

  /**
   * Constructor.
   *
   * @param key to identify if this object made by an authorised client
   * @param ldaptiveAuthentication the ldaptive authentication
   * @throws IllegalArgumentException if a {@code null} was passed
   */
  public LdaptiveRememberMeAuthenticationToken(
      String key,
      LdaptiveAuthentication ldaptiveAuthentication) {
    super(key, ldaptiveAuthentication.getPrincipal(), ldaptiveAuthentication.getAuthorities());
    this.delegate = ldaptiveAuthentication;
  }

  @Override
  public LdaptiveUserDetails getPrincipal() {
    return delegate.getPrincipal();
  }

  @Override
  public Object getCredentials() {
    return delegate.getCredentials();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

}
