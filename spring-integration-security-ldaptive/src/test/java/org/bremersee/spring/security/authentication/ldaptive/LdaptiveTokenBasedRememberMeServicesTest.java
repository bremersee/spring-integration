package org.bremersee.spring.security.authentication.ldaptive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.spring.security.authentication.ldaptive.provider.OpenLdapTemplate;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetails;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * The ldaptive remember me services test.
 */
class LdaptiveTokenBasedRememberMeServicesTest {

  private final LdaptiveAuthenticationProperties properties = new OpenLdapTemplate();

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
        "secret", properties, authenticationManager);
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