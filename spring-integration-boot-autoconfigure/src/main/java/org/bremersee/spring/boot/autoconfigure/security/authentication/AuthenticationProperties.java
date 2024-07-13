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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The authentication properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.authentication")
@Data
public class AuthenticationProperties {

  /**
   * The remember-me properties.
   */
  private RememberMeProperties rememberMe = new RememberMeProperties();

  /**
   * The jwt converter properties.
   */
  private JwtConverterProperties jwtConverter = new JwtConverterProperties();

  /**
   * The ldaptive properties.
   */
  private LdaptiveProperties ldaptive = new LdaptiveProperties();

  /**
   * The remember-me properties.
   *
   * @author Christian Bremer
   */
  @Data
  public static class RememberMeProperties {

    /**
     * The key.
     */
    private String key;

    /**
     * Specifies whether remember-me is always activated.
     */
    private Boolean alwaysRemember;

    /**
     * The cookie name.
     */
    private String cookieName;

    /**
     * The cookie domain.
     */
    private String cookieDomain;

    /**
     * Specifies whether to use secure cookie.
     */
    private Boolean useSecureCookie;

    /**
     * The parameter name (default remember-me).
     */
    private String parameterName;

    /**
     * The token validity in seconds (default two weeks).
     */
    private Integer tokenValiditySeconds;
  }

  /**
   * The jwt converter properties.
   */
  @Data
  public static class JwtConverterProperties {

    /**
     * The json path to the username.
     */
    private String nameJsonPath = "$.sub"; // keycloak: $.preferred_username

    /**
     * The json path to the first name.
     */
    private String firstNameJsonPath = "$.given_name";

    /**
     * The json path to the last name.
     */
    private String lastNameJsonPath = "$.family_name";

    /**
     * The json path to the email.
     */
    private String emailJsonPath = "$.email";

    /**
     * The json path to the roles.
     */
    private String rolesJsonPath = "$.scope"; // keycloak: $.realm_access.roles

    /**
     * Specifies whether the roles are represented as a json array or as a list separated by
     * {@link #getRolesValueSeparator()}.
     */
    private boolean rolesValueList = false; // keycloak: true

    /**
     * The roles separator to use if {@link #isRolesValueList()} is set to {@code false}.
     */
    private String rolesValueSeparator = " ";

    /**
     * The default roles.
     */
    private List<String> defaultRoles = new ArrayList<>();

    /**
     * The role mappings.
     */
    private List<RoleMapping> roleMapping = new ArrayList<>();

    /**
     * The role prefix (like 'ROLE_' or 'SCOPE_').
     */
    private String rolePrefix = "SCOPE_"; // keycloak: ROLE_

    /**
     * The role case transformation.
     */
    private CaseTransformation roleCaseTransformation;

    /**
     * The string replacements for roles.
     */
    private List<StringReplacement> roleStringReplacements;

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

  }

  /**
   * The ldaptive properties.
   */
  @Data
  public static class LdaptiveProperties {

    /**
     * The ldap template with default configuration properties.
     */
    private Template template = Template.ACTIVE_DIRECTORY;

    /**
     * The username to bind dn converter property.
     */
    private UsernameToBindDnConverterProperty usernameToBindDnConverter;

    /**
     * The user base dn (like 'ou=people,dc=example,dc=org'). This value is always required.
     */
    private String userBaseDn;

    /**
     * The object class of the user (like 'inetOrgPerson'). The selected template contains a
     * default.
     */
    private String userObjectClass;

    /**
     * The username attribute of the user (like 'uid' or 'sAMAccountName'). The selected template
     * contains a default.
     */
    private String usernameAttribute;

    /**
     * Applies only for simple bind. The rdn attribute of the user. This is normally the same as the
     * username attribute.
     */
    private String userRdnAttribute;

    /**
     * The password attribute of the user (like 'userPassword'). If it is empty, a simple user bind
     * will be done with the credentials of the user for authentication. If it is present, the
     * connection to the ldap server must be done by a 'global' user and a password encoder that
     * fits your requirements must be present. The default password encoder only supports SHA, that
     * is insecure.
     */
    private String passwordAttribute;

    /**
     * The password last set attribute (like 'pwdLastSet') can be used to activate the remember-me
     * functionality.
     */
    private String passwordLastSetAttribute;

    /**
     * The filter to find the user. If it is empty, it will be generated from 'userObjectClass' and
     * 'usernameAttribute' like this '(&(objectClass=inetOrgPerson)(uid={0}))'.
     */
    private String userFindOneFilter;

    /**
     * The scope to find a user. Default is 'one level'.
     */
    private SearchScope userFindOneSearchScope;

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
    private String emailAttribute;

    /**
     * The account control evaluator.
     */
    private AccountControlEvaluatorProperty accountControlEvaluator;

    /**
     * The group fetch strategy.
     */
    private GroupFetchStrategy groupFetchStrategy;

    /**
     * The member attribute.
     */
    private String memberAttribute;

    /**
     * The group base dn (like 'ou=groups,dc=example,dc=org'). It's only required, if
     * {@code groupFetchStrategy} is set to {@code GROUP_CONTAINS_USERS}.
     */
    private String groupBaseDn;

    /**
     * The group search scope. It's only required, if {@code groupFetchStrategy} is set to
     * {@code GROUP_CONTAINS_USERS},
     */
    private SearchScope groupSearchScope;

    /**
     * The group object class. It's only required, if {@code groupFetchStrategy} is set to
     * {@code GROUP_CONTAINS_USERS}
     */
    private String groupObjectClass;

    /**
     * The group id attribute. It's only required, if {@code groupFetchStrategy} is set to
     * {@code GROUP_CONTAINS_USERS}
     */
    private String groupIdAttribute;

    /**
     * The group member attribute. It's only required, if {@code groupFetchStrategy} is set to
     * {@code GROUP_CONTAINS_USERS}
     */
    private String groupMemberAttribute;

    /**
     * The group member format. It's only required, if {@code groupFetchStrategy} is set to
     * {@code GROUP_CONTAINS_USERS}
     */
    private String groupMemberFormat;

    /**
     * The role mappings.
     */
    private List<RoleMapping> roleMapping;

    /**
     * The default roles.
     */
    private List<String> defaultRoles;

    /**
     * The role prefix (like 'ROLE_').
     */
    private String rolePrefix;

    /**
     * The role case transformation.
     */
    private CaseTransformation roleCaseTransformation;

    /**
     * The string replacements for roles.
     */
    private List<StringReplacement> roleStringReplacements;

    /**
     * The search scope.
     */
    public enum SearchScope {

      /**
       * Base object search.
       */
      OBJECT,

      /**
       * Single level search.
       */
      ONELEVEL,

      /**
       * Whole subtree search.
       */
      SUBTREE,

      /**
       * Subordinate subtree search. See draft-sermersheim-ldap-subordinate-scope.
       */
      SUBORDINATE
    }

    /**
     * The username to bind dn converter property.
     */
    public enum UsernameToBindDnConverterProperty {

      /**
       * By user rdn attribute username to bind dn converter property.
       */
      BY_USER_RDN_ATTRIBUTE,

      /**
       * By domain email username to bind dn converter property.
       */
      BY_DOMAIN_EMAIL
    }

    /**
     * The account control evaluator property.
     */
    public enum AccountControlEvaluatorProperty {

      /**
       * The None.
       */
      NONE,

      /**
       * The Active directory.
       */
      ACTIVE_DIRECTORY
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
     * The templates for ldap authentication.
     *
     * @author Christian Bremer
     */
    public enum Template {

      /**
       * Active directory template.
       */
      ACTIVE_DIRECTORY,

      /**
       * Open ldap template.
       */
      OPEN_LDAP,

      /**
       * User contains groups template.
       */
      USER_CONTAINS_GROUPS,

      /**
       * Group contains users template.
       */
      GROUP_CONTAINS_USERS
    }
  }

  /**
   * The role mapping.
   */
  @Data
  public static class RoleMapping {

    private String source;

    private String target;
  }

  /**
   * The case transformation.
   */
  public enum CaseTransformation {

    /**
     * None case transformation.
     */
    NONE,

    /**
     * To upper case transformation.
     */
    TO_UPPER_CASE,

    /**
     * To lower case transformation.
     */
    TO_LOWER_CASE
  }

  /**
   * The string replacement.
   */
  @Data
  public static class StringReplacement {

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

}
