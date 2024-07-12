package org.bremersee.spring.security.authentication.ldaptive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetails;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.RememberMeServices;

/**
 * The ldaptive remember me authentication components test.
 */
class LdaptiveRememberMeAuthenticationComponentsTest {

  private static final String KEY = "secret";

  private static final String USER_BASE_DN = "ou=people,dc=bremersee,dc=org";
  private static final String USER_DN = "uid=junit,ou=people,dc=bremersee,dc=org";

  private LdaptiveAuthenticationManager authenticationManager;

  private RememberMeServices rememberMeServices;

  private LdaptiveRememberMeAuthenticationComponents target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    rememberMeServices = mock(RememberMeServices.class);
    authenticationManager = mock(LdaptiveAuthenticationManager.class);
    LdaptiveUserDetailsService ldaptiveUserDetailsService = mock(LdaptiveUserDetailsService.class);
    doReturn(ldaptiveUserDetailsService)
        .when(authenticationManager)
        .getUserDetailsService();
    target = new LdaptiveRememberMeAuthenticationComponents(
        KEY,
        authenticationManager,
        (key, userDetailsService) -> rememberMeServices);
  }

  /**
   * Gets user details service.
   */
  @Test
  void getUserDetailsService() {
    assertThat(target.getUserDetailsService()).isNotNull();
  }

  /**
   * Gets remember me authentication provider.
   */
  @Test
  void getRememberMeAuthenticationProvider() {
    assertThat(target.getRememberMeAuthenticationProvider()).isNotNull();
  }

  /**
   * Gets remember me authentication filter.
   */
  @Test
  void getRememberMeAuthenticationFilter() {
    assertThat(target.getRememberMeAuthenticationFilter()).isNotNull();
  }

  /**
   * Gets key.
   */
  @Test
  void getKey() {
    assertThat(target.getKey()).isEqualTo(KEY);
  }

  /**
   * Gets authentication manager.
   */
  @Test
  void getAuthenticationManager() {
    assertThat(target.getAuthenticationManager()).isNotNull();
  }

  /**
   * Gets remember me services.
   */
  @Test
  void getRememberMeServices() {
    assertThat(target.getRememberMeServices()).isNotNull();
  }

  /**
   * Auto login.
   */
  @Test
  void autoLogin() {
    RememberMeAuthenticationToken token = mock(RememberMeAuthenticationToken.class);
    doReturn(token)
        .when(rememberMeServices)
        .autoLogin(any(), any());
    Authentication actual = target.getRememberMeServices()
        .autoLogin(mock(HttpServletRequest.class), mock(HttpServletResponse.class));
    assertThat(actual).isEqualTo(token);
  }

  /**
   * Auto login with ldaptive authentication manager.
   */
  @Test
  void autoLoginWithLdaptiveAuthenticationManager() {
    RememberMeAuthenticationToken token = mock(RememberMeAuthenticationToken.class);
    doReturn(token)
        .when(rememberMeServices)
        .autoLogin(any(), any());
    LdaptiveRememberMeAuthenticationToken ldaptiveToken = new LdaptiveRememberMeAuthenticationToken(
        KEY,
        new LdaptiveAuthenticationToken(
            mock(LdaptiveAuthenticationProperties.class),
            createUserDetails()));
    doReturn(ldaptiveToken).when(authenticationManager).authenticate(token);
    Authentication actual = target.getRememberMeServices()
        .autoLogin(mock(HttpServletRequest.class), mock(HttpServletResponse.class));
    assertThat(actual).isEqualTo(ldaptiveToken);
  }

  /**
   * Auto login with authentication exception.
   */
  @Test
  void autoLoginWithAuthenticationException() {
    RememberMeAuthenticationToken token = mock(RememberMeAuthenticationToken.class);
    doReturn(token)
        .when(rememberMeServices)
        .autoLogin(any(), any());
    doThrow(new DisabledException("junit is disabled"))
        .when(authenticationManager)
        .authenticate(token);
    Authentication actual = target.getRememberMeServices()
        .autoLogin(mock(HttpServletRequest.class), mock(HttpServletResponse.class));
    assertThat(actual).isNull();
    verify(rememberMeServices).autoLogin(any(), any());
  }

  /**
   * Login success.
   */
  @Test
  void loginSuccess() {
    target.getRememberMeServices().loginSuccess(
        mock(HttpServletRequest.class),
        mock(HttpServletResponse.class),
        mock(Authentication.class));
    verify(rememberMeServices).loginSuccess(any(), any(), any());
  }

  private LdaptiveUserDetails createUserDetails() {
    return new LdaptiveUserDetails(createUser(), "junit",
        List.of(new SimpleGrantedAuthority("ROLE_USER")),
        "", true, true, true, true);
  }

  private LdapEntry createUser() {
    LdapEntry entry = new LdapEntry();
    entry.setDn(USER_DN);
    entry.addAttributes(LdapAttribute.builder().name("uid").values("junit").build());
    entry.addAttributes(LdapAttribute.builder().name("givenName").values("Test").build());
    entry.addAttributes(LdapAttribute.builder().name("sn").values("User").build());
    entry.addAttributes(LdapAttribute.builder().name("mail").values("junit@example.com").build());
    entry.addAttributes(LdapAttribute.builder()
        .name("memberOf").values("cn=tester," + USER_BASE_DN).build());
    return entry;
  }

}