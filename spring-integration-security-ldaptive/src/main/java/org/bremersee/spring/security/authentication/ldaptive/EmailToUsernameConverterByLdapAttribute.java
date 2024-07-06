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

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.DefaultConnectionFactory;
import org.ldaptive.FilterTemplate;
import org.ldaptive.LdapAttribute;
import org.ldaptive.SearchRequest;
import org.ldaptive.SearchResponse;

/**
 * The type EmailToUsernameConverterByLdapAttribute.
 *
 * @author Christian Bremer
 */
public class EmailToUsernameConverterByLdapAttribute implements EmailToUsernameConverter {

  private final LdaptiveAuthenticationProperties properties;

  private final ConnectionConfig connectionConfig;

  /**
   * Instantiates a new Email to username converter by ldap attribute.
   *
   * @param properties the properties
   * @param connectionConfig the connection config
   */
  public EmailToUsernameConverterByLdapAttribute(
      LdaptiveAuthenticationProperties properties,
      ConnectionConfig connectionConfig) {
    this.properties = properties;
    this.connectionConfig = connectionConfig;
  }

  @Override
  public Optional<String> getUsernameByEmail(String email) {
    return Optional.ofNullable(email)
        .filter(mail -> isValidEmail(mail)
            && areRequiredPropertiesPresent()
            && isBindConnectionInitializerPresent())
        .flatMap(this::findUsernameByEmail);
  }

  private boolean isBindConnectionInitializerPresent() {
    return Stream.ofNullable(connectionConfig.getConnectionInitializers())
        .flatMap(Arrays::stream)
        .anyMatch(connectionInitializer -> connectionInitializer.getClass()
            .isAssignableFrom(BindConnectionInitializer.class));
  }

  private boolean areRequiredPropertiesPresent() {
    return !isEmpty(properties.getUserObjectClass())
        && !isEmpty(properties.getEmailAttribute())
        && !isEmpty(properties.getUsernameAttribute());
  }

  private Optional<String> findUsernameByEmail(String email) {
    LdaptiveTemplate ldaptiveTemplate = new LdaptiveTemplate(
        new DefaultConnectionFactory(connectionConfig));
    String filter = String.format("(&(objectClass=%s)(%s={0}))",
        properties.getUserObjectClass(), properties.getEmailAttribute());
    SearchRequest searchRequest = SearchRequest.builder()
        .dn(properties.getUserBaseDn())
        .filter(FilterTemplate.builder()
            .filter(filter)
            .parameters(email)
            .build())
        .scope(properties.getUserFindOneSearchScope())
        .build();
    return Stream.ofNullable(ldaptiveTemplate.search(searchRequest))
        .map(SearchResponse::getEntries)
        .filter(collection -> collection.size() == 1)
        .flatMap(Collection::stream)
        .findFirst()
        .map(ldapEntry -> ldapEntry.getAttribute(
            properties.getUsernameAttribute()))
        .map(LdapAttribute::getStringValue);
  }
}
