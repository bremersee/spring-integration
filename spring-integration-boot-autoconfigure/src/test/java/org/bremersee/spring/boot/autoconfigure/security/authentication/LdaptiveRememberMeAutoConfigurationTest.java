package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.spring.boot.autoconfigure.security.authentication.AuthenticationProperties.LdaptiveProperties.Template;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationManager;
import org.bremersee.spring.security.ldaptive.userdetails.LdaptiveUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.RememberMeAuthenticationProvider;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;

/**
 * The ldaptive remember me autoconfiguration test.
 */
class LdaptiveRememberMeAutoConfigurationTest {

  private final AuthenticationProperties properties = new AuthenticationProperties();

  private LdaptiveRememberMeAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    properties.getRememberMe().setKey("secret");
    properties.getLdaptive().setUserBaseDn("ou=users,dc=system");

    properties.getLdaptive().setTemplate(Template.OPEN_LDAP);
    target = new LdaptiveRememberMeAutoConfiguration(properties);
    target.init();

    properties.getLdaptive().setTemplate(Template.ACTIVE_DIRECTORY);
    target = new LdaptiveRememberMeAutoConfiguration(properties);
    target.init();
  }

  /**
   * Remember me authentication provider.
   */
  @Test
  void rememberMeAuthenticationProvider() {
    RememberMeAuthenticationProvider actual = target.rememberMeAuthenticationProvider();
    assertThat(actual).isNotNull();
  }

  /**
   * Remember me services.
   */
  @Test
  void rememberMeServices() {
    LdaptiveAuthenticationManager authenticationManager = mock(LdaptiveAuthenticationManager.class);
    LdaptiveUserDetailsService userDetailsService = mock(LdaptiveUserDetailsService.class);
    doReturn(userDetailsService).when(authenticationManager).getUserDetailsService();
    RememberMeServices services = target.rememberMeServices(authenticationManager);
    assertThat(services).isNotNull();
  }

  /**
   * Remember me authentication filter.
   */
  @Test
  void rememberMeAuthenticationFilter() {
    LdaptiveAuthenticationManager authenticationManager = mock(LdaptiveAuthenticationManager.class);
    LdaptiveUserDetailsService userDetailsService = mock(LdaptiveUserDetailsService.class);
    doReturn(userDetailsService).when(authenticationManager).getUserDetailsService();
    RememberMeAuthenticationFilter actual = target.rememberMeAuthenticationFilter(
        authenticationManager, target.rememberMeServices(authenticationManager));
    assertThat(actual).isNotNull();
  }
}