package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.spring.boot.autoconfigure.security.authentication.AuthenticationProperties.LdaptiveProperties.Template;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationManager;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveRememberMeAuthenticationComponents;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetailsService;
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

  private LdaptiveAuthenticationManager authenticationManager;

  private LdaptiveRememberMeAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    properties.setRememberMeKey("secret");
    properties.getLdaptive().setTemplate(Template.ACTIVE_DIRECTORY);
    properties.getLdaptive().setUserBaseDn("ou=users,dc=system");

    authenticationManager = mock(LdaptiveAuthenticationManager.class);

    target = new LdaptiveRememberMeAutoConfiguration(properties);
    target.init();
  }

  /**
   * Ldaptive remember me authentication components.
   */
  @Test
  void ldaptiveRememberMeAuthenticationComponents() {
    LdaptiveUserDetailsService userDetailsService = mock(LdaptiveUserDetailsService.class);
    doReturn(userDetailsService)
        .when(authenticationManager)
        .getUserDetailsService();

    LdaptiveRememberMeAuthenticationComponents actual = target
        .ldaptiveRememberMeAuthenticationComponents(authenticationManager);
    assertThat(actual).isNotNull();
  }

  /**
   * Remember me authentication provider.
   */
  @Test
  void rememberMeAuthenticationProvider() {
    LdaptiveRememberMeAuthenticationComponents components = mock(
        LdaptiveRememberMeAuthenticationComponents.class);
    RememberMeAuthenticationProvider expected = mock(RememberMeAuthenticationProvider.class);
    doReturn(expected).when(components).getRememberMeAuthenticationProvider();
    RememberMeAuthenticationProvider actual = target.rememberMeAuthenticationProvider(components);
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Remember me services.
   */
  @Test
  void rememberMeServices() {
    LdaptiveRememberMeAuthenticationComponents components = mock(
        LdaptiveRememberMeAuthenticationComponents.class);
    RememberMeServices expected = mock(RememberMeServices.class);
    doReturn(expected).when(components).getRememberMeServices();
    RememberMeServices actual = target.rememberMeServices(components);
    assertThat(actual).isEqualTo(expected);
  }

  /**
   * Remember me authentication filter.
   */
  @Test
  void rememberMeAuthenticationFilter() {
    LdaptiveRememberMeAuthenticationComponents components = mock(
        LdaptiveRememberMeAuthenticationComponents.class);
    RememberMeAuthenticationFilter expected = mock(RememberMeAuthenticationFilter.class);
    doReturn(expected).when(components).getRememberMeAuthenticationFilter();
    RememberMeAuthenticationFilter actual = target.rememberMeAuthenticationFilter(components);
    assertThat(actual).isEqualTo(expected);
  }
}