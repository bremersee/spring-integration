package org.bremersee.spring.security.ldaptive.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.ldaptive.authentication.provider.GroupContainsUsersTemplate;
import org.bremersee.spring.security.ldaptive.authentication.provider.UserContainsGroupsTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * The ldaptive user details service test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class LdaptiveUserDetailsServiceTest {

  private static final String USER_BASE_DN = "ou=people,dc=bremersee,dc=org";
  private static final String USER_DN = "uid=junit,ou=people,dc=bremersee,dc=org";

  private LdaptiveTemplate ldaptiveTemplate;

  /**
   * Init ldaptive user details service.
   *
   * @param properties the properties
   * @return the ldaptive user details service
   */
  LdaptiveUserDetailsService init(LdaptiveAuthenticationProperties properties) {
    ldaptiveTemplate = mock(LdaptiveTemplate.class);
    LdaptiveUserDetailsService target = new LdaptiveUserDetailsService(
        properties, ldaptiveTemplate);
    target.setRememberMeTokenProvider(null); // has no effect
    target.setAccountControlEvaluator(null); // has no effect
    target.setGrantedAuthoritiesMapper(null); // has no effect
    return target;
  }

  /**
   * Load not existing user.
   */
  @Test
  void loadNotExistingUser() {
    LdaptiveAuthenticationProperties properties = new UserContainsGroupsTemplate();
    properties.setUserBaseDn(USER_BASE_DN);
    properties.setUserRdnAttribute("uid");
    LdaptiveUserDetailsService target = init(properties);

    doReturn(Optional.empty()).when(ldaptiveTemplate).findOne(any());

    assertThatExceptionOfType(UsernameNotFoundException.class)
        .isThrownBy((() -> target.loadUserByUsername("junit")));
  }

  /**
   * Load user with groups by username.
   *
   * @param softly the softly
   */
  @Test
  void loadUserWithGroupsByUsername(SoftAssertions softly) {
    LdaptiveAuthenticationProperties properties = new UserContainsGroupsTemplate();
    properties.setUserBaseDn(USER_BASE_DN);
    properties.setUserRdnAttribute("uid");
    LdaptiveUserDetailsService target = init(properties);

    LdapEntry user = createUser();
    doReturn(Optional.of(user)).when(ldaptiveTemplate).findOne(any());

    LdaptiveUserDetails actual = target.loadUserByUsername(USER_DN);

    assertThat(actual).isNotNull();
    assertActual(actual, softly);
  }

  /**
   * Load user and groups by username.
   *
   * @param softly the softly
   */
  @Test
  void loadUserAndGroupsByUsername(SoftAssertions softly) {
    LdaptiveAuthenticationProperties properties = new GroupContainsUsersTemplate();
    properties.setUserBaseDn(USER_BASE_DN);
    properties.setGroupBaseDn(USER_BASE_DN);
    properties.setUserRdnAttribute("uid");
    properties.setGroupIdAttribute("uid"); // doesn't exist -> use rdn
    properties.setGroupMemberFormat("${username}");

    LdaptiveUserDetailsService target = init(properties);

    LdapEntry user = createUser();
    doReturn(Optional.of(user)).when(ldaptiveTemplate).findOne(any());

    LdapEntry group = createGroup();
    doReturn(List.of(group)).when(ldaptiveTemplate).findAll(any());

    LdaptiveUserDetails actual = target.loadUserByUsername("junit");

    assertThat(actual).isNotNull();
    assertActual(actual, softly);
  }

  private void assertActual(LdaptiveUserDetails actual, SoftAssertions softly) {
    softly
        .assertThat(actual.getDn())
        .isEqualTo(USER_DN);
    softly
        .assertThat(actual.getName())
        .isEqualTo("junit");
    softly
        .assertThat(actual.getUsername())
        .isEqualTo("junit");
    softly
        .assertThat(actual.getPassword())
        .isNotEmpty();
    List<GrantedAuthority> actualAuthorities = new ArrayList<>(actual.getAuthorities());
    List<GrantedAuthority> expectedAuthorities = List.of(new SimpleGrantedAuthority("ROLE_tester"));
    softly
        .assertThat(actualAuthorities)
        .containsExactlyInAnyOrderElementsOf(expectedAuthorities);
    softly
        .assertThat(actual.isEnabled())
        .isTrue();
    softly
        .assertThat(actual.isAccountNonLocked())
        .isTrue();
    softly
        .assertThat(actual.isAccountNonExpired())
        .isTrue();
    softly
        .assertThat(actual.isCredentialsNonExpired())
        .isTrue();
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

  private LdapEntry createGroup() {
    LdapEntry entry = new LdapEntry();
    entry.setDn("cn=tester," + USER_BASE_DN);
    entry.addAttributes(LdapAttribute.builder()
        .name("uniqueMember").values("junit").build());
    return entry;
  }

}