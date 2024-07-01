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

package org.bremersee.spring.security.authentication.ldaptive;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bremersee.spring.security.authentication.CaseTransformation;
import org.ldaptive.SearchScope;
import org.springframework.util.ObjectUtils;

/**
 * The ldaptive authentication properties.
 *
 * @author Christian Bremer
 */
@Data
@NoArgsConstructor
public class LdaptiveAuthenticationProperties implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  protected UsernameToBindDnConverterProperty usernameToBindDnConverter;

  /**
   * The user base dn (like 'ou=people,dc=example,dc=org'). This value is always required.
   */
  protected String userBaseDn;

  /**
   * The object class of the user (like 'inetOrgPerson'). The selected template contains a default.
   */
  protected String userObjectClass;

  /**
   * The username attribute of the user (like 'uid' or 'sAMAccountName'). The selected template
   * contains a default.
   */
  protected String usernameAttribute;

  /**
   * Applies only for simple bind. The rdn attribute of the user. This is normally the same as the
   * username attribute.
   */
  protected String userRdnAttribute;

  /**
   * The password attribute of the user (like 'userPassword'). If it is empty, a simple user bind
   * will be done with the credentials of the user for authentication. If it is present, the
   * connection to the ldap server must be done by a 'global' user and a password encoder that fits
   * your requirements must be present. The default password encoder only supports SHA, that is
   * insecure.
   */
  protected String passwordAttribute;

  /**
   * The filter to find the user. If it is empty, it will be generated from 'userObjectClass' and
   * 'usernameAttribute' like this '(&(objectClass=inetOrgPerson)(uid={0}))'.
   */
  protected String userFindOneFilter;

  /**
   * The scope to find a user. Default is 'one level'.
   */
  protected SearchScope userFindOneSearchScope;

  /**
   * The first name attribute of the user. Default is 'givenName'.
   */
  protected String firstNameAttribute;

  /**
   * The last name attribute of the user. Default is 'sn'.
   */
  protected String lastNameAttribute;

  /**
   * The email attribute of the user. Default is 'mail';
   */
  protected String emailAttribute;

  protected AccountControlEvaluatorProperty accountControlEvaluator;


  protected GroupFetchStrategy groupFetchStrategy;

  protected String memberAttribute;

  protected String groupBaseDn;

  protected SearchScope groupSearchScope;

  protected String groupObjectClass;

  protected String groupIdAttribute;

  protected String groupMemberAttribute;

  protected String groupMemberFormat;

  protected List<RoleMapping> roleMapping;

  protected List<String> defaultRoles;

  protected String rolePrefix;

  protected CaseTransformation roleCaseTransformation;

  protected List<StringReplacement> roleStringReplacements;

  public static class WithDefaults extends LdaptiveAuthenticationProperties {

    @Serial
    private static final long serialVersionUID = 1L;

    public WithDefaults() {
      usernameToBindDnConverter = UsernameToBindDnConverterProperty.BY_USER_RDN_ATTRIBUTE;

      userObjectClass = "inetOrgPerson";
      usernameAttribute = "uid";

      userFindOneSearchScope = SearchScope.ONELEVEL;
      firstNameAttribute = "givenName";
      lastNameAttribute = "sn";
      emailAttribute = "mail";
      accountControlEvaluator = AccountControlEvaluatorProperty.NONE;

      groupFetchStrategy = GroupFetchStrategy.USER_CONTAINS_GROUPS;
      groupMemberAttribute = "memberOf";
      groupIdAttribute = "cn";
      groupSearchScope = SearchScope.ONELEVEL;
      roleMapping = new ArrayList<>();
      defaultRoles = new ArrayList<>();
      roleStringReplacements = new ArrayList<>();
      roleCaseTransformation = CaseTransformation.NONE;
    }

    @Override
    public String getUserRdnAttribute() {
      if (ObjectUtils.isEmpty(userRdnAttribute)) {
        return usernameAttribute;
      }
      return userRdnAttribute;
    }

    @Override
    public String getUserFindOneFilter() {
      if (ObjectUtils.isEmpty(userFindOneFilter)
          && !ObjectUtils.isEmpty(getUserObjectClass())
          && !ObjectUtils.isEmpty(getUsernameAttribute())) {
        return String
            .format("(&(objectClass=%s)(%s={0}))", getUserObjectClass(), getUserRdnAttribute());
      }
      return userFindOneFilter;
    }

  }

  /**
   * The enum Group fetch strategy.
   */
  public enum GroupFetchStrategy {

    /**
     * Groups will not be fetched.
     */
    NONE,

    /**
     * User contains groups group-fetch strategy.
     */
    USER_CONTAINS_GROUPS,

    /**
     * Group contains users group-fetch strategy.
     */
    GROUP_CONTAINS_USERS
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class StringReplacement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The regular expression to which the string is to be matched. '{@code [- ]}' for example would
     * replace every '-' and every space.
     */
    private String regex;

    /**
     * The string to be substituted for each match.
     */
    private String replacement;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoleMapping implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String source;

    private String target;
  }

}
