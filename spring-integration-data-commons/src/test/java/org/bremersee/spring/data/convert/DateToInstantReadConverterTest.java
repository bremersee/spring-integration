package org.bremersee.spring.data.convert;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Date;
import org.junit.jupiter.api.Test;

class DateToInstantReadConverterTest {

  @Test
  void convert() {
    Date date = new Date();
    Instant instant = new DateToInstantReadConverter().convert(date);
    assertThat(instant)
        .isEqualTo(date.toInstant());
  }

}