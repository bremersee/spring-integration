package org.bremersee.spring.data.mongodb.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

class MongoSearchLanguageTest {

  private static final MongoSearchLanguage target = MongoSearchLanguage.DE;

  @Test
  void testToString() {
    assertThat(target.toString())
        .isEqualTo("de");
  }

  @Test
  void toLocale() {
    assertThat(target.toLocale())
        .isEqualTo(Locale.GERMAN);
  }

  @Test
  void fromValue() {
    assertThat(MongoSearchLanguage.fromValue("de"))
        .isEqualTo(target);
  }

  @Test
  void testFromValue() {
    assertThat(MongoSearchLanguage.fromValue("unknown", target))
        .isEqualTo(target);
  }

  @Test
  void fromLocale() {
    assertThat(MongoSearchLanguage.fromLocale(Locale.GERMAN, target))
        .isEqualTo(target);
  }
}