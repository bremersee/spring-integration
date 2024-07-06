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

package org.bremersee.spring.security.authentication;

import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * The normalized authentication converter supports normalization of roles.
 *
 * <p>The order of normalization is:
 * <ul>
 * <li>1. role mapping</li>
 * <li>2. case transformation</li>
 * <li>3. string replacements</li>
 * <li>4. adding of prefix</li>
 * </ul>
 *
 * @param <S> the type of the authentication source
 * @param <T> the type of the normalized authentication
 * @author Christian Bremer
 */
@AllArgsConstructor
@Getter(AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode
public abstract class AuthenticationConverter<S, T extends Authentication>
    implements Converter<S, T> {

  /**
   * The default roles.
   */
  protected List<String> defaultRoles;

  /**
   * The role mapping.
   */
  protected Map<String, String> roleMapping;

  /**
   * The role prefix.
   */
  protected String rolePrefix;

  /**
   * The role case transformation.
   */
  protected CaseTransformation roleCaseTransformation;

  /**
   * The role string replacements.
   */
  protected Map<String, String> roleStringReplacements;

  /**
   * Normalize authorities.
   *
   * @param authorities the authorities
   * @return the normalized authorities
   */
  protected Set<GrantedAuthority> normalize(Collection<? extends String> authorities) {
    return Stream.concat(
            Stream.ofNullable(authorities)
                .flatMap(Collection::stream)
                .filter(authority -> !ObjectUtils.isEmpty(authority))
                .map(this::normalize),
            Stream.ofNullable(getDefaultRoles())
                .flatMap(Collection::stream)
                .filter(authority -> !ObjectUtils.isEmpty(authority))
                .map(SimpleGrantedAuthority::new))
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
    Optional<GrantedAuthority> mappedValue = Stream.ofNullable(getRoleMapping())
        .flatMap(m -> m.entrySet().stream())
        .filter(e -> authority.equalsIgnoreCase(e.getKey()))
        .findFirst()
        .map(Entry::getValue)
        .map(SimpleGrantedAuthority::new);
    if (mappedValue.isPresent()) {
      return mappedValue.get();
    }
    String value = authority;
    if (nonNull(getRoleCaseTransformation())) {
      value = switch (getRoleCaseTransformation()) {
        case TO_LOWER_CASE -> value.toLowerCase();
        case TO_UPPER_CASE -> value.toUpperCase();
        case NONE -> value;
      };
    }
    if (!isEmpty(getRoleStringReplacements())) {
      for (Map.Entry<String, String> replacement : getRoleStringReplacements().entrySet()) {
        value = value.replaceAll(replacement.getKey(), replacement.getValue());
      }
    }
    if (!isEmpty(getRolePrefix()) && !value.startsWith(getRolePrefix())) {
      value = getRolePrefix() + value;
    }
    return new SimpleGrantedAuthority(value);
  }

}
