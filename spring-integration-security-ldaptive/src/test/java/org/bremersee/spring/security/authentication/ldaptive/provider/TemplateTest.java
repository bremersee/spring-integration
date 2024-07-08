package org.bremersee.spring.security.authentication.ldaptive.provider;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.WithDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The type Template test.
 */
@ExtendWith({SoftAssertionsExtension.class})
class TemplateTest {

  /**
   * Apply template.
   *
   * @param softly the softly
   */
  @Test
  void applyTemplate(SoftAssertions softly) {
    LdaptiveAuthenticationProperties source = new WithDefaults();
    for (Template t : Template.values()) {
      LdaptiveAuthenticationProperties actual = t.applyTemplate(source);
      softly
          .assertThat(actual)
          .isNotNull();
    }
  }
}