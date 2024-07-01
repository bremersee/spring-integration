/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.spring.security.authentication;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The json path jwt converter test.
 *
 * @author Christian Bremer
 */
@ExtendWith({SoftAssertionsExtension.class})
class JsonPathJwtConverterTest {

  /**
   * Convert jwt.
   */
  @Test
  void convertWithScopes(SoftAssertions softly) {
    JsonPathJwtConverter converter = JsonPathJwtConverter.builder()
        .firstNameJsonPath("$.given_name")
        .lastNameJsonPath("$.family_name")
        .emailJsonPath("$.email")
        .rolesJsonPath("$.scope")
        .rolesValueList(false)
        .rolesValueSeparator(" ")
        .rolePrefix("ROLE_")
        .roleCaseTransformation(CaseTransformation.TO_UPPER_CASE)
        .defaultRoles(List.of("ROLE_USER"))
        .build();
    Jwt jwt = createJwt();
    NormalizedJwtAuthenticationToken actual = converter.convert(jwt);
    softly
        .assertThat(actual.getName())
        .isEqualTo("livia");
    softly
        .assertThat(actual.getFirstName())
        .isEqualTo("Anna Livia");
    softly
        .assertThat(actual.getLastName())
        .isEqualTo("Plurabelle");
    softly
        .assertThat(actual.getMail())
        .isEqualTo("anna@example.org");
    softly
        .assertThat(actual.getAuthorities())
        .containsExactlyInAnyOrder(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_EMAIL"),
            new SimpleGrantedAuthority("ROLE_PROFILE"));
  }

  @Test
  void convertWithRoles(SoftAssertions softly) {
    JsonPathJwtConverter converter = JsonPathJwtConverter.builder()
        .nameJsonPath("$.preferred_username")
        .lastNameJsonPath("$.family_name")
        .emailJsonPath("$.email")
        .rolesJsonPath("$.realm_access.roles")
        .rolesValueList(true)
        .rolePrefix("ROLE_")
        .roleCaseTransformation(CaseTransformation.NONE)
        .defaultRoles(List.of("ROLE_USER"))
        .build();
    Jwt jwt = createJwt();
    NormalizedJwtAuthenticationToken actual = converter.convert(jwt);
    softly
        .assertThat(actual.getName())
        .isEqualTo("anna");
    softly
        .assertThat(actual.getFirstName())
        .isNull();
    softly
        .assertThat(actual.getLastName())
        .isEqualTo("Plurabelle");
    softly
        .assertThat(actual.getMail())
        .isEqualTo("anna@example.org");
    softly
        .assertThat(actual.getAuthorities())
        .containsExactlyInAnyOrder(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_LOCAL_USER"));
  }

  private static Jwt createJwt() {
    Map<String, Object> headers = new HashMap<>();
    headers.put("alg", "RS256");
    headers.put("kid", "-V5Nzr9xllRiyURYK-t6pB7C-5E8IKm8-eAPFLVvCt4");
    headers.put("typ", "JWT");

    Date exp = new Date(System.currentTimeMillis() + 1000L * 60L * 15L);
    Date iat = new Date();
    Builder builder = new Builder();
    builder.audience("account");
    builder.expirationTime(exp);
    builder.issuer("https://openid.dev.bremersee.org/auth/realms/omnia");
    builder.issueTime(iat);
    builder.jwtID("48502385-7f37-47da-abc9-a223b8972795");
    builder.notBeforeTime(iat);
    builder.subject("livia");

    builder.claim("scope", "email profile");
    builder.claim("email_verified", false);
    builder.claim("name", "Anna Livia Plurabelle");
    builder.claim("preferred_username", "anna");
    builder.claim("given_name", "Anna Livia");
    builder.claim("family_name", "Plurabelle");
    builder.claim("email", "anna@example.org");

    Map<String, Object> roles = new LinkedHashMap<>();
    roles.put("roles", Collections.singletonList("LOCAL_USER"));
    builder.claim("realm_access", roles);

    JWTClaimsSet claimsSet = builder.build();
    return new Jwt(
        "an-access-token",
        iat.toInstant(),
        exp.toInstant(),
        headers,
        claimsSet.getClaims());
  }
}