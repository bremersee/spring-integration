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

package org.bremersee.spring.security.oauth2.server.resource.authentication;

import static java.util.Objects.requireNonNullElseGet;
import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bremersee.spring.security.core.NormalizedUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * The json path jwt converter.
 *
 * @author Christian Bremer
 */
@Getter(AccessLevel.PROTECTED)
@ToString(callSuper = true)
@EqualsAndHashCode
public class JsonPathJwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

  /**
   * The json path to the username.
   */
  private final String nameJsonPath;

  /**
   * The json path to the first name.
   */
  private final String firstNameJsonPath;

  /**
   * The json path to the last name.
   */
  private final String lastNameJsonPath;

  /**
   * The json path to the email.
   */
  private final String emailJsonPath;

  /**
   * The json path to the roles.
   */
  private final String rolesJsonPath;

  /**
   * Specifies whether the roles are represented as a json array or as a list separated by
   * {@link #getRolesValueSeparator()}.
   */
  private final boolean rolesValueList;

  /**
   * The roles separator to use if {@link #isRolesValueList()} is set to {@code false}.
   */
  private final String rolesValueSeparator;

  /**
   * The authorities mapper.
   */
  private final GrantedAuthoritiesMapper authoritiesMapper;

  /**
   * Instantiates a new Json path jwt converter.
   *
   * @param nameJsonPath the name json path
   * @param firstNameJsonPath the first name json path
   * @param lastNameJsonPath the last name json path
   * @param emailJsonPath the email json path
   * @param rolesJsonPath the roles json path
   * @param rolesValueList the roles value list
   * @param rolesValueSeparator the roles value separator
   * @param authoritiesMapper the authorities mapper
   */
  public JsonPathJwtConverter(
      String nameJsonPath,
      String firstNameJsonPath,
      String lastNameJsonPath,
      String emailJsonPath,
      String rolesJsonPath,
      boolean rolesValueList,
      String rolesValueSeparator,
      GrantedAuthoritiesMapper authoritiesMapper) {

    this.nameJsonPath = nameJsonPath;
    this.firstNameJsonPath = firstNameJsonPath;
    this.lastNameJsonPath = lastNameJsonPath;
    this.emailJsonPath = emailJsonPath;
    this.rolesJsonPath = rolesJsonPath;
    this.rolesValueList = rolesValueList;
    this.rolesValueSeparator = rolesValueSeparator;
    this.authoritiesMapper = requireNonNullElseGet(authoritiesMapper, SimpleAuthorityMapper::new);
  }

  @NonNull
  @Override
  public NormalizedJwtAuthenticationToken convert(@NonNull Jwt source) {
    JsonPathJwtParser parser = new JsonPathJwtParser(source);
    return new NormalizedJwtAuthenticationToken(
        source,
        new NormalizedUser(
            getUsername(source, parser),
            getFirstName(parser),
            getLastName(parser),
            getEmail(parser)),
        getGrantedAuthorities(parser));
  }

  /**
   * Gets granted authorities.
   *
   * @param parser the parser
   * @return the granted authorities
   */
  protected Collection<? extends GrantedAuthority> getGrantedAuthorities(JsonPathJwtParser parser) {
    Stream<String> values = isRolesValueList()
        ? getAuthoritiesFromList(parser)
        : getAuthoritiesFromValue(parser);
    Set<GrantedAuthority> authorities = values.map(SimpleGrantedAuthority::new)
        .collect(Collectors.toSet());
    return authoritiesMapper.mapAuthorities(authorities);
  }

  /**
   * Gets authorities from list.
   *
   * @param parser the parser
   * @return the authorities from list
   */
  protected Stream<String> getAuthoritiesFromList(JsonPathJwtParser parser) {
    //noinspection unchecked
    return Stream.ofNullable(getRolesJsonPath())
        .map(path -> parser.read(path, List.class))
        .filter(Objects::nonNull)
        .map(list -> (List<String>) list)
        .flatMap(Collection::stream);
  }

  /**
   * Gets authorities from value.
   *
   * @param parser the parser
   * @return the authorities from value
   */
  protected Stream<String> getAuthoritiesFromValue(JsonPathJwtParser parser) {
    return Stream.ofNullable(getRolesJsonPath())
        .filter(path -> !isEmpty(getRolesValueSeparator()))
        .map(path -> parser.read(path, String.class))
        .filter(Objects::nonNull)
        .map(value -> value.split(Pattern.quote(getRolesValueSeparator())))
        .flatMap(Arrays::stream)
        .map(String::valueOf);
  }

  /**
   * Gets username.
   *
   * @param source the source
   * @param parser the parser
   * @return the username
   */
  protected String getUsername(Jwt source, JsonPathJwtParser parser) {
    return Optional.ofNullable(getNameJsonPath())
        .filter(jsonPath -> !jsonPath.isBlank())
        .map(jsonPath -> parser.read(jsonPath, String.class))
        .orElseGet(source::getSubject);
  }

  /**
   * Gets first name.
   *
   * @param parser the parser
   * @return the first name
   */
  protected String getFirstName(JsonPathJwtParser parser) {
    return parser.read(getFirstNameJsonPath(), String.class);
  }

  /**
   * Gets last name.
   *
   * @param parser the parser
   * @return the last name
   */
  protected String getLastName(JsonPathJwtParser parser) {
    return parser.read(getLastNameJsonPath(), String.class);
  }

  /**
   * Gets email.
   *
   * @param parser the parser
   * @return the email
   */
  protected String getEmail(JsonPathJwtParser parser) {
    return parser.read(getEmailJsonPath(), String.class);
  }

}
