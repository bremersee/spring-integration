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

package org.bremersee.spring.boot.autoconfigure.security.authentication;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationManager;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveRememberMeAuthenticationComponents;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * The ldaptive remember-me autoconfiguration.
 *
 * @author Christian Bremer
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "bremersee.authentication", name = "remember-me-key")
@ConditionalOnBean(
    type = "org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationManager")
@AutoConfigureAfter({LdaptiveAuthenticationAutoConfiguration.class})
@EnableConfigurationProperties(AuthenticationProperties.class)
@Slf4j
public class LdaptiveRememberMeAutoConfiguration {

  private final String rememberMeKey;

  private final LdaptiveAuthenticationProperties properties;

  /**
   * Instantiates a new ldaptive remember-me autoconfiguration.
   *
   * @param properties the properties
   */
  public LdaptiveRememberMeAutoConfiguration(AuthenticationProperties properties) {
    this.rememberMeKey = properties.getRememberMeKey();
    this.properties = LdaptivePropertiesMapper.map(properties);
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    String message;
    if (ObjectUtils.isEmpty(properties.getPasswordLastSetAttribute())) {
      message = """
          WARNING: There is no password-last-set attribute configured.
          * Remembered users can login as long as they exist and have been correctly evaluated.
          * You may provide your own
          * org.bremersee.spring.security.core.userdetails.ldaptive.LdaptivePasswordProvider
          * and org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator!""";
    } else {
      message = String.format("OK: Using '%s' as password-last-set attribute.",
          properties.getPasswordLastSetAttribute());
    }
    log.info("""

            *********************************************************************************
            * {}
            * {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName(),
        message);
  }

  /**
   * Creates ldaptive remember me authentication components with token based remember-me services.
   *
   * @param authenticationManager the authentication manager
   * @return the ldaptive remember me authentication components
   */
  @ConditionalOnMissingBean
  @ConditionalOnBean(LdaptiveAuthenticationManager.class)
  @Bean
  public LdaptiveRememberMeAuthenticationComponents ldaptiveRememberMeAuthenticationComponents(
      LdaptiveAuthenticationManager authenticationManager) {
    return new LdaptiveRememberMeAuthenticationComponents(
        rememberMeKey,
        authenticationManager,
        TokenBasedRememberMeServices::new);
  }

  /**
   * Creates remember me authentication provider.
   *
   * @param rememberMeAuthentication the remember me authentication
   * @return the remember me authentication provider
   */
  @ConditionalOnMissingBean
  @Bean
  public RememberMeAuthenticationProvider rememberMeAuthenticationProvider(
      LdaptiveRememberMeAuthenticationComponents rememberMeAuthentication) {
    return rememberMeAuthentication.getRememberMeAuthenticationProvider();
  }

  /**
   * Creates remember me services.
   *
   * @param rememberMeAuthentication the remember me authentication
   * @return the remember me services
   */
  @ConditionalOnMissingBean
  @Bean
  public RememberMeServices rememberMeServices(
      LdaptiveRememberMeAuthenticationComponents rememberMeAuthentication) {
    return rememberMeAuthentication.getRememberMeServices();
  }

  /**
   * Creates remember me authentication filter.
   *
   * @param rememberMeAuthentication the remember me authentication
   * @return the remember me authentication filter
   */
  @ConditionalOnMissingBean
  @Bean
  public RememberMeAuthenticationFilter rememberMeAuthenticationFilter(
      LdaptiveRememberMeAuthenticationComponents rememberMeAuthentication) {
    return rememberMeAuthentication.getRememberMeAuthenticationFilter();
  }

}
