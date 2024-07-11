package org.bremersee.ldaptive;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.ldaptive.BindRequest;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.ModifyRequest;

/**
 * The ldaptive samba template test.
 */
class LdaptiveSambaTemplateTest {

  /**
   * The Connection factory.
   */
  ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
  /**
   * The Target.
   */
  LdaptiveSambaTemplate target = spy(new LdaptiveSambaTemplate(connectionFactory));

  /**
   * Sets password generator.
   */
  @Test
  void setPasswordGenerator() {
    target.setPasswordGenerator(() -> UUID.randomUUID().toString());
  }

  /**
   * Clone template.
   */
  @Test
  void cloneTemplate() {
    assertThat(target.clone())
        .isNotNull();
  }

  /**
   * Generate user password.
   */
  @Test
  void generateUserPassword() {
    configureClonedLdaptiveSambaTemplate();
    String newPass = target.generateUserPassword("cn=foobar,cn=users,dc=example,dc=org");
    assertThat(newPass)
        .isNotEmpty();
  }

  /**
   * Modify user password.
   */
  @Test
  void modifyUserPassword() {
    configureClonedLdaptiveSambaTemplate();
    doReturn(true).when(target).bind(any(BindRequest.class));
    target.modifyUserPassword("cn=foobar,cn=users,dc=example,dc=org", "old", "new");
  }

  /**
   * Modify user password with invalid old password.
   */
  @Test
  void modifyUserPasswordWithInvalidOldPassword() {
    doReturn(false).when(target).bind(any(BindRequest.class));
    assertThatExceptionOfType(LdaptiveException.class)
        .isThrownBy(() -> target
            .modifyUserPassword("cn=foobar,cn=users,dc=example,dc=org", "old", "new"));
  }

  private void configureClonedLdaptiveSambaTemplate() {
    LdaptiveSambaTemplate cloned = spy(new LdaptiveSambaTemplate(connectionFactory));
    doNothing().when(cloned).modify(any(ModifyRequest.class));
    doAnswer(invocation -> {
      cloned.setErrorHandler(invocation.getArgument(0));
      return cloned;
    }).when(target).clone(any(LdaptiveErrorHandler.class));
  }

}