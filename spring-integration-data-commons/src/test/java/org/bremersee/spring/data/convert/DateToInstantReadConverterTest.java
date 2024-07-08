package org.bremersee.spring.data.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * The type Date to instant read converter test.
 */
class DateToInstantReadConverterTest {

  /**
   * Convert.
   */
  @Test
  void convert() {
    Date date = new Date();
    Instant instant = new DateToInstantReadConverter().convert(date);
    assertThat(instant)
        .isEqualTo(date.toInstant());
  }

}