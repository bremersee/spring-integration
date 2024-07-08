package org.bremersee.spring.core.convert.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.TimeZone;
import org.junit.jupiter.api.Test;

/**
 * The type String to time zone converter test.
 */
class StringToTimeZoneConverterTest {

  private static final StringToTimeZoneConverter target = new StringToTimeZoneConverter();

  /**
   * Convert.
   */
  @Test
  void convert() {
    assertThat(target.convert("Europe/Berlin"))
        .isEqualTo(TimeZone.getTimeZone("Europe/Berlin"));
  }
}