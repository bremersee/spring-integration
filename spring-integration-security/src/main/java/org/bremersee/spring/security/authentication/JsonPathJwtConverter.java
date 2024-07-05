/*
 * Copyright 2020 the original author or authors.
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

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The json path jwt converter.
 *
 * @author Christian Bremer
 */
@Getter(AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class JsonPathJwtConverter
    extends AuthenticationConverter<Jwt, AbstractAuthenticationToken> {

  private final String nameJsonPath;

  private final String firstNameJsonPath;

  private final String lastNameJsonPath;

  private final String emailJsonPath;

  private final String rolesJsonPath;

  private final boolean rolesValueList;

  private final String rolesValueSeparator;

  @Builder(toBuilder = true)
  public JsonPathJwtConverter(
      String nameJsonPath,
      String firstNameJsonPath,
      String lastNameJsonPath,
      String emailJsonPath,
      String rolesJsonPath,
      boolean rolesValueList,
      String rolesValueSeparator,
      List<String> defaultRoles,
      Map<String, String> roleMapping,
      String rolePrefix,
      CaseTransformation roleCaseTransformation,
      Map<String, String> roleStringReplacements) {

    super(defaultRoles, roleMapping, rolePrefix, roleCaseTransformation, roleStringReplacements);
    this.nameJsonPath = nameJsonPath;
    this.firstNameJsonPath = firstNameJsonPath;
    this.lastNameJsonPath = lastNameJsonPath;
    this.emailJsonPath = emailJsonPath;
    this.rolesJsonPath = rolesJsonPath;
    this.rolesValueList = rolesValueList;
    this.rolesValueSeparator = rolesValueSeparator;
  }

  @NonNull
  @Override
  public NormalizedJwtAuthenticationToken convert(@NonNull Jwt source) {
    JsonPathJwtParser parser = new JsonPathJwtParser(source);
    return new NormalizedJwtAuthenticationToken(
        source,
        getGrantedAuthorities(parser),
        getUsername(source, parser),
        getFirstMame(parser),
        getLastMame(parser),
        getEmail(parser));
  }

  protected Set<GrantedAuthority> getGrantedAuthorities(JsonPathJwtParser parser) {
    Set<String> authorities = isRolesValueList()
        ? getAuthoritiesFromList(parser)
        : getAuthoritiesFromValue(parser);
    return normalize(authorities);
  }

  protected Set<String> getAuthoritiesFromList(JsonPathJwtParser parser) {
    //noinspection unchecked
    return Stream.ofNullable(getRolesJsonPath())
        .map(path -> parser.read(path, List.class))
        .filter(Objects::nonNull)
        .map(list -> (List<String>) list)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  protected Set<String> getAuthoritiesFromValue(JsonPathJwtParser parser) {
    return Stream.ofNullable(getRolesJsonPath())
        .filter(path -> !isEmpty(getRolesValueSeparator()))
        .map(path -> parser.read(path, String.class))
        .filter(Objects::nonNull)
        .map(value -> value.split(Pattern.quote(getRolesValueSeparator())))
        .flatMap(Arrays::stream)
        .map(String::valueOf)
        .collect(Collectors.toSet());
  }

  protected String getUsername(Jwt source, JsonPathJwtParser parser) {
    return Optional.ofNullable(getNameJsonPath())
        .filter(jsonPath -> !jsonPath.isBlank())
        .map(jsonPath -> parser.read(jsonPath, String.class))
        .orElseGet(source::getSubject);
  }

  protected String getFirstMame(JsonPathJwtParser parser) {
    return parser.read(getFirstNameJsonPath(), String.class);
  }

  protected String getLastMame(JsonPathJwtParser parser) {
    return parser.read(getLastNameJsonPath(), String.class);
  }

  protected String getEmail(JsonPathJwtParser parser) {
    return parser.read(getEmailJsonPath(), String.class);
  }

}
