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
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The sort order converter properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.comparator.converter")
@Data
@NoArgsConstructor
public class SortOrderConverterProperties {

  /**
   * The argument separator between field and direction (for example {@code field;desc}).
   */
  private String argumentSeparator = ";";

  /**
   * The chain separator between the order items (for example {@code field0;desc,field1;asc}).
   */
  private String chainSeparator = ",";

}
