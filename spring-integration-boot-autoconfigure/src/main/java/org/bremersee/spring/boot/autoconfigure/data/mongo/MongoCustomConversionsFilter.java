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

import static java.util.Objects.isNull;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.util.ClassUtils;

/**
 * The mongo custom conversions filter.
 *
 * @author Christian Bremer
 */
public interface MongoCustomConversionsFilter extends Predicate<Converter<?, ?>> {

  /**
   * The default filter.
   */
  @Slf4j
  class DefaultFilter implements MongoCustomConversionsFilter {

    private final boolean readWriteAnnotationRequired;

    private final Set<Pattern> allowClassNames;

    private final boolean allowAllClassNames;

    /**
     * Instantiates a new default filter.
     *
     * @param readWriteAnnotationRequired the read write annotation required
     * @param allowClassNames the allowed prefixes
     */
    public DefaultFilter(boolean readWriteAnnotationRequired, Collection<String> allowClassNames) {
      this.readWriteAnnotationRequired = readWriteAnnotationRequired;
      this.allowClassNames = Stream.ofNullable(allowClassNames)
          .flatMap(Collection::stream)
          .filter(Objects::nonNull)
          .map(String::trim)
          .filter(prefix -> !prefix.isEmpty())
          .map(Pattern::compile)
          .collect(Collectors.toSet());
      this.allowAllClassNames = this.allowClassNames.isEmpty();
    }

    @Override
    public boolean test(Converter<?, ?> converter) {
      Class<?> cls = ClassUtils.getUserClass(converter);
      String className = cls.getName();
      if (readWriteAnnotationRequired && isNotReadWriteAnnotationPresent(cls)) {
        log.info("Custom mongo conversions: '{}' is ignored, because it is not annotated with "
            + "'@ReadingConverter' or '@WritingConverter'.", className);
        return false;
      }
      if (allowAllClassNames) {
        log.info("Custom mongo conversions: '{}' is added to the registry.", className);
        return true;
      }
      boolean isAllowed = allowClassNames
          .stream()
          .anyMatch(pattern -> pattern.matcher(className).matches());
      if (isAllowed) {
        log.info("Custom mongo conversions: '{}' is added to the registry.", className);
      } else {
        log.info("Custom mongo conversions: '{}' is ignored, because it is not allowed by "
            + "configuration.", className);
      }
      return isAllowed;
    }

    private boolean isNotReadWriteAnnotationPresent(Class<?> cls) {
      return isNull(findAnnotation(cls, ReadingConverter.class))
          && isNull(findAnnotation(cls, WritingConverter.class));
    }

  }

}
