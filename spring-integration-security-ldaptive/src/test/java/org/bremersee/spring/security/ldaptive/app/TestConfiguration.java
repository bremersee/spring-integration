/*
 * Copyright 2021 the original author or authors.
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

package org.bremersee.spring.security.ldaptive.app;

import org.bremersee.ldaptive.LdaptiveTemplate;
import org.bremersee.spring.security.core.EmailToUsernameResolver;
import org.bremersee.spring.security.ldaptive.authentication.EmailToUsernameResolverByLdapAttribute;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationManager;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.ldaptive.authentication.provider.OpenLdapTemplate;
import org.ldaptive.BindConnectionInitializer;
import org.ldaptive.ConnectionConfig;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.DefaultConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * The test configuration to test the ldaptive security.
 *
 * @author Christian Bremer
 */
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackageClasses = {TestConfiguration.class})
public class TestConfiguration {

  @Value("${spring.ldap.embedded.base-dn}")
  private String baseDn;

  @Value("${spring.ldap.embedded.credential.username}")
  private String username;

  @Value("${spring.ldap.embedded.credential.password}")
  private String password;

  @Value("${spring.ldap.embedded.port}")
  private int port;

  /**
   * Connection factory.
   *
   * @return the connection factory
   */
  @Bean
  public ConnectionFactory connectionFactory() {
    return DefaultConnectionFactory.builder()
        .config(ConnectionConfig.builder()
            .url("ldap://localhost:" + port)
            .connectionInitializers(BindConnectionInitializer.builder()
                .dn(username)
                .credential(password)
                .build())
            .build())
        .build();
  }

  /**
   * Authentication properties ldaptive authentication properties.
   *
   * @return the ldaptive authentication properties
   */
  @Bean
  public LdaptiveAuthenticationProperties authenticationProperties() {
    LdaptiveAuthenticationProperties authenticationProperties = new OpenLdapTemplate();
    authenticationProperties.setUserBaseDn("ou=people," + baseDn);
    authenticationProperties.setLastNameAttribute("sn");
    authenticationProperties.setFirstNameAttribute("givenName");
    authenticationProperties.setEmailAttribute("mail");
    return authenticationProperties;
  }

  /**
   * Email to username converter email to username converter.
   *
   * @param connectionFactory the connection factory
   * @param authenticationProperties the authentication properties
   * @return the email to username converter
   */
  @Bean
  public EmailToUsernameResolver emailToUsernameConverter(
      ConnectionFactory connectionFactory,
      LdaptiveAuthenticationProperties authenticationProperties) {
    return new EmailToUsernameResolverByLdapAttribute(
        authenticationProperties,
        new LdaptiveTemplate(connectionFactory));
  }

  /**
   * Authentication manager ldaptive authentication manager.
   *
   * @param connectionFactory the connection factory
   * @param authenticationProperties the authentication properties
   * @param emailToUsernameResolver the email to username converter
   * @return the ldaptive authentication manager
   */
  @Bean
  public LdaptiveAuthenticationManager authenticationManager(
      ConnectionFactory connectionFactory,
      LdaptiveAuthenticationProperties authenticationProperties,
      EmailToUsernameResolver emailToUsernameResolver) {
    LdaptiveAuthenticationManager authenticationManager = new LdaptiveAuthenticationManager(
        connectionFactory.getConnectionConfig(),
        authenticationProperties);
    authenticationManager.setEmailToUsernameResolver(emailToUsernameResolver);
    return authenticationManager;
  }

}
