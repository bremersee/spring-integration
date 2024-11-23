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

import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.spring.boot.autoconfigure.ldaptive.LdaptiveAutoConfiguration;
import org.bremersee.spring.security.core.EmailToUsernameResolver;
import org.bremersee.spring.security.core.authority.mapping.NormalizedGrantedAuthoritiesMapper;
import org.bremersee.spring.security.ldaptive.authentication.AccountControlEvaluator;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthentication;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationManager;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.ldaptive.authentication.ReactiveLdaptiveAuthenticationManager;
import org.bremersee.spring.security.ldaptive.authentication.UsernameToBindDnConverter;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveEvaluatedRememberMeTokenProvider;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptivePwdLastSetRememberMeTokenProvider;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveRememberMeTokenProvider;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetails;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
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
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
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
    "org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationManager"
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
    this.properties = LdaptivePropertiesMapper.map(properties);
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
   * The ldaptive remember-me token provider.
   *
   * @param accountControlEvaluator the account control evaluator
   * @return the ldaptive remember-me token provider
   */
  @ConditionalOnMissingBean
  @Bean
  public LdaptiveRememberMeTokenProvider ldaptiveRememberMeTokenProvider(
      ObjectProvider<AccountControlEvaluator> accountControlEvaluator) {

    AccountControlEvaluator evaluator = accountControlEvaluator
        .getIfAvailable(() -> properties.getAccountControlEvaluator().get());
    if (!isEmpty(properties.getPasswordLastSetAttribute())) {
      return new LdaptivePwdLastSetRememberMeTokenProvider(
          evaluator, properties.getPasswordLastSetAttribute());
    }
    return new LdaptiveEvaluatedRememberMeTokenProvider(evaluator);
  }

  /**
   * Creates ldaptive authentication manager.
   *
   * @param connectionConfig the connection config
   * @param connectionFactoryProvider the connection factory provider
   * @param ldaptiveTemplateProvider the ldaptive template provider
   * @param ldaptivePasswordEncoderProvider the ldaptive password encoder provider
   * @param ldaptiveRememberMeTokenProvider the ldaptive remember-me token provider
   * @param emailToUsernameResolver the email to username resolver
   * @param usernameToBindDnConverter the username to bind dn provider
   * @param accountControlEvaluator the account control evaluator
   * @param grantedAuthoritiesMapper the granted authorities mapper
   * @param tokenConverter the token converter
   * @return the ldaptive authentication manager
   */
  @ConditionalOnWebApplication(type = Type.SERVLET)
  @Bean(initMethod = "init")
  public LdaptiveAuthenticationManager ldaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      ObjectProvider<ConnectionFactory> connectionFactoryProvider,
      ObjectProvider<LdaptiveTemplate> ldaptiveTemplateProvider,
      LdaptivePasswordEncoderProvider ldaptivePasswordEncoderProvider,
      LdaptiveRememberMeTokenProvider ldaptiveRememberMeTokenProvider,
      ObjectProvider<EmailToUsernameResolver> emailToUsernameResolver,
      ObjectProvider<UsernameToBindDnConverter> usernameToBindDnConverter,
      ObjectProvider<AccountControlEvaluator> accountControlEvaluator,
      ObjectProvider<GrantedAuthoritiesMapper> grantedAuthoritiesMapper,
      ObjectProvider<Converter<LdaptiveUserDetails, LdaptiveAuthentication>> tokenConverter) {

    LdaptiveAuthenticationManager manager = new LdaptiveAuthenticationManager(
        getLdaptiveTemplate(connectionConfig, connectionFactoryProvider, ldaptiveTemplateProvider),
        properties);
    manager.setPasswordEncoder(ldaptivePasswordEncoderProvider.get());
    manager.setPasswordProvider(ldaptiveRememberMeTokenProvider);
    emailToUsernameResolver.ifAvailable(manager::setEmailToUsernameResolver);
    usernameToBindDnConverter.ifAvailable(manager::setUsernameToBindDnConverter);
    accountControlEvaluator.ifAvailable(manager::setAccountControlEvaluator);
    manager.setGrantedAuthoritiesMapper(getGrantedAuthoritiesMapper(grantedAuthoritiesMapper));
    tokenConverter.ifAvailable(manager::setTokenConverter);
    return manager;
  }

  /**
   * Creates reactive ldaptive authentication manager.
   *
   * @param connectionConfig the connection config
   * @param connectionFactoryProvider the connection factory provider
   * @param ldaptiveTemplateProvider the ldaptive template provider
   * @param ldaptivePasswordEncoderProvider the ldaptive password encoder provider
   * @param ldaptiveRememberMeTokenProvider the ldaptive remember-me token provider
   * @param emailToUsernameResolver the email to username resolver
   * @param usernameToBindDnConverter the username to bind dn converter
   * @param accountControlEvaluator the account control evaluator
   * @param grantedAuthoritiesMapper the granted authorities mapper
   * @param tokenConverter the token converter
   * @return the reactive ldaptive authentication manager
   */
  @ConditionalOnWebApplication(type = Type.REACTIVE)
  @Bean
  public ReactiveLdaptiveAuthenticationManager reactiveLdaptiveAuthenticationManager(
      ConnectionConfig connectionConfig,
      ObjectProvider<ConnectionFactory> connectionFactoryProvider,
      ObjectProvider<LdaptiveTemplate> ldaptiveTemplateProvider,
      LdaptivePasswordEncoderProvider ldaptivePasswordEncoderProvider,
      LdaptiveRememberMeTokenProvider ldaptiveRememberMeTokenProvider,
      ObjectProvider<EmailToUsernameResolver> emailToUsernameResolver,
      ObjectProvider<UsernameToBindDnConverter> usernameToBindDnConverter,
      ObjectProvider<AccountControlEvaluator> accountControlEvaluator,
      ObjectProvider<GrantedAuthoritiesMapper> grantedAuthoritiesMapper,
      ObjectProvider<Converter<LdaptiveUserDetails, LdaptiveAuthentication>> tokenConverter) {
    return new ReactiveLdaptiveAuthenticationManager(
        ldaptiveAuthenticationManager(
            connectionConfig,
            connectionFactoryProvider,
            ldaptiveTemplateProvider,
            ldaptivePasswordEncoderProvider,
            ldaptiveRememberMeTokenProvider,
            emailToUsernameResolver,
            usernameToBindDnConverter,
            accountControlEvaluator,
            grantedAuthoritiesMapper,
            tokenConverter));
  }

  private LdaptiveTemplate getLdaptiveTemplate(
      ConnectionConfig connectionConfig,
      ObjectProvider<ConnectionFactory> connectionFactoryProvider,
      ObjectProvider<LdaptiveTemplate> ldaptiveTemplateProvider) {

    LdaptiveTemplate ldaptiveTemplate = ldaptiveTemplateProvider.getIfAvailable();
    if (nonNull(ldaptiveTemplate)) {
      return ldaptiveTemplate;
    }
    ConnectionFactory connectionFactory = connectionFactoryProvider.getIfAvailable();
    if (nonNull(connectionFactory)) {
      return new LdaptiveTemplate(connectionFactory);
    }
    return new LdaptiveTemplate(new DefaultConnectionFactory(connectionConfig));
  }

  private GrantedAuthoritiesMapper getGrantedAuthoritiesMapper(
      ObjectProvider<GrantedAuthoritiesMapper> grantedAuthoritiesMapper) {
    return grantedAuthoritiesMapper.getIfAvailable(() -> new NormalizedGrantedAuthoritiesMapper(
        properties.getDefaultRoles(),
        properties.toRoleMappings(),
        properties.getRolePrefix(),
        properties.getRoleCaseTransformation(),
        properties.toRoleStringReplacements()));
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
