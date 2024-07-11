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

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.ldaptive.LdapEntry;

/**
 * The ldaptive in-memory password provider. Don't use it! It's insecure!
 *
 * @author Christian Bremer
 */
public class LdaptiveInMemoryPasswordProvider implements LdaptivePasswordProvider {

  private static final Map<String, String> PASSWORD_MAP = new ConcurrentHashMap<>();

  @Override
  public String getPassword(LdapEntry ldapEntry, String clearPassword) {
    PASSWORD_MAP.put(ldapEntry.getDn(), clearPassword);
    return PLAIN + clearPassword;
  }

  @Override
  public String getPassword(LdapEntry ldapEntry) {
    return Optional.ofNullable(PASSWORD_MAP.get(ldapEntry.getDn()))
        .map(clearPassword -> PLAIN + clearPassword)
        .orElseGet(() -> LdaptivePasswordProvider.invalid().getPassword(ldapEntry));
  }

}
