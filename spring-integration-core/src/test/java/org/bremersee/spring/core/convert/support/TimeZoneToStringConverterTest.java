package org.bremersee.spring.core.convert.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TimeZone;
import org.junit.jupiter.api.Test;

class TimeZoneToStringConverterTest {

  private static final TimeZoneToStringConverter target = new TimeZoneToStringConverter();

  @Test
  void convert() {
    assertThat(target.convert(TimeZone.getTimeZone("Europe/Berlin")))
        .isEqualTo("Europe/Berlin");
  }
}