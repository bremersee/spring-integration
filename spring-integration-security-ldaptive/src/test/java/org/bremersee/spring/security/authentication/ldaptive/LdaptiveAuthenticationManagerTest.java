package org.bremersee.spring.security.authentication.ldaptive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.ldaptive.DefaultLdaptiveErrorHandler;
import org.bremersee.ldaptive.LdaptiveException;
import org.bremersee.ldaptive.LdaptiveSambaTemplate;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.ldaptive.serializable.SerLdapEntry;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.GroupFetchStrategy;
import org.bremersee.spring.security.authentication.ldaptive.provider.ActiveDirectoryTemplate;
import org.bremersee.spring.security.authentication.ldaptive.provider.GroupContainsUsersTemplate;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.provider.OpenLdapTemplate;
import org.bremersee.spring.security.authentication.ldaptive.provider.UserContainsGroupsTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.LdapException;
import org.ldaptive.ResultCode;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * The type Ldaptive authentication manager test.
 */
@ExtendWith({SoftAssertionsExtension.class})
class LdaptiveAuthenticationManagerTest {

  /**
   * Init.
   */
  @Test
  void init() {
    UserContainsGroupsTemplate properties = new UserContainsGroupsTemplate();
    properties.setPasswordAttribute("userPassword");
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        properties);
    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(target::init);

    target.setLdaptiveTemplateFn(LdaptiveSambaTemplate::new);
    target.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    target.init();
  }

  /**
   * Supports.
   */
  @Test
  void supports() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new OpenLdapTemplate());
    target.init();
    boolean actual = target.supports(UsernamePasswordAuthenticationToken.class);
    assertThat(actual)
        .isTrue();
  }

  /**
   * Authenticate with bind.
   *
   * @param softly the softly
   */
  @Test
  void authenticateWithBind(SoftAssertions softly) {
    ActiveDirectoryTemplate properties = new ActiveDirectoryTemplate();
    properties.setUserBaseDn("cn=users,dc=example,dc=org");
    properties.setFirstNameAttribute("givenName");
    properties.setLastNameAttribute("sn");
    LdaptiveAuthenticationManager target = spy(new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        properties));
    target.setAccountControlEvaluator(new NoAccountControlEvaluator());
    target.setUsernameToBindDnConverter(username -> username);

    LdaptiveTemplate ldaptiveTemplate = spy(new LdaptiveTemplate(target
        .getConnectionFactory("junit", "secret")));
    doReturn(ldaptiveTemplate)
        .when(target)
        .getLdapTemplate(anyString(), anyString());
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .attributes(
            LdapAttribute.builder()
                .name("givenName")
                .values("Junit")
                .build(),
            LdapAttribute.builder()
                .name("sn")
                .values("Tester")
                .build(),
            LdapAttribute.builder()
                .name("mail")
                .values("junit@example.org")
                .build(),
            LdapAttribute.builder()
                .name("memberOf")
                .values("cn=test-group,cn=users,dc=example,dc=org")
                .build())
        .build();
    doReturn(Optional.of(user))
        .when(ldaptiveTemplate)
        .findOne(any());

    LdaptiveAuthentication actual = target.authenticate(
        new UsernamePasswordAuthenticationToken("junit", "secret"));
    softly
        .assertThat(actual.isAuthenticated())
        .isTrue();
    softly
        .assertThat(actual.getName())
        .isEqualTo("junit");
    softly
        .assertThat(actual.getFirstName())
        .isEqualTo("Junit");
    softly
        .assertThat(actual.getLastName())
        .isEqualTo("Tester");
    softly
        .assertThat(actual.getEmail())
        .isEqualTo("junit@example.org");
    List<GrantedAuthority> authorities = new ArrayList<>(actual.getAuthorities());
    softly
        .assertThat(authorities)
        .containsExactlyInAnyOrder(new SimpleGrantedAuthority("test-group"));
  }

  /**
   * Authenticate with user password.
   *
   * @param softly the softly
   */
  @Test
  void authenticateWithUserPassword(SoftAssertions softly) {
    GroupContainsUsersTemplate properties = new GroupContainsUsersTemplate();
    properties.setUsernameAttribute("uid");
    properties.setFirstNameAttribute("givenName");
    properties.setLastNameAttribute("sn");
    properties.setPasswordAttribute("userPassword");
    properties.setUserBaseDn("cn=users,dc=example,dc=org");
    properties.setGroupBaseDn("cn=groups,dc=example,dc=org");
    LdaptiveAuthenticationManager target = spy(new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        properties));
    target.setAccountControlEvaluator(new NoAccountControlEvaluator());
    target.setUsernameToBindDnConverter(username -> username);
    target.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    target.init();

    LdaptiveTemplate ldaptiveTemplate = spy(new LdaptiveTemplate(target
        .getConnectionFactory("junit", "secret")));
    doReturn(ldaptiveTemplate)
        .when(target)
        .getLdapTemplate(anyString(), anyString());
    doReturn(true)
        .when(ldaptiveTemplate)
        .compare(any());

    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .attributes(
            LdapAttribute.builder()
                .name("uid")
                .values("junit")
                .build(),
            LdapAttribute.builder()
                .name("userPassword")
                .values("{noop}secret")
                .build(),
            LdapAttribute.builder()
                .name("givenName")
                .values("Junit")
                .build(),
            LdapAttribute.builder()
                .name("sn")
                .values("Tester")
                .build(),
            LdapAttribute.builder()
                .name("mail")
                .values("junit@example.org")
                .build())
        .build();
    doReturn(Optional.of(user))
        .when(ldaptiveTemplate)
        .findOne(any());

    LdapEntry group = LdapEntry.builder()
        .dn("cn=test-group,cn=groups,dc=example,dc=org")
        .attributes(
            LdapAttribute.builder()
                .name("cn")
                .values("test-group")
                .build())
        .build();
    doReturn(List.of(group))
        .when(ldaptiveTemplate)
        .findAll(any());

    LdaptiveAuthentication actual = target.authenticate(
        new UsernamePasswordAuthenticationToken("junit", "secret"));
    softly
        .assertThat(actual.isAuthenticated())
        .isTrue();
    softly
        .assertThat(actual.getName())
        .isEqualTo("junit");
    softly
        .assertThat(actual.getFirstName())
        .isEqualTo("Junit");
    softly
        .assertThat(actual.getLastName())
        .isEqualTo("Tester");
    softly
        .assertThat(actual.getEmail())
        .isEqualTo("junit@example.org");
    List<GrantedAuthority> authorities = new ArrayList<>(actual.getAuthorities());
    softly
        .assertThat(authorities)
        .containsExactlyInAnyOrder(new SimpleGrantedAuthority("test-group"));
  }

  /**
   * Gets ldaptive template.
   */
  @Test
  void getLdaptiveTemplate() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    target.setUsernameToBindDnConverter(username -> username);
    assertThat(target.getLdapTemplate("junit", "secret"))
        .isNotNull();
  }

  /**
   * Gets user with username not found exception.
   */
  @Test
  void getUserWithUsernameNotFoundException() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    LdaptiveTemplate ldaptiveTemplate = mock(LdaptiveTemplate.class);
    doReturn(Optional.empty())
        .when(ldaptiveTemplate)
        .findOne(any());
    assertThatExceptionOfType(UsernameNotFoundException.class)
        .isThrownBy(() -> target.getUser(ldaptiveTemplate, "junit"));
  }

  /**
   * Gets user with bad credentials exception by result code.
   */
  @Test
  void getUserWithBadCredentialsExceptionByResultCode() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    LdaptiveException ldaptiveException = new DefaultLdaptiveErrorHandler()
        .map(new LdapException(ResultCode.INVALID_CREDENTIALS, "Bad credentials"));
    LdaptiveTemplate ldaptiveTemplate = mock(LdaptiveTemplate.class);
    doThrow(ldaptiveException)
        .when(ldaptiveTemplate)
        .findOne(any());
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> target.getUser(ldaptiveTemplate, "junit"));
  }

  /**
   * Gets user with bad credentials exception by message.
   */
  @Test
  void getUserWithBadCredentialsExceptionByMessage() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    LdaptiveException ldaptiveException = new DefaultLdaptiveErrorHandler()
        .map(new LdapException(ResultCode.CONNECT_ERROR,
            "resultCode=" + ResultCode.INVALID_CREDENTIALS));
    LdaptiveTemplate ldaptiveTemplate = mock(LdaptiveTemplate.class);
    doThrow(ldaptiveException)
        .when(ldaptiveTemplate)
        .findOne(any());
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> target.getUser(ldaptiveTemplate, "junit"));
  }

  /**
   * Gets user with ldaptive exception.
   */
  @Test
  void getUserWithLdaptiveException() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    LdaptiveException ldaptiveException = new DefaultLdaptiveErrorHandler()
        .map(new LdapException(ResultCode.CONNECT_ERROR, "Something went wrong"));
    LdaptiveTemplate ldaptiveTemplate = mock(LdaptiveTemplate.class);
    doThrow(ldaptiveException)
        .when(ldaptiveTemplate)
        .findOne(any());
    assertThatExceptionOfType(LdaptiveException.class)
        .isThrownBy(() -> target.getUser(ldaptiveTemplate, "junit"));
  }

  /**
   * Change password.
   */
  @Test
  void changePassword() {
    LdaptiveAuthenticationManager target = spy(new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate()));
    LdaptiveTemplate ldaptiveTemplate = mock(LdaptiveTemplate.class);
    doReturn(ldaptiveTemplate)
        .when(target)
        .getLdapTemplate(anyString(), anyString());
    LdapEntry user = LdapEntry.builder().dn("cn=junit,cn=users,dc=example,dc=org").build();
    LdaptiveAuthenticationToken token = new LdaptiveAuthenticationToken(
        "junit", List.of(), new SerLdapEntry(user), new LdaptiveAuthenticationProperties());
    doReturn(token)
        .when(target)
        .authenticate("junit", "old", ldaptiveTemplate);
    target.changePassword("junit", "old", "new");
  }

  /**
   * Check password fails.
   */
  @Test
  void checkPasswordFails() {
    OpenLdapTemplate properties = new OpenLdapTemplate();
    properties.setPasswordAttribute("userPassword");
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        properties);
    target.setPasswordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    LdaptiveTemplate ldaptiveTemplate = mock(LdaptiveTemplate.class);
    doReturn(false)
        .when(ldaptiveTemplate)
        .compare(any());
    LdapEntry user = LdapEntry.builder().dn("cn=junit,cn=users,dc=example,dc=org").build();
    assertThatExceptionOfType(BadCredentialsException.class)
        .isThrownBy(() -> target.checkPassword(ldaptiveTemplate, user, "wrong"));
  }

  /**
   * Check account control throws disabled exception.
   */
  @Test
  void checkAccountControlThrowsDisabledException() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    AccountControlEvaluator evaluator = mock(AccountControlEvaluator.class);
    target.setAccountControlEvaluator(evaluator);
    LdapEntry user = LdapEntry.builder().dn("cn=junit,cn=users,dc=example,dc=org").build();
    doReturn(false)
        .when(evaluator)
        .isEnabled(user);
    assertThatExceptionOfType(DisabledException.class)
        .isThrownBy(() -> target.checkAccountControl(user));
  }

  /**
   * Check account control throws locked exception.
   */
  @Test
  void checkAccountControlThrowsLockedException() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    AccountControlEvaluator evaluator = mock(AccountControlEvaluator.class);
    target.setAccountControlEvaluator(evaluator);
    LdapEntry user = LdapEntry.builder().dn("cn=junit,cn=users,dc=example,dc=org").build();
    doReturn(true)
        .when(evaluator)
        .isEnabled(user);
    doReturn(false)
        .when(evaluator)
        .isAccountNonLocked(user);
    assertThatExceptionOfType(LockedException.class)
        .isThrownBy(() -> target.checkAccountControl(user));
  }

  /**
   * Check account control throws account expired exception.
   */
  @Test
  void checkAccountControlThrowsAccountExpiredException() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    AccountControlEvaluator evaluator = mock(AccountControlEvaluator.class);
    target.setAccountControlEvaluator(evaluator);
    LdapEntry user = LdapEntry.builder().dn("cn=junit,cn=users,dc=example,dc=org").build();
    doReturn(true)
        .when(evaluator)
        .isEnabled(user);
    doReturn(true)
        .when(evaluator)
        .isAccountNonLocked(user);
    doReturn(false)
        .when(evaluator)
        .isAccountNonExpired(user);
    assertThatExceptionOfType(AccountExpiredException.class)
        .isThrownBy(() -> target.checkAccountControl(user));
  }

  /**
   * Check account control throws credentials expired exception.
   */
  @Test
  void checkAccountControlThrowsCredentialsExpiredException() {
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        new ActiveDirectoryTemplate());
    AccountControlEvaluator evaluator = mock(AccountControlEvaluator.class);
    target.setAccountControlEvaluator(evaluator);
    LdapEntry user = LdapEntry.builder().dn("cn=junit,cn=users,dc=example,dc=org").build();
    doReturn(true)
        .when(evaluator)
        .isEnabled(user);
    doReturn(true)
        .when(evaluator)
        .isAccountNonLocked(user);
    doReturn(true)
        .when(evaluator)
        .isAccountNonExpired(user);
    doReturn(false)
        .when(evaluator)
        .isCredentialsNonExpired(user);
    assertThatExceptionOfType(CredentialsExpiredException.class)
        .isThrownBy(() -> target.checkAccountControl(user));
  }

  /**
   * Gets authorities.
   */
  @Test
  void getAuthorities() {
    OpenLdapTemplate properties = new OpenLdapTemplate();
    properties.setGroupFetchStrategy(GroupFetchStrategy.NONE);
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        properties);
    LdapEntry user = LdapEntry.builder().dn("cn=junit,cn=users,dc=example,dc=org").build();
    Collection<? extends String> actual = target.getAuthorities(mock(LdaptiveTemplate.class), user);
    assertThat(actual)
        .isEmpty();
  }

  /**
   * Gets authority filter.
   */
  @Test
  void getAuthorityFilter() {
    OpenLdapTemplate properties = new OpenLdapTemplate();
    properties.setUsernameAttribute("uid");
    properties.setGroupObjectClass("group");
    properties.setGroupMemberAttribute("member");
    properties.setGroupMemberFormat("cn=${username},cn=users,dc=example,dc=org");
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        properties);
    LdapEntry user = LdapEntry.builder()
        .dn("cn=junit,cn=users,dc=example,dc=org")
        .attributes(LdapAttribute.builder()
            .name("uid")
            .values("junit")
            .build())
        .build();
    String actual = target.getAuthorityFilter(user);
    assertThat(actual)
        .isEqualTo("(&(objectClass=group)(member=cn=junit,cn=users,dc=example,dc=org))");
  }

  /**
   * Gets authority name.
   */
  @Test
  void getAuthorityName() {
    OpenLdapTemplate properties = new OpenLdapTemplate();
    properties.setGroupIdAttribute(null);
    LdaptiveAuthenticationManager target = new LdaptiveAuthenticationManager(
        new ConnectionConfig("ldap://localhost:389"),
        properties);
    LdapEntry group = LdapEntry.builder().dn("cn=junit,cn=groups,dc=example,dc=org").build();
    String actual = target.getAuthorityName(group);
    assertThat(actual)
        .isEqualTo("junit");
  }

  /**
   * Gets authorities by groups in user.
   */
  @Test
  void getAuthoritiesByGroupsInUser() {
  }

  /**
   * Gets authorities by groups with user.
   */
  @Test
  void getAuthoritiesByGroupsWithUser() {
  }

  /**
   * Gets username.
   */
  @Test
  void getUsername() {
  }
}