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

package org.bremersee.spring.data.convert;


import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Objects;
import lombok.ToString;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/**
 * The offset date time to data write converter.
 *
 * @author Christian Bremer
 */
@WritingConverter
@ToString
public class OffsetDateTimeToDateWriteConverter implements Converter<OffsetDateTime, Date> {

  @Override
  public Date convert(OffsetDateTime offsetDateTime) {
    return Date.from(offsetDateTime.toInstant());
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
