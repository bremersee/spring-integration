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

package org.bremersee.spring.security.core;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * The email to username resolver.
 *
 * @author Christian Bremer
 */
public interface EmailToUsernameResolver {

  /**
   * Email regex from <a href="https://emailregex.com/">emailregex.com</a> (RFC 5322 Official
   * Standard).
   */
  String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*"
      + "|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]"
      + "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+"
      + "[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
      + "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:"
      + "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]"
      + "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";

  /**
   * Gets username by email.
   *
   * @param email the email
   * @return the username by email
   */
  Optional<String> getUsernameByEmail(String email);

  /**
   * Checks whether the given email is a valid email address.
   *
   * @param email the email
   * @return the boolean
   */
  default boolean isValidEmail(String email) {
    return Pattern.compile(EMAIL_REGEX).matcher(email).matches();
  }

}
