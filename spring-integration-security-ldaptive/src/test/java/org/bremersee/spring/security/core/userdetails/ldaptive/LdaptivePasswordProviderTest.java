package org.bremersee.spring.security.core.userdetails.ldaptive;

import static org.mockito.Mockito.mock;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapEntry;

/**
 * The type Ldaptive password provider test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class LdaptivePasswordProviderTest {

  /**
   * Invalid.
   *
   * @param softly the softly
   */
  @Test
  void invalid(SoftAssertions softly) {
    softly
        .assertThat(LdaptivePasswordProvider.invalid().getPassword(mock(LdapEntry.class)))
        .startsWith(LdaptivePasswordProvider.INVALID);
    softly
        .assertThat(LdaptivePasswordProvider.invalid().getPassword(mock(LdapEntry.class), ""))
        .startsWith(LdaptivePasswordProvider.INVALID);
  }
}