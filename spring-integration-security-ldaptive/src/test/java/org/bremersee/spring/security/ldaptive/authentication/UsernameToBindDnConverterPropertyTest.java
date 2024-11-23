package org.bremersee.spring.security.ldaptive.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationProperties.WithDefaults;
import org.bremersee.spring.security.ldaptive.authentication.UsernameToBindDnConverter.ByDomainEmail;
import org.bremersee.spring.security.ldaptive.authentication.UsernameToBindDnConverter.ByUserRdnAttribute;
import org.junit.jupiter.api.Test;

/**
 * The type Username to bind dn converter property test.
 */
class UsernameToBindDnConverterPropertyTest {

  /**
   * Apply by user rdn attribute.
   */
  @Test
  void applyByUserRdnAttribute() {
    UsernameToBindDnConverterProperty target = UsernameToBindDnConverterProperty
        .BY_USER_RDN_ATTRIBUTE;
    LdaptiveAuthenticationProperties properties = new WithDefaults();
    UsernameToBindDnConverter actual = target.apply(properties);
    assertThat(actual.getClass())
        .isEqualTo(ByUserRdnAttribute.class);
  }

  /**
   * Apply by domain email.
   */
  @Test
  void applyByDomainEmail() {
    UsernameToBindDnConverterProperty target = UsernameToBindDnConverterProperty
        .BY_DOMAIN_EMAIL;
    LdaptiveAuthenticationProperties properties = new WithDefaults();
    UsernameToBindDnConverter actual = target.apply(properties);
    assertThat(actual.getClass())
        .isEqualTo(ByDomainEmail.class);
  }
}