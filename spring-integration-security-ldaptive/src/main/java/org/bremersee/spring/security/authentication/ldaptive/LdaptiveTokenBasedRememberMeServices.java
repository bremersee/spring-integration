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

import jakarta.servlet.http.HttpServletRequest;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

/**
 * The ldaptive token based remember-me services.
 *
 * @author Christian Bremer
 */
public class LdaptiveTokenBasedRememberMeServices extends TokenBasedRememberMeServices
    implements LdaptiveRememberMeServices {

  private final LdaptiveAuthenticationProperties properties;

  /**
   * Instantiates new ldaptive token based remember-me services.
   *
   * @param key the key
   * @param properties the properties
   * @param authenticationManager the authentication manager
   */
  public LdaptiveTokenBasedRememberMeServices(
      String key,
      LdaptiveAuthenticationProperties properties,
      LdaptiveAuthenticationManager authenticationManager) {
    super(key, authenticationManager.getUserDetailsService());
    this.properties = properties;
  }

  @Override
  public UserDetailsService getUserDetailsService() {
    return super.getUserDetailsService();
  }

  @Override
  protected Authentication createSuccessfulAuthentication(
      HttpServletRequest request, UserDetails user) {
    if (user instanceof LdaptiveUserDetails ldaptiveUser) {
      return new LdaptiveRememberMeAuthenticationToken(
          getKey(),
          new LdaptiveAuthenticationToken(properties, ldaptiveUser));
    }
    return super.createSuccessfulAuthentication(request, user);
  }

}
