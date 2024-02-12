package org.bremersee.spring.core.convert.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class LocaleToStringConverterTest {

  private static final LocaleToStringConverter target = new LocaleToStringConverter();

  @Test
  void convert() {
    assertThat(target.convert(Locale.GERMANY))
        .isEqualTo("de-DE");
  }
}