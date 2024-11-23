package org.bremersee.spring.security.ldaptive.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.ldaptive.LdapEntry;

/**
 * The ldaptive remember-me token provider test.
 */
class LdaptiveRememberMeTokenProviderTest {

  /**
   * Invalid.
   */
  @Test
  void invalid() {
    assertThat(LdaptiveRememberMeTokenProvider.invalid().getRememberMeToken(mock(LdapEntry.class)))
        .isNotEmpty();
  }
}