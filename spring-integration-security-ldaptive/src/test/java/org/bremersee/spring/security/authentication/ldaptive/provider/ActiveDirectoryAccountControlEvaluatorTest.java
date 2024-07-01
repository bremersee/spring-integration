package org.bremersee.spring.security.authentication.ldaptive.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.ldaptive.transcoder.UserAccountControlValueTranscoder;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;

class ActiveDirectoryAccountControlEvaluatorTest {

  private static final ActiveDirectoryAccountControlEvaluator target
      = new ActiveDirectoryAccountControlEvaluator();

  @Test
  void isAccountNonExpired() {
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .build();
    assertThat(target.isAccountNonExpired(user))
        .isTrue();
  }

  @Test
  void isAccountNonLocked() {
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .build();
    assertThat(target.isAccountNonLocked(user))
        .isTrue();
  }

  @Test
  void isCredentialsNonExpired() {
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .build();
    assertThat(target.isCredentialsNonExpired(user))
        .isTrue();
  }

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