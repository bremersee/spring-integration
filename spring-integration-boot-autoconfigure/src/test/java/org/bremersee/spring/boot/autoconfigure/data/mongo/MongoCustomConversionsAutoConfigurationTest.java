package org.bremersee.spring.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MongoCustomConversionsAutoConfigurationTest {

  MongoCustomConversionsAutoConfiguration target;

  @BeforeEach
  void init() {
    MongoProperties properties = new MongoProperties();
    target = new MongoCustomConversionsAutoConfiguration(properties);
    target.init();
  }

  @Test
  void mongoCustomConversionsFilter() {
    assertThat(target.mongoCustomConversionsFilter())
        .isNotNull();
  }

  @Test
  void customConversions() {
    assertThat(target.customConversions(target.mongoCustomConversionsFilter(), List.of()))
        .isNotNull();
  }
}