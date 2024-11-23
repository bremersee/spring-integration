package org.bremersee.spring.security.ldaptive.authentication;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

/**
 * The type Reactive ldaptive authentication manager test.
 */
class ReactiveLdaptiveAuthenticationManagerTest {

  /**
   * Initialize.
   */
  @Test
  void initialize() {
    assertThatNoException()
        .isThrownBy(() -> new ReactiveLdaptiveAuthenticationManager(
            mock(LdaptiveAuthenticationManager.class)));
  }

}