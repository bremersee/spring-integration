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

package org.bremersee.spring.security.ldaptive.authentication;

import static org.springframework.util.ObjectUtils.isEmpty;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bremersee.ldaptive.LdaptiveException;
import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.spring.security.core.EmailToUsernameResolver;
import org.ldaptive.FilterTemplate;
import org.ldaptive.LdapAttribute;
import org.ldaptive.SearchRequest;

/**
 * The email to username resolver by ldap attribute.
 *
 * @author Christian Bremer
 */
public class EmailToUsernameResolverByLdapAttribute implements EmailToUsernameResolver {

  /**
   * The Logger.
   */
  protected final Log logger = LogFactory.getLog(this.getClass());

  /**
   * The properties.
   */
  @Getter(AccessLevel.PROTECTED)
  private final LdaptiveAuthenticationProperties properties;

  /**
   * The ldaptive template.
   */
  @Getter(AccessLevel.PROTECTED)
  private final LdaptiveTemplate ldaptiveTemplate;

  /**
   * Instantiates a new Email to username resolver by ldap attribute.
   *
   * @param properties the properties
   * @param ldaptiveTemplate the ldaptive template
   */
  public EmailToUsernameResolverByLdapAttribute(
      LdaptiveAuthenticationProperties properties,
      LdaptiveTemplate ldaptiveTemplate) {
    this.properties = properties;
    this.ldaptiveTemplate = ldaptiveTemplate;
  }

  @Override
  public Optional<String> getUsernameByEmail(String email) {
    return Optional.ofNullable(email)
        .filter(mail -> isValidEmail(mail)
            && areRequiredPropertiesPresent())
        .flatMap(this::findUsernameByEmail);
  }

  /**
   * Determines whether required properties are present or not.
   *
   * @return the boolean
   */
  protected boolean areRequiredPropertiesPresent() {
    return !isEmpty(getProperties().getUserBaseDn())
        && !isEmpty(getProperties().getUserObjectClass())
        && !isEmpty(getProperties().getUserFindOneSearchScope())
        && !isEmpty(getProperties().getEmailAttribute())
        && !isEmpty(getProperties().getUsernameAttribute());
  }

  private Optional<String> findUsernameByEmail(String email) {
    try {
      String filter = String.format("(&(objectClass=%s)(%s={0}))",
          getProperties().getUserObjectClass(), getProperties().getEmailAttribute());
      SearchRequest searchRequest = SearchRequest.builder()
          .dn(getProperties().getUserBaseDn())
          .filter(FilterTemplate.builder()
              .filter(filter)
              .parameters(email)
              .build())
          .scope(getProperties().getUserFindOneSearchScope())
          .build();
      return Stream.ofNullable(getLdaptiveTemplate().findAll(searchRequest))
          .filter(collection -> collection.size() == 1)
          .flatMap(Collection::stream)
          .findFirst()
          .map(ldapEntry -> ldapEntry.getAttribute(getProperties().getUsernameAttribute()))
          .map(LdapAttribute::getStringValue);

    } catch (LdaptiveException e) {
      logger.warn("Resolve username by email '" + email + "' failed.", e);
      return Optional.empty();
    }
  }
}
