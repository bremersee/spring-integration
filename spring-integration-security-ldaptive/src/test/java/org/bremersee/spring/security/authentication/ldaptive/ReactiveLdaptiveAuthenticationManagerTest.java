package org.bremersee.spring.security.authentication.ldaptive;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

class ReactiveLdaptiveAuthenticationManagerTest {

  @Test
  void initialize() {
    assertThatNoException()
        .isThrownBy(() -> new ReactiveLdaptiveAuthenticationManager(
            mock(LdaptiveAuthenticationManager.class)));
  }

}