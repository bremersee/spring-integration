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

package org.bremersee.spring.data.mongodb.core.convert;

import java.util.List;
import org.bremersee.spring.data.convert.DateToInstantReadConverter;
import org.bremersee.spring.data.convert.DateToOffsetDateTimeReadConverter;
import org.bremersee.spring.data.convert.InstantToDateWriteConverter;
import org.bremersee.spring.data.convert.LocaleReadConverter;
import org.bremersee.spring.data.convert.LocaleWriteConverter;
import org.bremersee.spring.data.convert.OffsetDateTimeToDateWriteConverter;
import org.bremersee.spring.data.convert.TimeZoneReadConverter;
import org.bremersee.spring.data.convert.TimeZoneWriteConverter;
import org.springframework.core.convert.converter.Converter;

/**
 * The type DefaultCustomConversions.
 *
 * @author Christian Bremer
 */
public class DefaultCustomConversions implements MongoCustomConversionsProvider {

  @Override
  public List<Converter<?, ?>> getCustomConversions() {
    return List.of(
        new DateToInstantReadConverter(),
        new DateToOffsetDateTimeReadConverter(),
        new InstantToDateWriteConverter(),
        new LocaleReadConverter(),
        new LocaleWriteConverter(),
        new OffsetDateTimeToDateWriteConverter(),
        new TimeZoneReadConverter(),
        new TimeZoneWriteConverter()
    );
  }
}
