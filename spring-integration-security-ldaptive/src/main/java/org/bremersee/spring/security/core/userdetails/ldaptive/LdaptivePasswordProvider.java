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

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ldaptive.LdapEntry;

/**
 * The ldaptive password provider.
 *
 * @author Christian Bremer
 */
public interface LdaptivePasswordProvider {

  /**
   * The constant INVALID.
   */
  String INVALID = "{invalid}-";

  /**
   * The constant PLAIN.
   */
  String PLAIN = "{noop}";

  /**
   * Gets password.
   *
   * @param ldapEntry the ldap entry
   * @param clearPassword the clear password
   * @return the password
   */
  String getPassword(LdapEntry ldapEntry, String clearPassword);

  /**
   * Gets password.
   *
   * @param ldapEntry the ldap entry
   * @return the password
   */
  String getPassword(LdapEntry ldapEntry);

  /**
   * Gets the invalid ldaptive password provider.
   *
   * @return the ldaptive password provider
   */
  static LdaptivePasswordProvider invalid() {
    return InvalidPasswordProvider.getInstance();
  }

  /**
   * The invalid ldaptive password provider.
   *
   * @author Christian Bremer
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  class InvalidPasswordProvider implements LdaptivePasswordProvider {

    private static final InvalidPasswordProvider INSTANCE = new InvalidPasswordProvider();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    static InvalidPasswordProvider getInstance() {
      return INSTANCE;
    }

    @Override
    public String getPassword(LdapEntry ldapEntry, String clearPassword) {
      return getPassword(ldapEntry);
    }

    @Override
    public String getPassword(LdapEntry ldapEntry) {
      return INVALID + UUID.randomUUID();
    }
  }

}
