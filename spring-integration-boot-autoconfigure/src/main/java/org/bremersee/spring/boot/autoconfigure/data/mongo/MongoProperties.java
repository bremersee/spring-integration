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

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The mongo properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.data.mongo")
@Data
@NoArgsConstructor
public class MongoProperties {

  /**
   * The custom conversions properties.
   */
  private CustomConversionsProperties customConversions = new CustomConversionsProperties();

  /**
   * The custom conversions properties.
   */
  @Data
  public static class CustomConversionsProperties {

    /**
     * Specifies whether custom conversions should be created as primary bean or not. Default is
     * {@code true}.
     */
    private boolean enable = true;

    /**
     * Specifies whether the converters must be annotated with
     * {@code org.springframework.data.convert.ReadingConverter} and
     * {@code org.springframework.data.convert.WritingConverter}. Default is {@code true}.
     */
    private boolean readWriteAnnotationRequired = true;

    /**
     * Regexes for class names.
     */
    private List<String> allowClassNames = new ArrayList<>();

  }

}
