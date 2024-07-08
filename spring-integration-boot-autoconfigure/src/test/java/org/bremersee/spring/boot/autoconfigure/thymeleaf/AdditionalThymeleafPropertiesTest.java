/*
 * Copyright 2020 the original author or authors.
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

package org.bremersee.spring.boot.autoconfigure.thymeleaf;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.boot.autoconfigure.thymeleaf.AdditionalThymeleafProperties.ResolverProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The type Additional thymeleaf properties test.
 *
 * @author Christian Bremer
 */
@ExtendWith(SoftAssertionsExtension.class)
class AdditionalThymeleafPropertiesTest {

  /**
   * Gets resolvers.
   *
   * @param softly the softly
   */
  @Test
  void getResolvers(SoftAssertions softly) {
    ResolverProperties rp0 = new ResolverProperties();
    rp0.setName("rp0");
    ResolverProperties rp1 = new ResolverProperties();
    rp1.setName("rp1");

    AdditionalThymeleafProperties expected = new AdditionalThymeleafProperties();
    expected.getResolvers().add(rp0);
    expected.getResolvers().add(rp1);

    AdditionalThymeleafProperties actual = new AdditionalThymeleafProperties();
    actual.getResolvers().add(rp0);
    actual.getResolvers().add(rp1);

    softly
        .assertThat(actual)
        .isEqualTo(expected);
    softly
        .assertThat(expected.toString())
        .contains("rp0");
    softly
        .assertThat(expected.getResolvers())
        .anyMatch(resolverProperties -> "rp0".equals(resolverProperties.getName()));
  }
}