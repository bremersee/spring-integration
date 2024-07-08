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

package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.bremersee.spring.boot.autoconfigure.security.authentication.AuthenticationProperties.JwtConverterProperties;
import org.bremersee.spring.boot.autoconfigure.security.authentication.AuthenticationProperties.RoleMapping;
import org.bremersee.spring.boot.autoconfigure.security.authentication.AuthenticationProperties.StringReplacement;
import org.bremersee.spring.security.authentication.CaseTransformation;
import org.bremersee.spring.security.authentication.JsonPathJwtConverter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.ClassUtils;

/**
 * The jwt converter autoconfiguration.
 *
 * @author Christian Bremer
 */
@AutoConfiguration
@ConditionalOnClass(name = {
    "org.bremersee.spring.security.authentication.JsonPathJwtConverter"
})
@ConditionalOnProperty(
    prefix = "spring.security.oauth2.resourceserver.jwt",
    name = "jwk-set-uri")
@EnableConfigurationProperties(AuthenticationProperties.class)
@Slf4j
public class JwtConverterAutoConfiguration {

  private final JwtConverterProperties properties;

  /**
   * Instantiates a new Jwt converter auto configuration.
   *
   * @param properties the properties
   */
  public JwtConverterAutoConfiguration(AuthenticationProperties properties) {
    this.properties = properties.getJwtConverter();
  }

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""

            *********************************************************************************
            * {}
            * properties = {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName(),
        properties);
  }

  /**
   * Creates jwt converter.
   *
   * @return the converter
   */
  @ConditionalOnMissingBean
  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> jwtConverter() {
    return JsonPathJwtConverter.builder()
        .nameJsonPath(properties.getNameJsonPath())
        .firstNameJsonPath(properties.getFirstNameJsonPath())
        .lastNameJsonPath(properties.getLastNameJsonPath())
        .emailJsonPath(properties.getEmailJsonPath())

        .rolesJsonPath(properties.getRolesJsonPath())
        .rolesValueList(properties.isRolesValueList())
        .rolesValueSeparator(properties.getRolesValueSeparator())
        .roleMapping(Stream.ofNullable(properties.getRoleMapping())
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(
                RoleMapping::getSource,
                RoleMapping::getTarget,
                (first, second) -> first,
                LinkedHashMap::new)))
        .roleStringReplacements(Stream.ofNullable(properties.getRoleStringReplacements())
            .flatMap(Collection::stream)
            .collect(Collectors.toMap(
                StringReplacement::getRegex,
                StringReplacement::getReplacement,
                (first, second) -> first,
                LinkedHashMap::new)))
        .roleCaseTransformation(getCaseTransformation(properties.getRoleCaseTransformation()))
        .rolePrefix(properties.getRolePrefix())
        .defaultRoles(properties.getDefaultRoles())
        .build();
  }

  private CaseTransformation getCaseTransformation(
      AuthenticationProperties.CaseTransformation source) {
    return isNull(source) ? CaseTransformation.NONE : CaseTransformation.valueOf(source.name());
  }
}
