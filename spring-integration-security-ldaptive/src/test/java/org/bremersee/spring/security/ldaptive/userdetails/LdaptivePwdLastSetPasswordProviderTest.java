package org.bremersee.spring.security.ldaptive.userdetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.spring.security.ldaptive.authentication.provider.NoAccountControlEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;

/**
 * The ldaptive pwd last set remember-me token provider test.
 */
class LdaptivePwdLastSetPasswordProviderTest {

  private LdaptivePwdLastSetRememberMeTokenProvider target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    target = new LdaptivePwdLastSetRememberMeTokenProvider();
    target.setAccountControlEvaluator(new NoAccountControlEvaluator());
    target.setPwdLastSetAttributeName("lastPwdSet");
  }

  /**
   * Gets remember-me token.
   */
  @Test
  void getRememberMeToken() {
    LdapEntry ldapEntry = mock(LdapEntry.class);
    LdapAttribute ldapAttribute = mock(LdapAttribute.class);
    doReturn("value").when(ldapAttribute).getStringValue();
    doReturn(ldapAttribute).when(ldapEntry).getAttribute("lastPwdSet");
    String evaluation = "true:true:true:true-";
    assertThat(target.getRememberMeToken(ldapEntry))
        .isEqualTo(evaluation + "value");
  }
}