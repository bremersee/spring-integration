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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bremersee.ldaptive.serializable.SerLdapEntry;
import org.bremersee.spring.security.authentication.AuthenticationSource;
import org.bremersee.spring.security.authentication.AuthenticationConverter;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.RoleMapping;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthenticationProperties.StringReplacement;
import org.ldaptive.LdapEntry;
import org.springframework.lang.NonNull;

/**
 * The type LdaptiveAuthenticationTokenConverter.
 *
 * @author Christian Bremer
 */
public class LdaptiveAuthenticationTokenConverter
    extends AuthenticationConverter<AuthenticationSource<LdapEntry>, LdaptiveAuthentication> {

  private final LdaptiveAuthenticationProperties properties;

  public LdaptiveAuthenticationTokenConverter(
      LdaptiveAuthenticationProperties properties) {
    super(
        properties.getDefaultRoles(),
        Stream.ofNullable(properties.getRoleMapping())
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(RoleMapping::getSource, RoleMapping::getTarget)),
        properties.getRolePrefix(),
        properties.getRoleCaseTransformation(),
        Stream
            .ofNullable(properties.getRoleStringReplacements())
            .flatMap(Collection::stream)
            .collect(Collectors
                .toMap(StringReplacement::getRegex, StringReplacement::getReplacement)));
    this.properties = properties;
  }

  @Override
  public LdaptiveAuthentication convert(
      @NonNull AuthenticationSource<LdapEntry> source) {
    return new LdaptiveAuthenticationToken(
        source.getName(),
        normalize(source.getAuthorities()),
        serialize(source.getAttributes()),
        properties);
  }

  protected SerLdapEntry serialize(LdapEntry ldapEntry) {
    return new SerLdapEntry(ldapEntry);
  }

}
