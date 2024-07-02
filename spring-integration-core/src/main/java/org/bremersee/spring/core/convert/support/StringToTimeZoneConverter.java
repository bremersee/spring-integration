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

package org.bremersee.spring.core.convert.support;

import java.util.Objects;
import java.util.TimeZone;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

/**
 * The string to time zone converter.
 *
 * @author Christian Bremer
 */
public class StringToTimeZoneConverter implements Converter<String, TimeZone> {

  @Override
  public TimeZone convert(@NonNull String source) {
    return TimeZone.getTimeZone(source);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getClass());
  }
}
