package org.bremersee.spring.security.core.userdetails.ldaptive;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapEntry;

/**
 * The type Ldaptive in memory password provider test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class LdaptiveInMemoryPasswordProviderTest {

  /**
   * Gets password.
   *
   * @param softly the softly
   */
  @Test
  void getPassword(SoftAssertions softly) {
    LdaptiveInMemoryPasswordProvider target = new LdaptiveInMemoryPasswordProvider();
    String dn = "cn=test,ou=users,dc=bremersee,dc=org";
    LdapEntry ldapEntry = mock(LdapEntry.class);
    doReturn(dn).when(ldapEntry).getDn();
    String password = "foobar";
    softly
        .assertThat(target.getPassword(ldapEntry, password))
        .isEqualTo(LdaptivePasswordProvider.PLAIN + password);
    softly
        .assertThat(target.getPassword(ldapEntry))
        .isEqualTo(LdaptivePasswordProvider.PLAIN + password);
  }
}