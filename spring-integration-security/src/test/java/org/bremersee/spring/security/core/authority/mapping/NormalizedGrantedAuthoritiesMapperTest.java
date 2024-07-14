package org.bremersee.spring.security.core.authority.mapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * The normalized granted authorities mapper test.
 */
class NormalizedGrantedAuthoritiesMapperTest {

  /**
   * Map authorities.
   *
   * @param transformation the transformation
   */
  @ParameterizedTest
  @ValueSource(strings = {"TO_UPPER_CASE", "TO_LOWER_CASE"})
  void mapAuthorities(String transformation) {
    CaseTransformation caseTransformation = CaseTransformation.valueOf(transformation);
    NormalizedGrantedAuthoritiesMapper target = createTarget(caseTransformation);
    Collection<? extends GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("junit-developers"),
        new SimpleGrantedAuthority("junit-developers"),
        new SimpleGrantedAuthority("foo"));
    Collection<GrantedAuthority> actual = new ArrayList<>(target.mapAuthorities(authorities));
    String normalizedValue = CaseTransformation.TO_UPPER_CASE.equals(caseTransformation)
        ? "JUNIT_DEVELOPERS"
        : "junit_developers";
    Assertions
        .assertThat(actual)
        .containsExactlyInAnyOrderElementsOf(Set.of(
            new SimpleGrantedAuthority("ROLE_" + normalizedValue),
            new SimpleGrantedAuthority("ROLE_BAR"),
            new SimpleGrantedAuthority("ROLE_LDAP")));
  }

  private NormalizedGrantedAuthoritiesMapper createTarget(CaseTransformation caseTransformation) {
    return new NormalizedGrantedAuthoritiesMapper(
        List.of("ROLE_LDAP"),
        Map.of("foo", "ROLE_BAR"),
        "ROLE_",
        caseTransformation,
        Map.of("[-]", "_"));
  }
}