package org.bremersee.spring.security.core.userdetails.ldaptive;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;

/**
 * The type Ldaptive pwd last set password provider test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class LdaptivePwdLastSetPasswordProviderTest {

  private LdaptivePwdLastSetPasswordProvider target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    target = new LdaptivePwdLastSetPasswordProvider();
    target.setAccountControlEvaluator(new NoAccountControlEvaluator());
    target.setPwdLastSetAttributeName("lastPwdSet");
  }

  /**
   * Gets password.
   */
  @Test
  void getPassword() {
    LdapEntry ldapEntry = mock(LdapEntry.class);
    LdapAttribute ldapAttribute = mock(LdapAttribute.class);
    doReturn("value").when(ldapAttribute).getStringValue();
    doReturn(ldapAttribute).when(ldapEntry).getAttribute("lastPwdSet");
    String evaluation = "true:true:true:true-";
    Assertions
        .assertThat(target.getPassword(ldapEntry))
        .isEqualTo(LdaptivePasswordProvider.INVALID + evaluation + "value");
  }
}