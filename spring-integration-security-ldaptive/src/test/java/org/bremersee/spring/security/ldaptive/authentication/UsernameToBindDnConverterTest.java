package org.bremersee.spring.security.ldaptive.authentication;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationProperties.WithDefaults;
import org.bremersee.spring.security.ldaptive.authentication.UsernameToBindDnConverter.ByDomainEmail;
import org.bremersee.spring.security.ldaptive.authentication.UsernameToBindDnConverter.ByUserRdnAttribute;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * The type Username to bind dn converter test.
 */
@ExtendWith({SoftAssertionsExtension.class})
class UsernameToBindDnConverterTest {

  /**
   * Convert by user rdn attribute.
   *
   * @param softly the softly
   */
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

  /**
   * Convert by domain email.
   *
   * @param softly the softly
   */
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