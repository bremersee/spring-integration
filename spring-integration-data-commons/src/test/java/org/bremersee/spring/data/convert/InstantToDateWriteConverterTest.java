package org.bremersee.spring.data.convert;

import java.time.Instant;
import java.util.Date;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class InstantToDateWriteConverterTest {

  @Test
  void convert() {
    Instant instant = Instant.now();
    Date date = new InstantToDateWriteConverter().convert(instant);
    Assertions.assertThat(date).isEqualTo(Date.from(instant));
  }
}