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

import static java.util.Objects.nonNull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.BiFunction;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetailsService;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.util.Assert;

/**
 * The ldaptive remember-me authentication components.
 *
 * @author Christian Bremer
 */
public class LdaptiveRememberMeAuthenticationComponents {

  /**
   * The key.
   */
  @Getter
  private final String key;

  /**
   * The authentication manager.
   */
  @Getter(AccessLevel.PROTECTED)
  private final LdaptiveAuthenticationManager authenticationManager;

  /**
   * The remember-me services.
   */
  @Getter
  private final RememberMeServices rememberMeServices;

  /**
   * Instantiates a ldaptive remember me authentication.
   *
   * @param key the key
   * @param authenticationManager the authentication manager
   * @param rememberMeServicesFactory the remember me services factory
   */
  public LdaptiveRememberMeAuthenticationComponents(
      String key,
      LdaptiveAuthenticationManager authenticationManager,
      BiFunction<String, UserDetailsService, RememberMeServices> rememberMeServicesFactory) {
    this.key = key;
    this.authenticationManager = authenticationManager;
    this.rememberMeServices = new LdaptiveRememberMeServices(
        rememberMeServicesFactory.apply(getKey(), getUserDetailsService()));
  }

  /**
   * Gets user details service.
   *
   * @return the user details service
   */
  public LdaptiveUserDetailsService getUserDetailsService() {
    return getAuthenticationManager().getUserDetailsService();
  }

  /**
   * Gets remember me authentication provider.
   *
   * @return the remember me authentication provider
   */
  public RememberMeAuthenticationProvider getRememberMeAuthenticationProvider() {
    return new RememberMeAuthenticationProvider(getKey());
  }

  /**
   * Gets remember me authentication filter.
   *
   * @return the remember me authentication filter
   */
  public RememberMeAuthenticationFilter getRememberMeAuthenticationFilter() {
    return new RememberMeAuthenticationFilter(getAuthenticationManager(), getRememberMeServices());
  }

  /**
   * The ldaptive remember me services.
   *
   * @author Christian Bremer
   */
  class LdaptiveRememberMeServices implements RememberMeServices {

    private final RememberMeServices delegate;

    /**
     * Instantiates new ldaptive remember me services.
     *
     * @param delegate the delegate
     */
    public LdaptiveRememberMeServices(RememberMeServices delegate) {
      Assert.notNull(delegate, "Delegate remember-me service must not be null.");
      this.delegate = delegate;
    }

    @Override
    public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
      Authentication autoAuth = delegate.autoLogin(request, response);
      try {
        LdaptiveAuthentication ldapAuth = getAuthenticationManager().authenticate(autoAuth);
        return nonNull(ldapAuth)
            ? new LdaptiveRememberMeAuthenticationToken(getKey(), ldapAuth)
            : autoAuth;

      } catch (AuthenticationException e) {
        loginFail(request, response);
        return null;
      }
    }

    @Override
    public void loginFail(HttpServletRequest request, HttpServletResponse response) {
      delegate.loginFail(request, response);
    }

    @Override
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication successfulAuthentication) {
      delegate.loginSuccess(request, response, successfulAuthentication);
    }
  }

}
