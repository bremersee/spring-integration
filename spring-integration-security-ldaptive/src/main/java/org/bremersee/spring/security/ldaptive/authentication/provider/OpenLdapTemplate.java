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

package org.bremersee.spring.security.ldaptive.authentication.provider;

import java.io.Serial;

/**
 * Template settings for OpenLDAP.
 *
 * @author Christian Bremer
 */
public class OpenLdapTemplate
    extends UserContainsGroupsTemplate {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new Open ldap template.
   */
  public OpenLdapTemplate() {
    setFirstNameAttribute("givenName");
    setLastNameAttribute("sn");
    setEmailAttribute("mail");
    setMemberAttribute("memberOf");
    setPasswordAttribute(""); // userPassword
  }

}
