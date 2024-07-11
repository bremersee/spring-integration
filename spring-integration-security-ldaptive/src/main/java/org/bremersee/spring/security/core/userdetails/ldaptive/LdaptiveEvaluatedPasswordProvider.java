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
import lombok.AccessLevel;
import lombok.Getter;
import org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.provider.NoAccountControlEvaluator;
import org.ldaptive.LdapEntry;

/**
 * The ldaptive evaluated password provider.
 *
 * @author Christian Bremer
 */
public class LdaptiveEvaluatedPasswordProvider implements LdaptivePasswordProvider {

  /**
   * The account control evaluator.
   */
  @Getter(AccessLevel.PROTECTED)
  private AccountControlEvaluator accountControlEvaluator = new NoAccountControlEvaluator();

  /**
   * Instantiates a new ldaptive evaluated password provider.
   */
  public LdaptiveEvaluatedPasswordProvider() {
    this(null);
  }

  /**
   * Instantiates a new Ldaptive pwd last set password provider.
   *
   * @param accountControlEvaluator the account control evaluator
   */
  public LdaptiveEvaluatedPasswordProvider(
      AccountControlEvaluator accountControlEvaluator) {
    setAccountControlEvaluator(accountControlEvaluator);
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

  @Override
  public final String getPassword(LdapEntry ldapEntry, String clearPassword) {
    return getPassword(ldapEntry);
  }

  @Override
  public String getPassword(LdapEntry ldapEntry) {
    return INVALID + getAccessControlValue(ldapEntry) + ldapEntry.getDn();
  }

  protected String getAccessControlValue(LdapEntry ldapEntry) {
    return String.format("%s:%s:%s:%s-",
        accountControlEvaluator.isAccountNonExpired(ldapEntry),
        accountControlEvaluator.isAccountNonLocked(ldapEntry),
        accountControlEvaluator.isCredentialsNonExpired(ldapEntry),
        accountControlEvaluator.isEnabled(ldapEntry));
  }

}
