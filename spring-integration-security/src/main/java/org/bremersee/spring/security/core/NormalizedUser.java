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

import java.io.Serial;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The normalized user.
 *
 * @author Christian Bremer
 */
@ToString
@EqualsAndHashCode
public class NormalizedUser implements NormalizedPrincipal {

  @Serial
  private static final long serialVersionUID = 1L;

  private final String name;

  private final String firstName;

  private final String lastName;

  private final String email;

  /**
   * Instantiates a new normalized user.
   *
   * @param name the name
   * @param firstName the first name
   * @param lastName the last name
   * @param email the email
   */
  public NormalizedUser(String name, String firstName, String lastName, String email) {
    this.name = name;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public String getEmail() {
    return email;
  }

}
