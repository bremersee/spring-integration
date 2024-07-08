package org.bremersee.spring.security.authentication;

import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * The type Authentication converter test.
 */
class AuthenticationConverterTest {

  /**
   * Normalize.
   *
   * @param transformation the transformation
   */
  @ParameterizedTest
  @ValueSource(strings = {"TO_UPPER_CASE", "TO_LOWER_CASE"})
  void normalize(String transformation) {
    CaseTransformation caseTransformation = CaseTransformation.valueOf(transformation);
    AuthenticationConverterImpl target = new AuthenticationConverterImpl(
        List.of("ROLE_LDAP"),
        Map.of("foo", "ROLE_BAR"),
        "ROLE_",
        caseTransformation,
        Map.of("[-]", "_")
    );
    Set<String> authorities = Set.of("junit-developers", "foo");
    Set<GrantedAuthority> actual = target.normalize(authorities);
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

  /**
   * The type Authentication converter.
   */
  static class AuthenticationConverterImpl extends AuthenticationConverter<Object, Authentication> {

    /**
     * Instantiates a new Authentication converter.
     *
     * @param defaultRoles the default roles
     * @param roleMapping the role mapping
     * @param rolePrefix the role prefix
     * @param roleCaseTransformation the role case transformation
     * @param roleStringReplacements the role string replacements
     */
    public AuthenticationConverterImpl(
        List<String> defaultRoles,
        Map<String, String> roleMapping,
        String rolePrefix,
        CaseTransformation roleCaseTransformation,
        Map<String, String> roleStringReplacements) {
      super(defaultRoles, roleMapping, rolePrefix, roleCaseTransformation, roleStringReplacements);
    }

    @Override
    public Authentication convert(@NonNull Object source) {
      return mock(Authentication.class);
    }
  }
}