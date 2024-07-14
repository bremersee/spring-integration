package org.bremersee.spring.boot.autoconfigure.data.ldaptive;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.spring.boot.autoconfigure.ldaptive.LdaptiveAutoConfiguration;
import org.bremersee.spring.boot.autoconfigure.ldaptive.LdaptiveConnectionProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The ldaptive auto configuration test.
 */
class LdaptiveAutoConfigurationTest {

  /**
   * The Target.
   */
  LdaptiveAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    LdaptiveConnectionProperties properties = new LdaptiveConnectionProperties();
    properties.setLdapUrl("ldap://localhost:389");
    target = new LdaptiveAutoConfiguration(properties);
    target.init();
  }

  /**
   * Connection config.
   */
  @Test
  void connectionConfig() {
    assertThat(target.connectionConfig())
        .isNotNull();
  }

  /**
   * Connection factory.
   */
  @Test
  void connectionFactory() {
    assertThat(target.connectionFactory(target.connectionConfig()))
        .isNotNull();
  }

  /**
   * The ldaptive template.
   */
  @Test
  void ldaptiveTemplate() {
    assertThat(target.ldaptiveTemplate(target.connectionFactory(target.connectionConfig())))
        .isNotNull();
  }

  /**
   * Reactive ldaptive template.
   */
  @Test
  void reactiveLdaptiveTemplate() {
    assertThat(target.reactiveLdaptiveTemplate(target.connectionFactory(target.connectionConfig())))
        .isNotNull();
  }
}