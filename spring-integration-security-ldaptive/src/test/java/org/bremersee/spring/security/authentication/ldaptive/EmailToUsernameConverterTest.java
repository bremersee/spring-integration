package org.bremersee.spring.security.authentication.ldaptive;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * The type Email to username converter test.
 */
class EmailToUsernameConverterTest {

  /**
   * The Target.
   */
  EmailToUsernameConverter target = Mockito.spy(EmailToUsernameConverter.class);

  /**
   * Is valid email.
   */
  @Test
  void isValidEmail() {
    Assertions.assertThat(target.isValidEmail("foo.bar@example.com")).isTrue();
  }

  /**
   * Is not valid email.
   */
  @Test
  void isNotValidEmail() {
    Assertions.assertThat(target.isValidEmail("foobar_example.com")).isFalse();
  }
}