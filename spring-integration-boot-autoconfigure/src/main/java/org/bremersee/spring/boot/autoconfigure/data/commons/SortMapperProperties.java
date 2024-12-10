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

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The type SortMapperProperties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.comparator.sort-mapper")
@Data
public class SortMapperProperties {

  /**
   * Specifies whether null-handling is supported or not. If it is not supported, the mapper maps
   * null-handling to {@code Sort.NullHandling.NATIVE}.
   */
  private boolean nullHandlingSupported;

  /**
   * Specifies how {@code Sort.NullHandling.NATIVE} is mapped. If it is {@code true}, native becomes
   * null is first, otherwise null is last.
   */
  private boolean nativeNullHandlingIsNullIsFirst;

}
