package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.bremersee.spring.security.authentication.EmailToUsernameResolver;
import org.bremersee.spring.security.authentication.ldaptive.AccountControlEvaluator;
import org.bremersee.spring.security.authentication.ldaptive.LdaptiveAuthentication;
import org.bremersee.spring.security.authentication.ldaptive.UsernameToBindDnConverter;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptivePasswordProvider;
import org.bremersee.spring.security.core.userdetails.ldaptive.LdaptiveUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldaptive.ConnectionConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

/**
 * The ldaptive authentication autoconfiguration test.
 */
@ExtendWith(SoftAssertionsExtension.class)
class LdaptiveAuthenticationAutoConfigurationTest {

  /**
   * The Properties.
   */
  final AuthenticationProperties properties = new AuthenticationProperties();

  /**
   * The Target.
   */
  LdaptiveAuthenticationAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    properties.getLdaptive().setUserBaseDn("cn=users,dc=example,dc=org");
    target = new LdaptiveAuthenticationAutoConfiguration(properties);
  }

  /**
   * Ldaptive password encoder provider.
   */
  @Test
  void ldaptivePasswordEncoderProvider() {
    assertThat(target.ldaptivePasswordEncoderProvider())
        .isNotNull();
  }

  /**
   * Ldaptive authentication manager.
   */
  @Test
  void ldaptiveAuthenticationManager() {
    ConnectionConfig connectionConfig = ConnectionConfig.builder().build();
    ObjectProvider<EmailToUsernameResolver> emailToUsernameResolver = mock();
    ObjectProvider<UsernameToBindDnConverter> usernameToBindDnConverter = mock();
    ObjectProvider<AccountControlEvaluator> accountControlEvaluator = mock();
    ObjectProvider<GrantedAuthoritiesMapper> grantedAuthoritiesMapper = mock();
    ObjectProvider<Converter<LdaptiveUserDetails, LdaptiveAuthentication>> tokenConverter = mock();
    assertThat(target
        .ldaptiveAuthenticationManager(
            connectionConfig,
            mock(),
            mock(),
            target.ldaptivePasswordEncoderProvider(),
            LdaptivePasswordProvider.invalid(),
            emailToUsernameResolver,
            usernameToBindDnConverter,
            accountControlEvaluator,
            grantedAuthoritiesMapper,
            tokenConverter))
        .isNotNull();
  }

  /**
   * Reactive ldaptive authentication manager.
   */
  @Test
  void reactiveLdaptiveAuthenticationManager() {
    ConnectionConfig connectionConfig = ConnectionConfig.builder().build();
    ObjectProvider<EmailToUsernameResolver> emailToUsernameResolver = mock();
    ObjectProvider<UsernameToBindDnConverter> usernameToBindDnConverter = mock();
    ObjectProvider<AccountControlEvaluator> accountControlEvaluator = mock();
    ObjectProvider<GrantedAuthoritiesMapper> grantedAuthoritiesMapper = mock();
    ObjectProvider<Converter<LdaptiveUserDetails, LdaptiveAuthentication>> tokenConverter = mock();
    assertThat(target
        .reactiveLdaptiveAuthenticationManager(
            connectionConfig,
            mock(),
            mock(),
            target.ldaptivePasswordEncoderProvider(),
            LdaptivePasswordProvider.invalid(),
            emailToUsernameResolver,
            usernameToBindDnConverter,
            accountControlEvaluator,
            grantedAuthoritiesMapper,
            tokenConverter))
        .isNotNull();
  }

  @Test
  void ldaptivePasswordProvider(SoftAssertions softly) {
    ObjectProvider<AccountControlEvaluator> evaluator = mock();
    softly
        .assertThat(target.ldaptivePasswordProvider(evaluator))
        .isNotNull();

    properties.getLdaptive().setPasswordLastSetAttribute("pwdLastSet");
    softly
        .assertThat(target.ldaptivePasswordProvider(evaluator))
        .isNotNull();
  }
}