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

package org.bremersee.spring.security.ldaptive.authentication;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bremersee.spring.security.core.authority.mapping.CaseTransformation;
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

  /**
   * The username (like 'anna') to bind dn (like 'cn=anna,ou=people,dc=example,dc=org') converter.
   */
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
   * The password last set attribute (like 'pwdLastSet') can be used to activate the remember-me
   * functionality.
   */
  protected String passwordLastSetAttribute;

  /**
   * The filter to find the user. If it is empty, it will be generated from {@code userObjectClass}
   * and {@code usernameAttribute} like this {@code (&(objectClass=inetOrgPerson)(uid={0}))}.
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

  /**
   * The account control evaluator.
   */
  protected AccountControlEvaluatorProperty accountControlEvaluator;

  /**
   * The group fetch strategy.
   */
  protected GroupFetchStrategy groupFetchStrategy;

  /**
   * The member attribute.
   */
  protected String memberAttribute;

  /**
   * The group base dn (like 'ou=groups,dc=example,dc=org'). It's only required, if
   * {@code groupFetchStrategy} is set to {@code GROUP_CONTAINS_USERS}.
   */
  protected String groupBaseDn;

  /**
   * The group search scope. It's only required, if {@code groupFetchStrategy} is set to
   * {@code GROUP_CONTAINS_USERS},
   */
  protected SearchScope groupSearchScope;

  /**
   * The group object class. It's only required, if {@code groupFetchStrategy} is set to
   * {@code GROUP_CONTAINS_USERS}
   */
  protected String groupObjectClass;

  /**
   * The group id attribute. It's only required, if {@code groupFetchStrategy} is set to
   * {@code GROUP_CONTAINS_USERS}
   */
  protected String groupIdAttribute;

  /**
   * The group member attribute. It's only required, if {@code groupFetchStrategy} is set to
   * {@code GROUP_CONTAINS_USERS}
   */
  protected String groupMemberAttribute;

  /**
   * The group member format. It's only required, if {@code groupFetchStrategy} is set to
   * {@code GROUP_CONTAINS_USERS}
   */
  protected String groupMemberFormat;

  /**
   * The role mappings.
   */
  protected List<RoleMapping> roleMapping;

  /**
   * The default roles.
   */
  protected List<String> defaultRoles;

  /**
   * The role prefix (like 'ROLE_').
   */
  protected String rolePrefix;

  /**
   * The role case transformation.
   */
  protected CaseTransformation roleCaseTransformation;

  /**
   * The string replacements for roles.
   */
  protected List<StringReplacement> roleStringReplacements;

  /**
   * To role mappings map.
   *
   * @return the map
   */
  public Map<String, String> toRoleMappings() {
    return Stream.ofNullable(getRoleMapping())
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(
            RoleMapping::getSource,
            RoleMapping::getTarget,
            (first, second) -> first,
            LinkedHashMap::new));
  }

  /**
   * To role string replacements map.
   *
   * @return the map
   */
  public Map<String, String> toRoleStringReplacements() {
    return Stream.ofNullable(getRoleStringReplacements())
        .flatMap(Collection::stream)
        .collect(Collectors.toMap(
            StringReplacement::getRegex,
            StringReplacement::getReplacement,
            (first, second) -> first,
            LinkedHashMap::new));
  }

  /**
   * The ldaptive authentication properties with defaults.
   */
  public static class WithDefaults extends LdaptiveAuthenticationProperties {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new ldaptive authentication properties with defaults.
     */
    public WithDefaults() {
      usernameToBindDnConverter = UsernameToBindDnConverterProperty.BY_USER_RDN_ATTRIBUTE;

      userObjectClass = "inetOrgPerson";
      usernameAttribute = "uid";

      userFindOneSearchScope = SearchScope.ONELEVEL;
      firstNameAttribute = "givenName";
      lastNameAttribute = "sn";
      emailAttribute = "mail";
      memberAttribute = "memberOf";
      accountControlEvaluator = AccountControlEvaluatorProperty.NONE;

      groupFetchStrategy = GroupFetchStrategy.USER_CONTAINS_GROUPS;
      groupObjectClass = "groupOfUniqueNames";
      groupMemberAttribute = "uniqueMember";
      groupSearchScope = SearchScope.ONELEVEL;
      roleMapping = new ArrayList<>();
      defaultRoles = new ArrayList<>();
      roleStringReplacements = new ArrayList<>();
      roleCaseTransformation = CaseTransformation.NONE;
    }

    /**
     * Get user rdn attribute.
     *
     * @return the user rdn attribute
     */
    @Override
    public String getUserRdnAttribute() {
      if (ObjectUtils.isEmpty(userRdnAttribute)) {
        return usernameAttribute;
      }
      return userRdnAttribute;
    }

    /**
     * Get user find one filter.
     *
     * @return the user find one filter
     */
    @Override
    public String getUserFindOneFilter() {
      if (ObjectUtils.isEmpty(userFindOneFilter)
          && !ObjectUtils.isEmpty(getUserObjectClass())
          && !ObjectUtils.isEmpty(getUsernameAttribute())) {
        return String
            .format("(&(objectClass=%s)(%s={0}))", getUserObjectClass(), getUsernameAttribute());
      }
      return userFindOneFilter;
    }

  }

  /**
   * The group fetch strategy.
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

  /**
   * The string replacement.
   */
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

  /**
   * The role mapping.
   */
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RoleMapping implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * The value from the ldap (like 'developers').
     */
    private String source;

    /**
     * The value in the spring security context (like 'ROLE_DEVELOPER').
     */
    private String target;
  }

}
