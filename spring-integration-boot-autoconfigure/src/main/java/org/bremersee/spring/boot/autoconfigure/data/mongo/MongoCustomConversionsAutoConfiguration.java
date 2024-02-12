/*
 * Copyright 2022 the original author or authors.
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

package org.bremersee.spring.boot.autoconfigure.data.mongo;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.spring.boot.autoconfigure.data.mongo.MongoCustomConversionsFilter.DefaultFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.util.ClassUtils;

/**
 * The mongo custom conversions' autoconfiguration.
 *
 * @author Christian Bremer
 */
@ConditionalOnProperty(
    name = "bremersee.spring.data.mongo.custom-conversions.enable",
    havingValue = "true",
    matchIfMissing = true)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnClass({MongoCustomConversions.class})
@AutoConfiguration
@Slf4j
public class MongoCustomConversionsAutoConfiguration {

  private final MongoProperties.CustomConversionsProperties properties;

  /**
   * Instantiates a new mongo custom conversions autoconfiguration.
   *
   * @param properties the properties
   */
  public MongoCustomConversionsAutoConfiguration(
      MongoProperties properties) {
    this.properties = properties.getCustomConversions();
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""

            *********************************************************************************
            * {}
            *********************************************************************************
            * properties = {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName(), properties);
  }

  /**
   * Create mongo custom conversions filter bean.
   *
   * @return the mongo custom conversions filter
   */
  @ConditionalOnMissingBean
  @Bean
  public MongoCustomConversionsFilter mongoCustomConversionsFilter() {
    return new DefaultFilter(
        properties.isReadWriteAnnotationRequired(),
        properties.getAllowClassNames());
  }

  /**
   * Create custom conversions bean.
   *
   * @param mongoCustomConversionsFilter the mongo custom conversions filter
   * @param conversionsProviders the configurers
   * @return the mongo custom conversions
   */
  @Primary
  @Bean
  public MongoCustomConversions customConversions(
      MongoCustomConversionsFilter mongoCustomConversionsFilter,
      List<MongoCustomConversionsProvider> conversionsProviders) {

    log.info("Adding mongo custom conversions ...");
    return new MongoCustomConversions(conversionsProviders.stream()
        .map(MongoCustomConversionsProvider::getCustomConversions)
        .filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .filter(mongoCustomConversionsFilter)
        .collect(Collectors.toList()));
  }

}
