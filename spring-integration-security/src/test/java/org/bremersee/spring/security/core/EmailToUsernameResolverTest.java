package org.bremersee.spring.security.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

import org.junit.jupiter.api.Test;

/**
 * The email to username resolver test.
 */
class EmailToUsernameResolverTest {

  /**
   * The Target.
   */
  EmailToUsernameResolver target = spy(EmailToUsernameResolver.class);

  /**
   * Is valid email.
   */
  @Test
  void isValidEmail() {
    assertThat(target.isValidEmail("foo.bar@example.com")).isTrue();
  }

  /**
   * Is not valid email.
   */
  @Test
  void isNotValidEmail() {
    assertThat(target.isValidEmail("foobar_example.com")).isFalse();
  }
}