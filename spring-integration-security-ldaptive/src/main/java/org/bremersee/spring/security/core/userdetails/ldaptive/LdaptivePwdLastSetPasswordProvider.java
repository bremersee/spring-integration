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

package org.bremersee.spring.security.core.userdetails.ldaptive;

import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.springframework.util.ObjectUtils;

/**
 * The ldaptive pwdLastSet password provider.
 *
 * @author Christian Bremer
 */
public class LdaptivePwdLastSetPasswordProvider implements LdaptivePasswordProvider {

  /**
   * The account control evaluator.
   */
  @Getter(AccessLevel.PROTECTED)
  private AccountControlEvaluator accountControlEvaluator = new NoAccountControlEvaluator();

  /**
   * The pwdLastSet attribute name.
   */
  @Getter(AccessLevel.PROTECTED)
  private String pwdLastSetAttributeName = "pwdLastSet";

  /**
   * Instantiates a new Ldaptive pwd last set password provider.
   */
  public LdaptivePwdLastSetPasswordProvider() {
    this(null);
  }

  /**
   * Instantiates a new Ldaptive pwd last set password provider.
   *
   * @param accountControlEvaluator the account control evaluator
   */
  public LdaptivePwdLastSetPasswordProvider(
      AccountControlEvaluator accountControlEvaluator) {
    this(null, null);
  }

  /**
   * Instantiates a new Ldaptive pwd last set password provider.
   *
   * @param accountControlEvaluator the account control evaluator
   * @param pwdLastSetAttributeName the pwd last set attribute name
   */
  public LdaptivePwdLastSetPasswordProvider(
      AccountControlEvaluator accountControlEvaluator,
      String pwdLastSetAttributeName) {
    setAccountControlEvaluator(accountControlEvaluator);
    setPwdLastSetAttributeName(pwdLastSetAttributeName);
  }

  /**
   * Sets account control evaluator.
   *
   * @param accountControlEvaluator the account control evaluator
   */
  public void setAccountControlEvaluator(AccountControlEvaluator accountControlEvaluator) {
    if (Objects.nonNull(accountControlEvaluator)) {
      this.accountControlEvaluator = accountControlEvaluator;
    }
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
  public String getPassword(LdapEntry ldapEntry, String clearPassword) {
    return getPassword(ldapEntry);
  }

  @Override
  public String getPassword(LdapEntry ldapEntry) {
    return Optional
        .ofNullable(ldapEntry.getAttribute(getPwdLastSetAttributeName()))
        .map(LdapAttribute::getStringValue)
        .map(value -> INVALID + getAccessControlValue(ldapEntry) + value)
        .orElseGet(() -> LdaptivePasswordProvider.invalid().getPassword(ldapEntry));
  }

  private String getAccessControlValue(LdapEntry ldapEntry) {
    return String.format("%s:%s:%s:%s-",
        accountControlEvaluator.isAccountNonExpired(ldapEntry),
        accountControlEvaluator.isAccountNonLocked(ldapEntry),
        accountControlEvaluator.isCredentialsNonExpired(ldapEntry),
        accountControlEvaluator.isEnabled(ldapEntry));
  }

}
