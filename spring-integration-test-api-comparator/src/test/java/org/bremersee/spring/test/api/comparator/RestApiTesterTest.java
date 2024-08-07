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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorAssertionType.ANNOTATION_MUST_NOT_BE_NULL;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorAssertionType.METHOD_MUST_NOT_BE_NULL;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorAssertionType.SAME_ANNOTATION_ATTRIBUTE_VALUE;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorAssertionType.SAME_ANNOTATION_SIZE;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorAssertionType.SAME_METHOD_SIZE;
import static org.bremersee.spring.test.api.comparator.RestApiComparator.assertSameApi;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorExclusion.exclusionBuilder;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorPath.PathType.ANNOTATION;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorPath.PathType.ATTRIBUTE;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorPath.PathType.CLASS;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorPath.PathType.METHOD;
import static org.bremersee.spring.test.api.comparator.RestApiComparatorPath.pathBuilder;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

/**
 * The rest api tester test.
 *
 * @author Christian Bremer
 */
class RestApiTesterTest {

  /**
   * Compare good apis.
   */
  @Test
  void compareGoodApis() {
    assertSameApi(GoodRestApiOne.class, GoodRestApiTwo.class);
  }

  /**
   * Compare bad apis and expect wrong class annotations.
   */
  @Test
  void compareBadApisAndExpectWrongClassAnnotations() {
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> assertSameApi(BadApis.One.class, BadApis.Two.class));
  }

  /**
   * Compare bad apis and expect wrong size of methods.
   */
  @Test
  void compareBadApisAndExpectWrongSizeOfMethods() {
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> assertSameApi(BadApis.Three.class, BadApis.Four.class));
  }

  /**
   * Compare bad apis and expect wrong methods.
   */
  @Test
  void compareBadApisAndExpectWrongMethods() {
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> assertSameApi(BadApis.Five.class, BadApis.Six.class));
  }

  /**
   * Compare bad apis and expect wrong method parameters.
   */
  @Test
  void compareBadApisAndExpectWrongMethodParameters() {
    assertThatExceptionOfType(AssertionError.class)
        .isThrownBy(() -> assertSameApi(
            new SoftAssertions(),
            true,
            BadApis.Seven.class,
            BadApis.Eight.class));
  }

  /**
   * Compare bad apis but exclude exclude different values.
   */
  @Test
  void compareBadApisButExcludeDifferentValues() {
    SoftAssertions softAssertions = new SoftAssertions();
    assertSameApi(
        softAssertions,
        false,
        BadApis.Three.class,
        BadApis.Four.class,
        exclusionBuilder()
            .path(pathBuilder()
                .add(CLASS, "Four")
                .build())
            .type(SAME_METHOD_SIZE)
            .build(),
        exclusionBuilder()
            .path(pathBuilder()
                .add(CLASS, "Four")
                .add(METHOD, "getExampleModels")
                .build())
            .type(METHOD_MUST_NOT_BE_NULL)
            .build(),
        exclusionBuilder()
            .path(pathBuilder()
                .add(CLASS, "Four")
                .add(METHOD, "updateExampleModel")
                .build())
            .type(METHOD_MUST_NOT_BE_NULL)
            .build(),
        exclusionBuilder()
            .path(pathBuilder()
                .add(CLASS, "Four")
                .add(METHOD, "addExampleModel")
                .build())
            .type(SAME_ANNOTATION_SIZE)
            .build(),
        exclusionBuilder()
            .path(pathBuilder()
                .add(CLASS, "Four")
                .add(METHOD, "addExampleModel")
                .add(ANNOTATION, "PostMapping")
                .build())
            .type(ANNOTATION_MUST_NOT_BE_NULL)
            .build(),
        exclusionBuilder()
            .path(pathBuilder()
                .add(CLASS, "Four")
                .add(METHOD, "getExampleModel")
                .add(ANNOTATION, "ApiResponses")
                .add(ATTRIBUTE, "value")
                .add(ANNOTATION, "ApiResponse")
                .add(ATTRIBUTE, "content")
                .add(ANNOTATION, "Content")
                .add(ATTRIBUTE, "schema")
                .add(ANNOTATION, "Schema")
                .add(ATTRIBUTE, "implementation")
                .build())
            .type(SAME_ANNOTATION_ATTRIBUTE_VALUE)
            .build()
    );
    softAssertions.assertAll();
  }
}