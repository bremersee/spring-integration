/*
 * Copyright 2024 the original author or authors.
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

package org.bremersee.spring.security.ldaptive.authentication;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.core.EmailToUsernameResolver;
import org.bremersee.spring.security.ldaptive.app.TestConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.TestSocketUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * The ldaptive authentication manager spring boot test.
 *
 * @author Christian Bremer
 */
@SpringBootTest(
    classes = {TestConfiguration.class},
    webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.web-application-type=servlet",
        "spring.ldap.embedded.base-dn=dc=bremersee,dc=org",
        "spring.ldap.embedded.credential.username=uid=admin",
        "spring.ldap.embedded.credential.password=secret",
        "spring.ldap.embedded.ldif=classpath:schema.ldif",
        "spring.ldap.embedded.validation.enabled=false"
    })
@ExtendWith({SoftAssertionsExtension.class})
class LdaptiveAuthenticationManagerSpringBootTest {

  @Autowired
  private LdaptiveAuthenticationManager authenticationManager;

  @Autowired
  private EmailToUsernameResolver emailToUsernameResolver;

  @LocalServerPort
  private int port;

  /**
   * Sets embedded ldap port.
   */
  @BeforeAll
  static void setEmbeddedLdapPort() {
    int embeddedLdapPort = TestSocketUtils.findAvailableTcpPort();
    System.setProperty("spring.ldap.embedded.port", String.valueOf(embeddedLdapPort));
  }

  /**
   * Authenticate.
   *
   * @param softly the softly
   */
  @Test
  void authenticate(SoftAssertions softly) {

    // wrong password: authentication fails
    softly
        .assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken("anna", "secret")));

    // authenticate successfully
    LdaptiveAuthentication authenticationToken = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(
            "anna.livia@bremersee.org", "topsecret"));

    softly
        .assertThat(authenticationToken.isAuthenticated())
        .isTrue();
    softly
        .assertThat(authenticationToken.getName())
        .isEqualTo("anna");
    softly
        .assertThat(authenticationToken.getPrincipal().getName())
        .isEqualTo("anna");
    softly
        .assertThat(authenticationToken.getPrincipal().getEmail())
        .isEqualTo("anna.livia@bremersee.org");
    softly
        .assertThat(authenticationToken.getPrincipal().getFirstName())
        .isEqualTo("Anna Livia");
    softly
        .assertThat(authenticationToken.getPrincipal().getLastName())
        .isEqualTo("Plurabelle");
    softly
        .assertThat(authenticationToken.getPrincipal().getDn())
        .isEqualTo("uid=anna,ou=people,dc=bremersee,dc=org");
    List<GrantedAuthority> actual = new ArrayList<>(authenticationToken.getAuthorities());
    List<GrantedAuthority> expectedRoles = List.of(
        new SimpleGrantedAuthority("ROLE_developers"),
        new SimpleGrantedAuthority("ROLE_managers"));
    softly
        .assertThat(actual)
        .containsExactlyInAnyOrderElementsOf(expectedRoles);
  }

  /**
   * Say hello endpoint.
   *
   * @param softly the softly
   */
  @Test
  void sayHelloEndpoint(SoftAssertions softly) {
    softly
        .assertThatExceptionOfType(HttpClientErrorException.class)
        .isThrownBy(() -> new RestTemplate()
            .getForObject("http://localhost:" + port + "/hello", String.class))
        .extracting(RestClientResponseException::getStatusCode)
        .isEqualTo(HttpStatus.UNAUTHORIZED);

    // get again
    String helloResponse = WebClient.builder()
        .baseUrl("http://localhost:" + port)
        .filter(ExchangeFilterFunctions.basicAuthentication("hans", "topsecret"))
        .build()
        .get()
        .uri("/hello")
        .exchangeToMono(res -> res.bodyToMono(String.class))
        .block();
    softly
        .assertThat(helloResponse)
        .isEqualTo("Hello!");
  }

  /**
   * Email to username.
   *
   * @param softly the softly
   */
  @Test
  void emailToUsername(SoftAssertions softly) {
    softly
        .assertThat(emailToUsernameResolver.getUsernameByEmail("anna.livia@bremersee.org"))
        .hasValue("anna");
    softly
        .assertThat(emailToUsernameResolver.getUsernameByEmail("anna"))
        .isEmpty();
  }

}
