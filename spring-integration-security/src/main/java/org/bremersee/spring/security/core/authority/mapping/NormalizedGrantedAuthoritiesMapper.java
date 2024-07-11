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

package org.bremersee.spring.security.core.authority.mapping;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.util.Assert;

/**
 * The normalized granted authorities mapper.
 *
 * @author Christian Bremer
 */
public class NormalizedGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

  /**
   * The default roles.
   */
  private final List<String> defaultRoles;

  /**
   * The role mapping.
   */
  private final Map<String, String> roleMapping;

  /**
   * The role prefix.
   */
  private final String rolePrefix;

  /**
   * The role case transformation.
   */
  private final CaseTransformation roleCaseTransformation;

  /**
   * The role string replacements.
   */
  private final Map<String, String> roleStringReplacements;

  /**
   * Instantiates a new normalized granted authorities mapper.
   *
   * @param defaultRoles the default roles
   * @param roleMapping the role mapping
   * @param rolePrefix the role prefix
   * @param roleCaseTransformation the role case transformation
   * @param roleStringReplacements the role string replacements
   */
  public NormalizedGrantedAuthoritiesMapper(
      List<String> defaultRoles,
      Map<String, String> roleMapping,
      String rolePrefix,
      CaseTransformation roleCaseTransformation,
      Map<String, String> roleStringReplacements) {
    this.defaultRoles = defaultRoles;
    this.roleMapping = roleMapping;
    this.rolePrefix = requireNonNullElse(rolePrefix, "ROLE_");
    this.roleCaseTransformation = roleCaseTransformation;
    this.roleStringReplacements = roleStringReplacements;
  }

  @Override
  public Collection<? extends GrantedAuthority> mapAuthorities(
      Collection<? extends GrantedAuthority> authorities) {
    Stream<GrantedAuthority> defaultAuthorities = defaultRoles.stream()
        .filter(authority -> !isEmpty(authority))
        .map(SimpleGrantedAuthority::new);
    Stream<GrantedAuthority> givenAuthorities = Stream.ofNullable(authorities)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(GrantedAuthority::getAuthority)
        .filter(authority -> !isEmpty(authority))
        .map(this::normalize);
    return Stream.concat(defaultAuthorities, givenAuthorities)
        .collect(Collectors.toSet());
  }

  /**
   * Normalize granted authority.
   *
   * @param authority the authority
   * @return the granted authority
   */
  protected GrantedAuthority normalize(String authority) {
    Assert.hasText(authority, "Authority must be present.");
    Optional<GrantedAuthority> mappedValue = Stream.ofNullable(roleMapping)
        .flatMap(m -> m.entrySet().stream())
        .filter(e -> authority.equalsIgnoreCase(e.getKey()))
        .findFirst()
        .map(Entry::getValue)
        .map(SimpleGrantedAuthority::new);
    if (mappedValue.isPresent()) {
      return mappedValue.get();
    }
    String value = authority;
    if (nonNull(roleCaseTransformation)) {
      value = switch (roleCaseTransformation) {
        case TO_LOWER_CASE -> value.toLowerCase();
        case TO_UPPER_CASE -> value.toUpperCase();
        case NONE -> value;
      };
    }
    if (!isEmpty(roleStringReplacements)) {
      for (Map.Entry<String, String> replacement : roleStringReplacements.entrySet()) {
        value = value.replaceAll(replacement.getKey(), replacement.getValue());
      }
    }
    if (!isEmpty(rolePrefix) && !value.startsWith(rolePrefix)) {
      value = rolePrefix + value;
    }
    return new SimpleGrantedAuthority(value);
  }

}
