package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.UsernameToBindDnConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldaptive.ConnectionConfig;
import org.springframework.beans.factory.ObjectProvider;

class LdaptiveAuthenticationAutoConfigurationTest {

  final AuthenticationProperties properties = new AuthenticationProperties();

  LdaptiveAuthenticationAutoConfiguration target;

  @BeforeEach
  void init() {
    properties.getLdaptive().setUserBaseDn("cn=users,dc=example,dc=org");
    target = new LdaptiveAuthenticationAutoConfiguration(properties);
  }

  @Test
  void ldaptivePasswordEncoderProvider() {
    assertThat(target.ldaptivePasswordEncoderProvider())
        .isNotNull();
  }

  @Test
  void ldaptiveAuthenticationTokenConverter() {
    assertThat(target.ldaptiveAuthenticationTokenConverter())
        .isNotNull();
  }

  @Test
  void ldaptiveAuthenticationManager() {
    ConnectionConfig connectionConfig = ConnectionConfig.builder().build();
    ObjectProvider<UsernameToBindDnConverter> usernameToBindDnProvider = mock();
    ObjectProvider<AccountControlEvaluator> accountControlEvaluatorProvider = mock();
    assertThat(target
        .ldaptiveAuthenticationManager(
            connectionConfig,
            usernameToBindDnProvider,
            target.ldaptivePasswordEncoderProvider(),
            accountControlEvaluatorProvider,
            target.ldaptiveAuthenticationTokenConverter()))
        .isNotNull();
  }

  @Test
  void reactiveLdaptiveAuthenticationManager() {
    ConnectionConfig connectionConfig = ConnectionConfig.builder().build();
    ObjectProvider<UsernameToBindDnConverter> usernameToBindDnProvider = mock();
    ObjectProvider<AccountControlEvaluator> accountControlEvaluatorProvider = mock();
    assertThat(target
        .reactiveLdaptiveAuthenticationManager(
            connectionConfig,
            usernameToBindDnProvider,
            target.ldaptivePasswordEncoderProvider(),
            accountControlEvaluatorProvider,
            target.ldaptiveAuthenticationTokenConverter()))
        .isNotNull();
  }
}