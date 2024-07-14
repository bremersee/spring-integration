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

import lombok.extern.slf4j.Slf4j;
import org.bremersee.comparator.model.SortOrdersTextProperties;
import org.bremersee.comparator.spring.converter.SortOrderConverter;
import org.bremersee.comparator.spring.converter.SortOrdersConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

/**
 * The sort order converter autoconfiguration.
 *
 * @author Christian Bremer
 */
@ConditionalOnClass(name = {
    "org.bremersee.comparator.spring.converter.SortOrdersConverter"})
@AutoConfiguration
@Slf4j
public class SortOrderConverterAutoConfiguration {

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
   * @param properties the properties
   * @return the sort order converter
   */
  @ConditionalOnMissingBean
  @Bean
  public SortOrderConverter sortOrderConverter(
      ObjectProvider<SortOrdersTextProperties> properties) {
    return new SortOrderConverter(properties.getIfAvailable(SortOrdersTextProperties::defaults));
  }

  /**
   * Creates sort orders converter.
   *
   * @param properties the properties
   * @return the sort orders converter
   */
  @ConditionalOnMissingBean
  @Bean
  public SortOrdersConverter sortOrdersConverter(
      ObjectProvider<SortOrdersTextProperties> properties) {
    return new SortOrdersConverter(properties.getIfAvailable(SortOrdersTextProperties::defaults));
  }

}
