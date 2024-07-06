package org.bremersee.spring.security.authentication.ldaptive;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class EmailToUsernameConverterTest {

  EmailToUsernameConverter target = Mockito.spy(EmailToUsernameConverter.class);

  @Test
  void isValidEmail() {
    Assertions.assertThat(target.isValidEmail("foo.bar@example.com")).isTrue();
  }

  @Test
  void isNotValidEmail() {
    Assertions.assertThat(target.isValidEmail("foobar_example.com")).isFalse();
  }
}