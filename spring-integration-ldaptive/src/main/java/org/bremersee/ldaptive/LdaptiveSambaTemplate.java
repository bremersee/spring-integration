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

package org.bremersee.ldaptive;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.ldaptive.AttributeModification;
import org.ldaptive.AttributeModification.Type;
import org.ldaptive.ConnectionFactory;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapException;
import org.ldaptive.ModifyRequest;
import org.ldaptive.ResultCode;
import org.ldaptive.SimpleBindRequest;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

/**
 * The ldaptive samba template.
 *
 * @author Christian Bremer
 */
public class LdaptiveSambaTemplate extends LdaptiveTemplate {

  private Supplier<String> passwordGenerator;

  /**
   * Instantiates a new ldaptive samba template.
   *
   * @param connectionFactory the connection factory
   */
  public LdaptiveSambaTemplate(ConnectionFactory connectionFactory) {
    super(connectionFactory);
    this.passwordGenerator = getDefaultPasswordGenerator();
  }

  /**
   * Sets password generator.
   *
   * @param passwordGenerator the password generator
   */
  public void setPasswordGenerator(Supplier<String> passwordGenerator) {
    if (Objects.nonNull(passwordGenerator)) {
      this.passwordGenerator = passwordGenerator;
    }
  }

  @Override
  public LdaptiveSambaTemplate clone(LdaptiveErrorHandler errorHandler) {
    LdaptiveSambaTemplate template = new LdaptiveSambaTemplate(getConnectionFactory());
    template.setErrorHandler(errorHandler);
    return template;
  }

  @Override
  public String generateUserPassword(String dn) {
    String newPassword = passwordGenerator.get();
    setUserPassword(dn, newPassword);
    return newPassword;
  }

  private void setUserPassword(String dn, String newPassword) {
    String quotedPassword = "\"" + newPassword + "\"";
    char[] unicodePwd = quotedPassword.toCharArray();
    byte[] pwdArray = new byte[unicodePwd.length * 2];
    for (int i = 0; i < unicodePwd.length; i++) {
      pwdArray[i * 2 + 1] = (byte) (unicodePwd[i] >>> 8);
      pwdArray[i * 2] = (byte) (unicodePwd[i] & 0xff);
    }
    LdapAttribute ldapAttribute = new LdapAttribute();
    ldapAttribute.setName("unicodePwd");
    ldapAttribute.setBinary(true);
    ldapAttribute.addBinaryValues(pwdArray);
    AttributeModification attributeModification = new AttributeModification(
        Type.REPLACE, ldapAttribute);
    ModifyRequest modifyRequest = ModifyRequest.builder()
        .dn(dn)
        .modifications(attributeModification)
        .build();
    this.clone(new AbstractLdaptiveErrorHandler() {
          @Override
          public LdaptiveException map(LdapException ldapException) {
            int httpStatus;
            String errorCode;
            String reason;
            if (ldapException.getResultCode() == ResultCode.CONSTRAINT_VIOLATION
                && ldapException.getMessage().contains("check_password_restrictions")) {
              httpStatus = 400;
              errorCode = "check_password_restrictions";
              reason = "New password restrictions were violated.";
            } else if (ldapException.getResultCode() == ResultCode.NO_SUCH_OBJECT) {
              httpStatus = 404;
              reason = "No such object found.";
              errorCode = "org.bremersee.ldaptive:26cfda51-0fde-443c-87b1-502fc165bb94";
            } else {
              httpStatus = 500;
              reason = "Internal error.";
              errorCode = "org.bremersee.ldaptive:a70939fb-2c94-412f-80c0-00a7d5dcf4a6";
            }
            return LdaptiveException.builder()
                .httpStatus(httpStatus)
                .errorCode(errorCode)
                .reason(reason)
                .cause(ldapException)
                .build();
          }
        })
        .modify(modifyRequest);
  }

  @Override
  public void modifyUserPassword(String dn, String oldPass, String newPass) {
    if (bind(new SimpleBindRequest(dn, oldPass))) {
      setUserPassword(dn, newPass);
    } else {
      throw LdaptiveException.builder()
          .reason("Bind failed.")
          .errorCode("bind_failed")
          .httpStatus(400)
          .build();
    }
  }

  private static Supplier<String> getDefaultPasswordGenerator() {
    PasswordGenerator pwGen = new PasswordGenerator();
    CharacterData special = new CharacterData() {
      @Override
      public String getErrorCode() {
        return "INSUFFICIENT_SPECIAL";
      }

      @Override
      public String getCharacters() {
        return "!#$%&'()*+,-./:;<=>?@[]_{|}~";
      }
    };
    List<CharacterRule> rules = List.of(
        // at least five upper-case character
        new CharacterRule(EnglishCharacterData.UpperCase, 5),

        // at least five lower-case character
        new CharacterRule(EnglishCharacterData.LowerCase, 5),

        // at least three digit character
        new CharacterRule(EnglishCharacterData.Digit, 3),

        // at least one special character
        new CharacterRule(special, 1));
    return () -> pwGen.generatePassword(16, rules);
  }

}
