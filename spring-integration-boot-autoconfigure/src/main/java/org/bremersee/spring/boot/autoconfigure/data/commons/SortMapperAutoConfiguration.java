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
import org.bremersee.comparator.spring.mapper.DefaultSortMapper;
import org.bremersee.comparator.spring.mapper.SortMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

/**
 * The sort mapper auto-configuration.
 *
 * @author Christian Bremer
 */
@ConditionalOnClass(name = {
    "org.bremersee.comparator.spring.mapper.DefaultSortMapper",
    "org.springframework.data.domain.Sort"
})
@AutoConfiguration
@EnableConfigurationProperties(SortMapperProperties.class)
@Slf4j
public class SortMapperAutoConfiguration {

  private final SortMapperProperties properties;

  /**
   * Instantiates a new sort mapper auto-configuration.
   *
   * @param properties the properties
   */
  public SortMapperAutoConfiguration(SortMapperProperties properties) {
    this.properties = properties;
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
   * Creates sort mapper bean.
   *
   * @return the sort mapper
   */
  @ConditionalOnMissingBean(SortMapper.class)
  @Bean
  public SortMapper sortMapper() {
    return new DefaultSortMapper(properties.isNullHandlingSupported(),
        properties.isNativeNullHandlingIsNullIsFirst());
  }

}
