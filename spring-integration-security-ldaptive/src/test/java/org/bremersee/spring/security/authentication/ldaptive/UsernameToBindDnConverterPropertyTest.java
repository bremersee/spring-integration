package org.bremersee.spring.security.authentication.ldaptive;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.WithDefaults;
import org.bremersee.spring.security.authentication.ldaptive.UsernameToBindDnConverter.ByDomainEmail;
import org.bremersee.spring.security.authentication.ldaptive.UsernameToBindDnConverter.ByUserRdnAttribute;
import org.junit.jupiter.api.Test;

class UsernameToBindDnConverterPropertyTest {

  @Test
  void applyByUserRdnAttribute() {
    UsernameToBindDnConverterProperty target = UsernameToBindDnConverterProperty
        .BY_USER_RDN_ATTRIBUTE;
    LdaptiveAuthenticationProperties properties = new WithDefaults();
    UsernameToBindDnConverter actual = target.apply(properties);
    assertThat(actual.getClass())
        .isEqualTo(ByUserRdnAttribute.class);
  }

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