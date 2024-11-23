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

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ldaptive.LdapEntry;

/**
 * The ldaptive remember-me token provider.
 *
 * @author Christian Bremer
 */
public interface LdaptiveRememberMeTokenProvider {

  /**
   * Gets remember-me token.
   *
   * @param ldapEntry the ldap entry
   * @return the remember-me token
   */
  String getRememberMeToken(LdapEntry ldapEntry);

  /**
   * Gets the invalid ldaptive remember-me token provider.
   *
   * @return the ldaptive remember-me token provider
   */
  static LdaptiveRememberMeTokenProvider invalid() {
    return InvalidRememberMeTokenProvider.getInstance();
  }

  /**
   * The invalid ldaptive remember-me token provider.
   *
   * @author Christian Bremer
   */
  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  class InvalidRememberMeTokenProvider implements LdaptiveRememberMeTokenProvider {

    private static final InvalidRememberMeTokenProvider INSTANCE = new InvalidRememberMeTokenProvider();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    static InvalidRememberMeTokenProvider getInstance() {
      return INSTANCE;
    }

    @Override
    public final String getRememberMeToken(LdapEntry ldapEntry) {
      return UUID.randomUUID().toString();
    }
  }

}
