package org.bremersee.spring.security.ldaptive.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Optional;
import org.bremersee.ldaptive.LdaptiveException;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.ldaptive.SearchScope;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * The type Email to username resolver by ldap attribute test.
 */
@ExtendWith({MockitoExtension.class})
class EmailToUsernameResolverByLdapAttributeTest {

  /**
   * The Properties.
   */
  @Mock
  LdaptiveAuthenticationProperties properties;

  /**
   * The Ldaptive template.
   */
  @Mock
  LdaptiveTemplate ldaptiveTemplate;

  /**
   * The Target.
   */
  @InjectMocks
  EmailToUsernameResolverByLdapAttribute target;

  /**
   * Gets username by email.
   */
  @Test
  void getUsernameByEmail() {
    doReturn("ou=people,dc=bremersee,dc=org").when(properties).getUserBaseDn();
    doReturn(SearchScope.ONELEVEL).when(properties).getUserFindOneSearchScope();
    doReturn("person").when(properties).getUserObjectClass();
    doReturn("mail").when(properties).getEmailAttribute();
    doReturn("uid").when(properties).getUsernameAttribute();

    LdapEntry user = createUser();
    doReturn(List.of(user)).when(ldaptiveTemplate).findAll(any());

    Optional<String> actual = target.getUsernameByEmail("junit@example.com");

    assertThat(actual)
        .hasValue("junit");
  }

  /**
   * Gets no username by email because of ldap exception.
   */
  @Test
  void getNoUsernameByEmailBecauseOfLdapException() {
    doReturn("ou=people,dc=bremersee,dc=org").when(properties).getUserBaseDn();
    doReturn(SearchScope.ONELEVEL).when(properties).getUserFindOneSearchScope();
    doReturn("person").when(properties).getUserObjectClass();
    doReturn("mail").when(properties).getEmailAttribute();
    doReturn("uid").when(properties).getUsernameAttribute();

    doThrow(LdaptiveException.builder().reason("test").build())
        .when(ldaptiveTemplate)
        .findAll(any());

    Optional<String> actual = target.getUsernameByEmail("junit@example.com");

    assertThat(actual).isEmpty();
  }

  /**
   * Gets no username by email because of missing property.
   */
  @Test
  void getNoUsernameByEmailBecauseOfMissingProperty() {
    doReturn("ou=people,dc=bremersee,dc=org").when(properties).getUserBaseDn();
    doReturn(SearchScope.ONELEVEL).when(properties).getUserFindOneSearchScope();
    doReturn("person").when(properties).getUserObjectClass();
    doReturn("mail").when(properties).getEmailAttribute();
    doReturn("").when(properties).getUsernameAttribute();

    Optional<String> actual = target.getUsernameByEmail("junit@example.com");

    assertThat(actual).isEmpty();
  }

  private LdapEntry createUser() {
    LdapEntry entry = new LdapEntry();
    entry.setDn("cn=junit,ou=people,dc=bremersee,dc=org");
    entry.addAttributes(LdapAttribute.builder().name("uid").values("junit").build());
    entry.addAttributes(LdapAttribute.builder().name("mail").values("junit@example.com").build());
    return entry;
  }

}