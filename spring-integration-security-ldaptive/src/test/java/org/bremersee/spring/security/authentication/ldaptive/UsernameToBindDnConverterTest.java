package org.bremersee.spring.security.authentication.ldaptive;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.WithDefaults;
import org.bremersee.spring.security.authentication.ldaptive.UsernameToBindDnConverter.ByDomainEmail;
import org.bremersee.spring.security.authentication.ldaptive.UsernameToBindDnConverter.ByUserRdnAttribute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({SoftAssertionsExtension.class})
class UsernameToBindDnConverterTest {

  @Test
  void convertByUserRdnAttribute(SoftAssertions softly) {
    LdaptiveAuthenticationProperties properties = new WithDefaults();
    properties.setUserRdnAttribute("cn");

    UsernameToBindDnConverter target = new ByUserRdnAttribute(properties);

    softly
        .assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> target.convert("junit"));

    properties.setUserBaseDn("cn=users,dc=example,dc=org");
    String actual = target.convert("junit");
    softly
        .assertThat(actual)
        .isEqualTo("cn=junit,cn=users,dc=example,dc=org");
  }

  @Test
  void convertByDomainEmail(SoftAssertions softly) {
    LdaptiveAuthenticationProperties properties = new WithDefaults();

    UsernameToBindDnConverter target = new ByDomainEmail(properties);

    softly
        .assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> target.convert("junit"));

    properties.setUserBaseDn("cn=users,dc=example,dc=org");
    String actual = target.convert("junit");
    softly
        .assertThat(actual)
        .isEqualTo("junit@example.org");
  }
}