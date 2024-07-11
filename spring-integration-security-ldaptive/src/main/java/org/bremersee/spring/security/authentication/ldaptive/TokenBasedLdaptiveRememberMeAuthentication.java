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

package org.bremersee.spring.security.authentication.ldaptive;

import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 * The token bases ldaptive remember me authentication.
 *
 * @author Christian Bremer
 */
public class TokenBasedLdaptiveRememberMeAuthentication
    extends LdaptiveRememberMeAuthentication {

  /**
   * Instantiates a new token bases ldaptive remember me authentication.
   *
   * @param key the key
   * @param authenticationManager the authentication manager
   */
  public TokenBasedLdaptiveRememberMeAuthentication(
      String key,
      LdaptiveAuthenticationManager authenticationManager) {
    super(key, authenticationManager);
  }

  @Override
  protected RememberMeServices getInternalRememberMeServices() {
    return new TokenBasedRememberMeServices(getKey(), getUserDetailsService());
  }
}
