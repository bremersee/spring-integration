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
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The type AuthenticationProperties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.authentication")
@Data
public class AuthenticationProperties {

  private JwtConverterProperties jwtConverter = new JwtConverterProperties();

  private LdaptiveProperties ldaptive = new LdaptiveProperties();

  @Data
  public static class JwtConverterProperties {

    private String nameJsonPath = "$.sub";

    private String firstNameJsonPath = "$.given_name";

    private String lastNameJsonPath = "$.family_name";

    private String emailJsonPath = "$.email";

    private String rolesJsonPath = "$.scope"; // $.realm_access.roles

    private boolean rolesValueList = false; // true

    private String rolesValueSeparator = " ";

    private List<String> defaultRoles = new ArrayList<>();

    private List<RoleMapping> roleMapping = new ArrayList<>();

    private String rolePrefix = "SCOPE_"; // ROLE_

    private CaseTransformation roleCaseTransformation;

    private List<StringReplacement> roleStringReplacements;

  }

  @Data
  public static class LdaptiveProperties {

    private Template template = Template.ACTIVE_DIRECTORY;

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

    private AccountControlEvaluatorProperty accountControlEvaluator;

    private GroupFetchStrategy groupFetchStrategy;

    private String memberAttribute;

    private String groupBaseDn;

    private SearchScope groupSearchScope;

    private String groupObjectClass;

    private String groupIdAttribute;

    private String groupMemberAttribute;

    private String groupMemberFormat;

    private List<RoleMapping> roleMapping;

    private List<String> defaultRoles;

    private String rolePrefix;

    private CaseTransformation roleCaseTransformation;

    private List<StringReplacement> roleStringReplacements;

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

    public enum UsernameToBindDnConverterProperty {

      BY_USER_RDN_ATTRIBUTE,

      BY_DOMAIN_EMAIL
    }

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
