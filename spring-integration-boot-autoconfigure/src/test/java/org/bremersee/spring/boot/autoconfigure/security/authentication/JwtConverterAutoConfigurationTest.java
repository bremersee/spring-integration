package org.bremersee.spring.boot.autoconfigure.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

/**
 * The type Jwt converter auto configuration test.
 */
class JwtConverterAutoConfigurationTest {

  /**
   * The Target.
   */
  JwtConverterAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeEach
  void init() {
    AuthenticationProperties properties = new AuthenticationProperties();
    target = new JwtConverterAutoConfiguration(properties);
    target.init();
  }

  /**
   * Jwt converter.
   */
  @Test
  void jwtConverter() {
    @SuppressWarnings("unchecked")
    ObjectProvider<GrantedAuthoritiesMapper> mapper = mock(ObjectProvider.class);
    doReturn(mock(GrantedAuthoritiesMapper.class))
        .when(mapper).getIfAvailable(any());
    assertThat(target.jwtConverter(mapper))
        .isNotNull();
  }
}