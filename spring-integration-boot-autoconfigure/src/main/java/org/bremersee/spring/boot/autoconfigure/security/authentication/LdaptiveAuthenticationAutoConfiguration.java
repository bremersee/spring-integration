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
import org.bremersee.spring.boot.autoconfigure.ldaptive.LdaptiveAutoConfiguration;
import org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationManager;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationTokenConverter;
import org.bremersee.spring.security.authentication.ldaptive.ReactiveLdaptiveAuthenticationManager;
import org.bremersee.spring.security.authentication.ldaptive.UsernameToBindDnConverter;
import org.bremersee.spring.security.authentication.ldaptive.provider.Template;
import org.ldaptive.ConnectionConfig;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

/**
 * The ldaptive authentication auto-configuration.
 *
 * @author Christian Bremer
 */
@AutoConfiguration
@ConditionalOnClass(name = {
    "org.ldaptive.ConnectionFactory",
    "org.bremersee.ldaptive.LdaptiveTemplate",
    "org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationManager"
})
@ConditionalOnBean({ConnectionConfig.class})
@ConditionalOnProperty(prefix = "bremersee.authentication.ldaptive", name = "user-base-dn")
@AutoConfigureAfter({LdaptiveAutoConfiguration.class})
@EnableConfigurationProperties(AuthenticationProperties.class)
@Slf4j
public class LdaptiveAuthenticationAutoConfiguration {

  private final LdaptiveAuthenticationProperties properties;

  /**
   * Instantiates a new ldaptive authentication autoconfiguration.
   *
   * @param properties the properties
   */
  public LdaptiveAuthenticationAutoConfiguration(AuthenticationProperties properties) {
    LdaptiveAuthenticationProperties tmp = PropertiesMapper.INSTANCE.map(properties.getLdaptive());
    Template template;
    try {
      template = Template.valueOf(properties.getLdaptive().getTemplate().name());
    } catch (Exception ignored) {
      template = null;
    }
    this.properties = Optional
        .ofNullable(template)
        .map(t -> t.applyTemplate(tmp))
        .orElse(tmp);
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""

            *********************************************************************************
            * {}
            * properties = {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName(),
        properties);
  }

  /**
   * Creates ldaptive password encoder provider.
   *
   * @return the ldaptive password encoder provider
   */
  @ConditionalOnMissingBean
  @Bean
  public LdaptivePasswordEncoderProvider ldaptivePasswordEncoderProvider() {
    return LdaptivePasswordEncoderProvider.defaultProvider();
  }

  /**
   * Creates ldaptive authentication token converter.
   *
   * @return the ldaptive authentication token converter
   */
  @ConditionalOnMissingBean
  @Bean
  public LdaptiveAuthenticationTokenConverter ldaptiveAuthenticationTokenConverter() {
    return new LdaptiveAuthenticationTokenConverter(properties);
  }

  /**
   * Creates ldaptive authentication manager.
   *
   * @param connectionConfig the connection config
   * @param usernameToBindDnProvider the username to bind dn provider
   * @param ldaptivePasswordEncoderProvider the ldaptive password encoder provider
   * @param accountControlEvaluatorProvider the account control evaluator provider
   * @param tokenConverter the token converter
   * @return the ldaptive authentication manager
   */
  @ConditionalOnWebApplication(type = Type.SERVLET)
  @Bean(initMethod = "init")
  public LdaptiveAuthenticationManager ldaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      ObjectProvider<UsernameToBindDnConverter> usernameToBindDnProvider,
      LdaptivePasswordEncoderProvider ldaptivePasswordEncoderProvider,
      ObjectProvider<AccountControlEvaluator> accountControlEvaluatorProvider,
      LdaptiveAuthenticationTokenConverter tokenConverter) {

    LdaptiveAuthenticationManager manager = new LdaptiveAuthenticationManager(
        connectionConfig, properties);
    manager.setPasswordEncoder(ldaptivePasswordEncoderProvider.get());
    usernameToBindDnProvider.ifAvailable(manager::setUsernameToBindDnConverter);
    AccountControlEvaluator accountControlEvaluator = accountControlEvaluatorProvider
        .getIfAvailable(() -> properties.getAccountControlEvaluator().get());
    manager.setAccountControlEvaluator(accountControlEvaluator);
    manager.setAuthenticationTokenConverter(tokenConverter);
    return manager;
  }

  /**
   * Creates reactive ldaptive authentication manager.
   *
   * @param connectionConfig the connection config
   * @param usernameToBindDnProvider the username to bind dn provider
   * @param ldaptivePasswordEncoderProvider the ldaptive password encoder provider
   * @param accountControlEvaluatorProvider the account control evaluator provider
   * @param tokenConverter the token converter
   * @return the reactive ldaptive authentication manager
   */
  @ConditionalOnWebApplication(type = Type.REACTIVE)
  @Bean
  public ReactiveLdaptiveAuthenticationManager reactiveLdaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      ObjectProvider<UsernameToBindDnConverter> usernameToBindDnProvider,
      LdaptivePasswordEncoderProvider ldaptivePasswordEncoderProvider,
      ObjectProvider<AccountControlEvaluator> accountControlEvaluatorProvider,
      LdaptiveAuthenticationTokenConverter tokenConverter) {
    return new ReactiveLdaptiveAuthenticationManager(
        ldaptiveAuthenticationManager(
            connectionConfig,
            usernameToBindDnProvider,
            ldaptivePasswordEncoderProvider,
            accountControlEvaluatorProvider,
            tokenConverter));
  }

  /**
   * The interface Properties mapper.
   */
  @Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
  interface PropertiesMapper {

    /**
     * The constant INSTANCE.
     */
    PropertiesMapper INSTANCE = Mappers.getMapper(PropertiesMapper.class);

    /**
     * Map ldaptive authentication properties.
     *
     * @param source the source
     * @return the ldaptive authentication properties
     */
    LdaptiveAuthenticationProperties map(AuthenticationProperties.LdaptiveProperties source);

  }

}
