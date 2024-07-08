package org.bremersee.spring.core.convert.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TimeZone;
import org.junit.jupiter.api.Test;

/**
 * The type Time zone to string converter test.
 */
class TimeZoneToStringConverterTest {

  private static final TimeZoneToStringConverter target = new TimeZoneToStringConverter();

  /**
   * Convert.
   */
  @Test
  void convert() {
    assertThat(target.convert(TimeZone.getTimeZone("Europe/Berlin")))
        .isEqualTo("Europe/Berlin");
  }
}