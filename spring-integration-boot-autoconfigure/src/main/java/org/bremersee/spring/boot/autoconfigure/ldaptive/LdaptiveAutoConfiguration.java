/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.spring.boot.autoconfigure.ldaptive;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.ldaptive.LdaptiveOperations;
import org.bremersee.ldaptive.LdaptiveProperties;
import org.bremersee.ldaptive.LdaptiveProperties.ConnectionPoolProperties;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.ldaptive.reactive.ReactiveLdaptiveOperations;
import org.bremersee.ldaptive.reactive.ReactiveLdaptiveTemplate;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.PooledConnectionFactory;
import org.ldaptive.pool.IdlePruneStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

/**
 * The ldaptive configuration.
 *
 * @author Christian Bremer
 */
@AutoConfiguration
@ConditionalOnClass(name = {
    "org.ldaptive.ConnectionFactory",
    "org.bremersee.ldaptive.LdaptiveProperties"
})
@ConditionalOnProperty(prefix = "bremersee.ldaptive", name = "ldap-url")
@EnableConfigurationProperties(LdaptiveConnectionProperties.class)
@Slf4j
public class LdaptiveAutoConfiguration {

  private final Class<? extends LdaptiveTemplate> ldaptiveTemplateClass;

  private final LdaptiveProperties properties;

  /**
   * Instantiates a new ldaptive configuration.
   *
   * @param properties the ldaptive connection properties
   */
  public LdaptiveAutoConfiguration(LdaptiveConnectionProperties properties) {
    Class<?> cls = Objects
        .requireNonNullElse(properties.getLdaptiveTemplateClass(), LdaptiveTemplate.class);
    //noinspection unchecked
    this.ldaptiveTemplateClass = LdaptiveTemplate.class.isAssignableFrom(cls)
        ? (Class<? extends LdaptiveTemplate>) cls
        : LdaptiveTemplate.class;
    this.properties = PropertiesMapper.INSTANCE.map(properties);
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
   * Creates connection config.
   *
   * @return the connection config
   */
  @ConditionalOnMissingBean(ConnectionConfig.class)
  @Bean
  public ConnectionConfig connectionConfig() {
    return properties.createConnectionConfig();
  }

  /**
   * Creates connection factory bean.
   *
   * @param connectionConfig the connection config
   * @return the connection factory bean
   */
  @ConditionalOnMissingBean(ConnectionFactory.class)
  @Bean(destroyMethod = "close")
  public ConnectionFactory connectionFactory(ConnectionConfig connectionConfig) {

    if (properties.isPooled()) {
      ConnectionPoolProperties properties = this.properties.getConnectionPool();
      PooledConnectionFactory factory = PooledConnectionFactory.builder()
          .config(connectionConfig)
          .blockWaitTime(properties.getBlockWaitTime())
          .connectOnCreate(properties.isConnectOnCreate())
          .failFastInitialize(properties.isFailFastInitialize())
          .max(properties.getMaxPoolSize())
          .min(properties.getMinPoolSize())
          .pruneStrategy(
              new IdlePruneStrategy(properties.getPrunePeriod(), properties.getIdleTime()))
          .validateOnCheckIn(properties.isValidateOnCheckIn())
          .validateOnCheckOut(properties.isValidateOnCheckOut())
          .validatePeriodically(properties.isValidatePeriodically())
          .validator(properties.getValidator().createConnectionValidator())
          .build();
      factory.initialize();
      return factory;
    }
    return new DefaultConnectionFactory(connectionConfig);
  }

  /**
   * Builds ldaptive template.
   *
   * @param connectionFactory the connection factory
   * @return the ldaptive template
   */
  @ConditionalOnMissingBean(LdaptiveOperations.class)
  @Bean
  public LdaptiveTemplate ldaptiveTemplate(ConnectionFactory connectionFactory) {
    try {
      return ldaptiveTemplateClass
          .getConstructor(ConnectionFactory.class)
          .newInstance(connectionFactory);
    } catch (Exception e) {
      log.warn("Could not instantiate LdaptiveTemplate {}. Instantiate default template.",
          ldaptiveTemplateClass.getName(), e);
      return new LdaptiveTemplate(connectionFactory);
    }
  }

  /**
   * Builds reactive ldaptive template.
   *
   * @param connectionFactory the connection factory
   * @return the reactive ldaptive template
   */
  @ConditionalOnClass(name = {"reactor.core.publisher.Mono"})
  @ConditionalOnMissingBean(ReactiveLdaptiveOperations.class)
  @Bean
  public ReactiveLdaptiveTemplate reactiveLdaptiveTemplate(ConnectionFactory connectionFactory) {
    return new ReactiveLdaptiveTemplate(connectionFactory);
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
     * Map ldaptive properties.
     *
     * @param properties the properties
     * @return the ldaptive properties
     */
    LdaptiveProperties map(LdaptiveConnectionProperties properties);
  }

}
