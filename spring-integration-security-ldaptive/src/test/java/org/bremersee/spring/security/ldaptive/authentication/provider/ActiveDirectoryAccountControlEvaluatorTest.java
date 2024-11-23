package org.bremersee.spring.security.ldaptive.authentication.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.ldaptive.transcoder.UserAccountControlValueTranscoder;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;

/**
 * The type Active directory account control evaluator test.
 */
class ActiveDirectoryAccountControlEvaluatorTest {

  private static final ActiveDirectoryAccountControlEvaluator target
      = new ActiveDirectoryAccountControlEvaluator();

  /**
   * Is account non expired.
   */
  @Test
  void isAccountNonExpired() {
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .build();
    assertThat(target.isAccountNonExpired(user))
        .isTrue();
  }

  /**
   * Is account non-locked.
   */
  @Test
  void isAccountNonLocked() {
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .build();
    assertThat(target.isAccountNonLocked(user))
        .isTrue();
  }

  /**
   * Is credentials non expired.
   */
  @Test
  void isCredentialsNonExpired() {
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .build();
    assertThat(target.isCredentialsNonExpired(user))
        .isTrue();
  }

  /**
   * Is enabled.
   */
  @Test
  void isEnabled() {
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .attributes(LdapAttribute.builder()
            .name(UserAccountControlValueTranscoder.ATTRIBUTE_NAME)
            .values(String.valueOf(UserAccountControlValueTranscoder
                .getUserAccountControlValue(false, null)))
            .build())
        .build();
    assertThat(target.isEnabled(user))
        .isFalse();
  }
}