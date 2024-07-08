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

package org.bremersee.spring.boot.autoconfigure.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.web.cors.CorsConfiguration;

/**
 * The cors properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.web.cors")
@Data
public class CorsProperties {

  private List<UrlBasedCorsConfiguration> configurations = new ArrayList<>();

  /**
   * To cors configurations map.
   *
   * @return the map
   */
  public Map<String, CorsConfiguration> toCorsConfigurations() {
    if (getConfigurations().isEmpty()) {
      return Map.of("/**", new CorsConfiguration().applyPermitDefaultValues());
    }
    return getConfigurations().stream()
        .collect(Collectors.toMap(
            UrlBasedCorsConfiguration::getPath,
            UrlBasedCorsConfiguration::getConfiguration,
            CorsConfiguration::combine));
  }

  /**
   * Theurl based cors configuration.
   */
  @Data
  public static class UrlBasedCorsConfiguration {

    /**
     * The path.
     */
    private String path;

    /**
     * The configuration for the path.
     */
    @NestedConfigurationProperty
    private CorsConfiguration configuration = new CorsConfiguration();
  }

}
