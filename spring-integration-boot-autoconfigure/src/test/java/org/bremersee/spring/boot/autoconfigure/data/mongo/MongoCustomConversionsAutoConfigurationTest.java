package org.bremersee.spring.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The type Mongo custom conversions auto configuration test.
 */
class MongoCustomConversionsAutoConfigurationTest {

  /**
   * The Target.
   */
  MongoCustomConversionsAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    MongoProperties properties = new MongoProperties();
    target = new MongoCustomConversionsAutoConfiguration(properties);
    target.init();
  }

  /**
   * Mongo custom conversions filter.
   */
  @Test
  void mongoCustomConversionsFilter() {
    assertThat(target.mongoCustomConversionsFilter())
        .isNotNull();
  }

  /**
   * Custom conversions.
   */
  @Test
  void customConversions() {
    assertThat(target.customConversions(target.mongoCustomConversionsFilter(), List.of()))
        .isNotNull();
  }
}