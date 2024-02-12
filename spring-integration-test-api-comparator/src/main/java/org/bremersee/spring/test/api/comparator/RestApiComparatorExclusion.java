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

package org.bremersee.spring.test.api.comparator;

import lombok.Builder;
import lombok.Getter;

/**
 * The rest api tester exclusion.
 *
 * @author Christian Bremer
 */
@Getter
public class RestApiComparatorExclusion {

  /**
   * Checks whether the given path and type are excluded via the given exclusions.
   *
   * @param path the path
   * @param type the type
   * @param exclusions the exclusions
   * @return the boolean
   */
  public static boolean isExcluded(
      RestApiComparatorPath path,
      RestApiComparatorAssertionType type,
      RestApiComparatorExclusion[] exclusions) {

    if (path == null || type == null || exclusions == null) {
      return false;
    }
    for (RestApiComparatorExclusion exclusion : exclusions) {
      if (exclusion.getPath() != null && exclusion.getPath().equals(path)
          && (exclusion.getType() == RestApiComparatorAssertionType.ANY
          || exclusion.getType() == type)) {
        return true;
      }
    }
    return isExcluded(path, type.getParent(), exclusions);
  }

  private final RestApiComparatorPath path;

  private final RestApiComparatorAssertionType type;

  /**
   * Instantiates a new rest api tester exclusion.
   *
   * @param path the path
   * @param type the type
   */
  @Builder(builderMethodName = "exclusionBuilder")
  public RestApiComparatorExclusion(RestApiComparatorPath path,
      RestApiComparatorAssertionType type) {
    this.path = path;
    this.type = type != null ? type : RestApiComparatorAssertionType.ANY;
  }

}
