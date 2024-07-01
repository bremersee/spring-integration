package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtConverterAutoConfigurationTest {

  JwtConverterAutoConfiguration target;

  @BeforeEach
  void init() {
    AuthenticationProperties properties = new AuthenticationProperties();
    target = new JwtConverterAutoConfiguration(properties);
    target.init();
  }

  @Test
  void jwtConverter() {
    assertThat(target.jwtConverter())
        .isNotNull();
  }
}