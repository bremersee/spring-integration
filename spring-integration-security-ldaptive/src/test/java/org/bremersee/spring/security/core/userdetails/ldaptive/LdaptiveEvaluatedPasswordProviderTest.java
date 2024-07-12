package org.bremersee.spring.security.core.userdetails.ldaptive;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapEntry;

/**
 * The type Ldaptive evaluated password provider test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class LdaptiveEvaluatedPasswordProviderTest {

  private LdaptiveEvaluatedPasswordProvider target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    target = new LdaptiveEvaluatedPasswordProvider();
    target.setAccountControlEvaluator(new NoAccountControlEvaluator());
  }

  /**
   * Gets password.
   *
   * @param softly the softly
   */
  @Test
  void getPassword(SoftAssertions softly) {
    String dn = "cn=test,ou=users,dc=bremersee,dc=org";
    LdapEntry ldapEntry = mock(LdapEntry.class);
    doReturn(dn).when(ldapEntry).getDn();
    String password = "foobar";
    String evaluation = "true:true:true:true-";
    softly
        .assertThat(target.getPassword(ldapEntry, password))
        .isEqualTo(LdaptivePasswordProvider.INVALID + evaluation + dn);
    softly
        .assertThat(target.getPassword(ldapEntry))
        .isEqualTo(LdaptivePasswordProvider.INVALID + evaluation + dn);
  }
}