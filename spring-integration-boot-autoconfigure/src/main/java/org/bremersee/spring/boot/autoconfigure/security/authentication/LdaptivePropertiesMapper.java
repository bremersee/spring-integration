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

package org.bremersee.spring.boot.autoconfigure.security.authentication;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bremersee.spring.boot.autoconfigure.security.authentication.LdaptiveAuthenticationAutoConfiguration.PropertiesMapper;
import org.bremersee.spring.security.ldaptive.authentication.LdaptiveAuthenticationProperties;
import org.bremersee.spring.security.ldaptive.authentication.provider.Template;

/**
 * The ldaptive properties mapper.
 *
 * @author Christian Bremer
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LdaptivePropertiesMapper {

  /**
   * Map ldaptive authentication properties.
   *
   * @param properties the properties
   * @return the ldaptive authentication properties
   */
  public static LdaptiveAuthenticationProperties map(AuthenticationProperties properties) {
    LdaptiveAuthenticationProperties tmp = PropertiesMapper.INSTANCE.map(properties.getLdaptive());
    Template template;
    try {
      template = Template.valueOf(properties.getLdaptive().getTemplate().name());
    } catch (Exception ignored) {
      template = null;
    }
    return Optional
        .ofNullable(template)
        .map(t -> t.applyTemplate(tmp))
        .orElse(tmp);
  }

}
