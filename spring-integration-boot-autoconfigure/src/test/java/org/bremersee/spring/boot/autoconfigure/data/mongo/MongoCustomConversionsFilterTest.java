package org.bremersee.spring.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.bremersee.spring.boot.autoconfigure.data.mongo.MongoCustomConversionsFilter.DefaultFilter;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

class MongoCustomConversionsFilterTest {

  @Test
  void acceptAll() {
    DefaultFilter target = new DefaultFilter(false, List.of());
    assertThat(target.test(mock(Converter.class)))
        .isTrue();
  }

  @Test
  void acceptWithNoAnnotation() {
    DefaultFilter target = new DefaultFilter(true, List.of());
    assertThat(target.test(mock(Converter.class)))
        .isFalse();
  }

  @Test
  void acceptWithWriteConverter() {
    DefaultFilter target = new DefaultFilter(true, List.of());
    assertThat(target.test(new WriteConverter()))
        .isTrue();
  }

  @Test
  void acceptWithReadConverter() {
    DefaultFilter target = new DefaultFilter(true, List.of());
    assertThat(target.test(new ReadConverter()))
        .isTrue();
  }

  @Test
  void acceptNoConverter() {
    DefaultFilter target = new DefaultFilter(false, List.of("^\\."));
    assertThat(target.test(new ReadConverter()))
        .isFalse();
  }

  @Test
  void acceptSpecifiedConverter() {
    DefaultFilter target = new DefaultFilter(false, List.of("^org.bremersee\\..*"));
    assertThat(target.test(new ReadConverter()))
        .isTrue();
  }

  @WritingConverter
  private static class WriteConverter implements Converter<Integer, String> {

    @Override
    public String convert(@NonNull Integer source) {
      return String.valueOf(source);
    }
  }

  @ReadingConverter
  private static class ReadConverter implements Converter<Integer, String> {

    @Override
    public String convert(@NonNull Integer source) {
      return String.valueOf(source);
    }
  }

}