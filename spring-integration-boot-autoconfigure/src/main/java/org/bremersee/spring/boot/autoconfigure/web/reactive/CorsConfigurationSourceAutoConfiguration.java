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

package org.bremersee.spring.boot.autoconfigure.web.reactive;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.spring.boot.autoconfigure.web.CorsProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * The reactive cors configuration source auto-configuration.
 *
 * @author Christian Bremer
 */
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnClass(name = {
    "org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource"
})
@EnableConfigurationProperties(CorsProperties.class)
@AutoConfiguration
@Slf4j
public class CorsConfigurationSourceAutoConfiguration {

  private final CorsProperties corsProperties;

  /**
   * Instantiates a new Cors configuration source autoconfiguration.
   *
   * @param corsProperties the cors properties
   */
  public CorsConfigurationSourceAutoConfiguration(CorsProperties corsProperties) {
    this.corsProperties = corsProperties;
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
        corsProperties);
  }

  /**
   * Creates cors configuration source.
   *
   * @return the cors configuration source
   */
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.setCorsConfigurations(corsProperties.toCorsConfigurations());
    return source;
  }
}
