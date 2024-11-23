/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.spring.security.ldaptive.userdetails;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.spring.security.ldaptive.authentication.AccountControlEvaluator;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.springframework.util.ObjectUtils;

/**
 * The ldaptive pwdLastSet remember-me token provider.
 *
 * @author Christian Bremer
 */
public class LdaptivePwdLastSetRememberMeTokenProvider
    extends LdaptiveEvaluatedRememberMeTokenProvider {

  /**
   * The pwdLastSet attribute name.
   */
  @Getter(AccessLevel.PROTECTED)
  private String pwdLastSetAttributeName = "pwdLastSet";

  /**
   * Instantiates a ldaptive pwd last set remember-me token provider.
   */
  public LdaptivePwdLastSetRememberMeTokenProvider() {
    this(null);
  }

  /**
   * Instantiates a ldaptive pwd last set remember-me token provider.
   *
   * @param accountControlEvaluator the account control evaluator
   */
  public LdaptivePwdLastSetRememberMeTokenProvider(
      AccountControlEvaluator accountControlEvaluator) {
    this(accountControlEvaluator, null);
  }

  /**
   * Instantiates a ldaptive pwd last set remember-me token provider.
   *
   * @param accountControlEvaluator the account control evaluator
   * @param pwdLastSetAttributeName the pwd last set attribute name
   */
  public LdaptivePwdLastSetRememberMeTokenProvider(
      AccountControlEvaluator accountControlEvaluator,
      String pwdLastSetAttributeName) {
    super(accountControlEvaluator);
    setPwdLastSetAttributeName(pwdLastSetAttributeName);
  }

  /**
   * Sets pwdLastSet attribute name.
   *
   * @param pwdLastSetAttributeName the pwdLastSet attribute name
   */
  public void setPwdLastSetAttributeName(String pwdLastSetAttributeName) {
    if (!ObjectUtils.isEmpty(pwdLastSetAttributeName)) {
      this.pwdLastSetAttributeName = pwdLastSetAttributeName;
    }
  }

  @Override
  public String getRememberMeToken(LdapEntry ldapEntry) {
    return Optional
        .ofNullable(ldapEntry.getAttribute(getPwdLastSetAttributeName()))
        .map(LdapAttribute::getStringValue)
        .map(value -> getAccessControlValue(ldapEntry) + value)
        .orElseGet(() -> LdaptiveRememberMeTokenProvider.invalid().getRememberMeToken(ldapEntry));
  }

}
