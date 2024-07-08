package org.bremersee.spring.boot.autoconfigure.web.reactive;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.spring.boot.autoconfigure.web.CorsProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The type Cors configuration source auto configuration test.
 */
class CorsConfigurationSourceAutoConfigurationTest {

  /**
   * The Target.
   */
  CorsConfigurationSourceAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    CorsProperties properties = new CorsProperties();
    target = new CorsConfigurationSourceAutoConfiguration(properties);
    target.init();
  }

  /**
   * Cors configuration source.
   */
  @Test
  void corsConfigurationSource() {
    assertThat(target.corsConfigurationSource())
        .isNotNull();
  }
}