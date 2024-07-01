package org.bremersee.spring.security.authentication.ldaptive;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.ldaptive.serializable.SerLdapEntry;
import org.bremersee.spring.security.authentication.CaseTransformation;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.RoleMapping;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.StringReplacement;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith({SoftAssertionsExtension.class})
class LdaptiveAuthenticationTokenConverterTest {

  @ParameterizedTest
  @ValueSource(strings = {"TO_UPPER_CASE", "TO_LOWER_CASE"})
  void convert(String transformation, SoftAssertions softly) {
    String firstNameAttr = "givenName";
    String lastNameAttr = "sn";
    String emailAttr = "email";

    LdaptiveAuthenticationProperties properties = new LdaptiveAuthenticationProperties();
    properties.setFirstNameAttribute(firstNameAttr);
    properties.setLastNameAttribute(lastNameAttr);
    properties.setEmailAttribute(emailAttr);
    properties.setRolePrefix("ROLE_");
    properties.setRoleCaseTransformation(CaseTransformation.valueOf(transformation));
    properties.setRoleStringReplacements(List.of(new StringReplacement("[-]", "_")));
    properties.setRoleMapping(List.of(new RoleMapping("foo", "ROLE_BAR")));
    properties.setDefaultRoles(List.of("ROLE_LDAP"));

    LdapAttribute email = new LdapAttribute(emailAttr, "test@example.org");
    LdapAttribute firstName = new LdapAttribute(firstNameAttr, "Junit");
    LdapAttribute lastName = new LdapAttribute(lastNameAttr, "Tester");
    LdapEntry ldapEntry = new LdapEntry();
    ldapEntry.addAttributes(email, firstName, lastName);
    Set<String> authorities = Set.of("junit-developers", "foo");

    LdaptiveAuthenticationTokenConverter target
        = new LdaptiveAuthenticationTokenConverter(properties);

    LdaptiveAuthenticationSource source = new LdaptiveAuthenticationSource(
        "junit", authorities, ldapEntry);
    LdaptiveAuthentication actual = requireNonNull(target.convert(source));

    String normalizedValue = CaseTransformation.TO_UPPER_CASE
        .equals(properties.getRoleCaseTransformation())
        ? "JUNIT_DEVELOPERS"
        : "junit_developers";
    List<GrantedAuthority> actualAuthorities = new ArrayList<>(actual.getAuthorities());
    softly
        .assertThat(actualAuthorities)
        .containsExactlyInAnyOrderElementsOf(Set.of(
            new SimpleGrantedAuthority("ROLE_" + normalizedValue),
            new SimpleGrantedAuthority("ROLE_BAR"),
            new SimpleGrantedAuthority("ROLE_LDAP")));
    softly
        .assertThat(actual.getMail())
        .isEqualTo("test@example.org");
    softly
        .assertThat(actual.getName())
        .isEqualTo("junit");
    softly
        .assertThat(actual.getPrincipal())
        .isEqualTo(new SerLdapEntry(ldapEntry));
    softly
        .assertThat(actual.getFirstName())
        .isEqualTo("Junit");
    softly
        .assertThat(actual.getLastName())
        .isEqualTo("Tester");
    softly
        .assertThat(actual.getCredentials())
        .isNull();
  }
}