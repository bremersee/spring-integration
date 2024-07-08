package org.bremersee.spring.data.mongodb.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;
import org.junit.jupiter.api.Test;

/**
 * The type Mongo search language test.
 */
class MongoSearchLanguageTest {

  private static final MongoSearchLanguage target = MongoSearchLanguage.DE;

  /**
   * Test to string.
   */
  @Test
  void testToString() {
    assertThat(target.toString())
        .isEqualTo("de");
  }

  /**
   * To locale.
   */
  @Test
  void toLocale() {
    assertThat(target.toLocale())
        .isEqualTo(Locale.GERMAN);
  }

  /**
   * From value.
   */
  @Test
  void fromValue() {
    assertThat(MongoSearchLanguage.fromValue("de"))
        .isEqualTo(target);
  }

  /**
   * Test from value.
   */
  @Test
  void testFromValue() {
    assertThat(MongoSearchLanguage.fromValue("unknown", target))
        .isEqualTo(target);
  }

  /**
   * From locale.
   */
  @Test
  void fromLocale() {
    assertThat(MongoSearchLanguage.fromLocale(Locale.GERMAN, target))
        .isEqualTo(target);
  }
}