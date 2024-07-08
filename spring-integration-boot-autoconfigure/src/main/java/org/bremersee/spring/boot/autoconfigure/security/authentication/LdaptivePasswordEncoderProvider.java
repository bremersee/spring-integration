/*
 * Copyright 2014 the original author or authors.
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

package org.bremersee.spring.boot.autoconfigure.security.authentication;

import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.password.LdapShaPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The password encoder provider.
 *
 * @author Christian Bremer
 */
@FunctionalInterface
public interface LdaptivePasswordEncoderProvider extends Supplier<PasswordEncoder> {

  /**
   * Creates a new default provider.
   *
   * @return the default password encoder provider
   */
  static LdaptivePasswordEncoderProvider defaultProvider() {
    return new DefaultLdaptivePasswordEncoderProvider();
  }

  /**
   * The default password encoder provider.
   */
  @NoArgsConstructor(access = AccessLevel.PACKAGE)
  class DefaultLdaptivePasswordEncoderProvider implements LdaptivePasswordEncoderProvider {

    @Override
    public PasswordEncoder get() {
      //noinspection deprecation
      return new LdapShaPasswordEncoder(KeyGenerators.shared(0));
    }
  }

}
