package org.bremersee.spring.security.ldaptive.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.spring.security.ldaptive.authentication.provider.NoAccountControlEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapEntry;

/**
 * The ldaptive evaluated remember-me token provider test.
 */
class LdaptiveEvaluatedPasswordProviderTest {

  private LdaptiveEvaluatedRememberMeTokenProvider target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    target = new LdaptiveEvaluatedRememberMeTokenProvider();
    target.setAccountControlEvaluator(new NoAccountControlEvaluator());
  }

  /**
   * Gets remember-me token.
   */
  @Test
  void getRememberMeToken() {
    String dn = "cn=test,ou=users,dc=bremersee,dc=org";
    LdapEntry ldapEntry = mock(LdapEntry.class);
    doReturn(dn).when(ldapEntry).getDn();
    String evaluation = "true:true:true:true-";
    assertThat(target.getRememberMeToken(ldapEntry))
        .isEqualTo(evaluation + dn);
  }
}