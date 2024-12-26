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

package org.bremersee.spring.boot.autoconfigure.data.commons;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.comparator.model.SortOrderTextSeparators;
import org.bremersee.comparator.spring.converter.SortOrderConverter;
import org.bremersee.comparator.spring.converter.SortOrderItemConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * The sort order converter autoconfiguration.
 *
 * @author Christian Bremer
 */
@ConditionalOnClass(name = {
    "org.bremersee.comparator.spring.converter.SortOrderConverter",
    "org.bremersee.comparator.spring.converter.SortOrderItemConverter"
})
@AutoConfiguration
@EnableConfigurationProperties(SortOrderConverterProperties.class)
@Slf4j
public class SortOrderConverterAutoConfiguration {

  private final SortOrderTextSeparators separators;

  /**
   * Instantiates a new sort order converter autoconfiguration.
   *
   * @param properties the properties
   */
  public SortOrderConverterAutoConfiguration(SortOrderConverterProperties properties) {
    SortOrderTextSeparators defaults = SortOrderTextSeparators.defaults();
    this.separators = SortOrderTextSeparators.builder()
        .argumentSeparator(Optional.ofNullable(properties.getArgumentSeparator())
            .filter(StringUtils::hasText)
            .orElse(defaults.getArgumentSeparator()))
        .chainSeparator(Optional.ofNullable(properties.getChainSeparator())
            .filter(StringUtils::hasText)
            .orElse(defaults.getChainSeparator()))
        .build();
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""
            
            *********************************************************************************
            * {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName());
  }

  /**
   * Creates sort order converter.
   *
   * @return the sort order converter
   */
  @ConditionalOnMissingBean
  @Bean
  public SortOrderConverter sortOrderConverter() {
    return new SortOrderConverter(separators);
  }

  /**
   * Creates sort order item converter.
   *
   * @return the sort order item converter
   */
  @ConditionalOnMissingBean
  @Bean
  public SortOrderItemConverter sortOrderItemConverter() {
    return new SortOrderItemConverter(separators);
  }

}
