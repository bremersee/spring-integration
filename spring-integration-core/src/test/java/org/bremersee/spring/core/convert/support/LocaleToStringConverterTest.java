package org.bremersee.spring.core.convert.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * The type Locale to string converter test.
 */
class LocaleToStringConverterTest {

  private static final LocaleToStringConverter target = new LocaleToStringConverter();

  /**
   * Convert.
   */
  @Test
  void convert() {
    assertThat(target.convert(Locale.GERMANY))
        .isEqualTo("de-DE");
  }
}