package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The type Jwt converter auto configuration test.
 */
class JwtConverterAutoConfigurationTest {

  /**
   * The Target.
   */
  JwtConverterAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    AuthenticationProperties properties = new AuthenticationProperties();
    target = new JwtConverterAutoConfiguration(properties);
    target.init();
  }

  /**
   * Jwt converter.
   */
  @Test
  void jwtConverter() {
    assertThat(target.jwtConverter())
        .isNotNull();
  }
}