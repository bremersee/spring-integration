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

package org.bremersee.ldaptive.serializable;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.ldaptive.LdapEntry;

/**
 * The serializable ldap entry.
 *
 * @author Christian Bremer
 */
@Getter
@ToString
@EqualsAndHashCode
public class SerLdapEntry implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * The distinguished name.
   */
  private final String dn;

  /**
   * The attributes.
   */
  private final Map<String, SerLdapAttr> attributes;

  /**
   * Instantiates a new serializable ldap entry.
   *
   * @param ldapEntry the ldap entry
   */
  public SerLdapEntry(LdapEntry ldapEntry) {
    this.dn = Optional.ofNullable(ldapEntry)
        .map(LdapEntry::getDn)
        .orElse(null);
    this.attributes = Stream.ofNullable(ldapEntry)
        .map(LdapEntry::getAttributes)
        .flatMap(Collection::stream)
        .map(SerLdapAttr::new)
        .collect(Collectors
            .toUnmodifiableMap(SerLdapAttr::getAttributeName, Function.identity()));
  }

}
