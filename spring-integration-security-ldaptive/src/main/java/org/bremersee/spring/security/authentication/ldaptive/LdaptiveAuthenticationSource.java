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

package org.bremersee.spring.security.authentication.ldaptive;

import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bremersee.spring.security.authentication.AuthenticationSource;
import org.ldaptive.LdapEntry;

/**
 * The type LdaptiveAuthenticationSource.
 *
 * @author Christian Bremer
 */
@Getter
@AllArgsConstructor
public class LdaptiveAuthenticationSource implements AuthenticationSource<LdapEntry> {

  /**
   * The Name.
   */
  private final String name;

  /**
   * The Authorities.
   */
  private final Collection<? extends String> authorities;

  /**
   * The Attributes.
   */
  private final LdapEntry attributes;
}