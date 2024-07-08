package org.bremersee.spring.security.authentication.ldaptive;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.authentication.ldaptive.provider.ActiveDirectoryAccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The type Account control evaluator property test.
 */
@ExtendWith({SoftAssertionsExtension.class})
class AccountControlEvaluatorPropertyTest {

  /**
   * Get.
   *
   * @param softly the softly
   */
  @Test
  void get(SoftAssertions softly) {
    softly
        .assertThat(AccountControlEvaluatorProperty.NONE.get().getClass())
        .isEqualTo(NoAccountControlEvaluator.class);
    softly
        .assertThat(AccountControlEvaluatorProperty.ACTIVE_DIRECTORY.get().getClass())
        .isEqualTo(ActiveDirectoryAccountControlEvaluator.class);
  }
}