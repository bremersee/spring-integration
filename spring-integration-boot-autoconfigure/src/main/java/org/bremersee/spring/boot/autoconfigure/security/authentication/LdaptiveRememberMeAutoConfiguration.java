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

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.spring.boot.autoconfigure.security.authentication.AuthenticationProperties.RememberMeProperties;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationManager;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveTokenBasedRememberMeServices;
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
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * The ldaptive remember-me autoconfiguration.
 *
 * @author Christian Bremer
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "bremersee.authentication.remember-me", name = "key")
@ConditionalOnBean(
    type = "org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationManager")
@AutoConfigureAfter({LdaptiveAuthenticationAutoConfiguration.class})
@EnableConfigurationProperties(AuthenticationProperties.class)
@Slf4j
public class LdaptiveRememberMeAutoConfiguration {

  private final RememberMeProperties rememberMeProperties;

  private final LdaptiveAuthenticationProperties properties;

  /**
   * Instantiates a new ldaptive remember-me autoconfiguration.
   *
   * @param properties the properties
   */
  public LdaptiveRememberMeAutoConfiguration(AuthenticationProperties properties) {
    this.rememberMeProperties = properties.getRememberMe();
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
          * org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveRememberMeTokenProvider
          * and org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator!""";
    } else {
      message = String.format("OK: Using '%s' as password-last-set attribute.",
          properties.getPasswordLastSetAttribute());
    }
    log.info("""
            
            *********************************************************************************
            * {}
            * {}
            * {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName(),
        rememberMeProperties,
        message);
  }

  /**
   * Creates remember me authentication provider.
   *
   * @return the remember me authentication provider
   */
  @ConditionalOnMissingBean
  @Bean
  public RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
    return new RememberMeAuthenticationProvider(rememberMeProperties.getKey());
  }

  /**
   * Creates remember me services.
   *
   * @param authenticationManager the authentication manager
   * @return the remember me services
   */
  @ConditionalOnBean(LdaptiveAuthenticationManager.class)
  @ConditionalOnMissingBean
  @Bean
  public RememberMeServices rememberMeServices(
      LdaptiveAuthenticationManager authenticationManager) {
    LdaptiveTokenBasedRememberMeServices services = new LdaptiveTokenBasedRememberMeServices(
        rememberMeProperties.getKey(), authenticationManager);
    Optional.ofNullable(rememberMeProperties.getAlwaysRemember())
        .ifPresent(services::setAlwaysRemember);
    Optional.ofNullable(rememberMeProperties.getCookieName())
        .ifPresent(services::setCookieName);
    Optional.ofNullable(rememberMeProperties.getCookieDomain())
        .ifPresent(services::setCookieDomain);
    Optional.ofNullable(rememberMeProperties.getUseSecureCookie())
        .ifPresent(services::setUseSecureCookie);
    Optional.ofNullable(rememberMeProperties.getParameterName())
        .ifPresent(services::setParameter);
    Optional.ofNullable(rememberMeProperties.getTokenValiditySeconds())
        .ifPresent(services::setTokenValiditySeconds);
    return services;
  }

  /**
   * Creates remember me authentication filter.
   *
   * @param authenticationManager the authentication manager
   * @param rememberMeServices the remember me services
   * @return the remember me authentication filter
   */
  @ConditionalOnBean(LdaptiveAuthenticationManager.class)
  @ConditionalOnMissingBean
  @Bean
  public RememberMeAuthenticationFilter rememberMeAuthenticationFilter(
      LdaptiveAuthenticationManager authenticationManager,
      RememberMeServices rememberMeServices) {
    return new RememberMeAuthenticationFilter(authenticationManager, rememberMeServices);
  }

}
