package org.bremersee.spring.boot.autoconfigure.web.reactive;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.spring.boot.autoconfigure.web.CorsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CorsConfigurationSourceAutoConfigurationTest {

  CorsConfigurationSourceAutoConfiguration target;

  @BeforeEach
  void init() {
    CorsProperties properties = new CorsProperties();
    target = new CorsConfigurationSourceAutoConfiguration(properties);
    target.init();
  }

  @Test
  void corsConfigurationSource() {
    assertThat(target.corsConfigurationSource())
        .isNotNull();
  }
}