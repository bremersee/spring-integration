package org.bremersee.spring.security.ldaptive.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetails;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * The ldaptive remember me services test.
 */
class LdaptiveTokenBasedRememberMeServicesTest {

  private LdaptiveUserDetailsService userDetailsService;

  private LdaptiveTokenBasedRememberMeServices target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    userDetailsService = mock(LdaptiveUserDetailsService.class);
    LdaptiveAuthenticationManager authenticationManager = mock(LdaptiveAuthenticationManager.class);
    doReturn(userDetailsService)
        .when(authenticationManager)
        .getUserDetailsService();
    target = new LdaptiveTokenBasedRememberMeServices(
        "secret", authenticationManager);
  }

  /**
   * Gets user details service.
   */
  @Test
  void getUserDetailsService() {
    UserDetailsService actual = target.getUserDetailsService();
    assertThat(actual)
        .isEqualTo(userDetailsService);
  }

  /**
   * Create successful authentication.
   */
  @Test
  void createSuccessfulAuthentication() {
    Authentication authentication = target
        .createSuccessfulAuthentication(null, mock(LdaptiveUserDetails.class));
    assertThat(authentication).isInstanceOf(LdaptiveRememberMeAuthenticationToken.class);
  }
}