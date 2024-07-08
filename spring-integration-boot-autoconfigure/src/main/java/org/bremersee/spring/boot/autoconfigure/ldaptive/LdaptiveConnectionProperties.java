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

package org.bremersee.spring.boot.autoconfigure.ldaptive;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The ldaptive connection properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties(prefix = "bremersee.ldaptive")
@Getter
@Setter
@ToString(exclude = {"bindCredentials"})
@EqualsAndHashCode(exclude = {"bindCredentials"})
@NoArgsConstructor
public class LdaptiveConnectionProperties {

  /**
   * The ldaptive template class.
   */
  private Class<?> ldaptiveTemplateClass;

  /**
   * Specifies whether the connection configuration is immutable or not.
   */
  private boolean immutable;

  /**
   * URL of the LDAP server(s) separated by space. For example
   * {@code ldaps://ldap1.example.org:636 ldaps://ldap2.example.org:636}.
   */
  private String ldapUrl;

  /**
   * Duration of time that connects will block.
   */
  private Duration connectTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time to wait for startTLS responses.
   */
  private Duration startTlsTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time to wait for responses.
   */
  private Duration responseTimeout = Duration.ofMinutes(1);

  /**
   * Duration of time that operations will block on reconnects, should generally be longer than
   * connect timeout.
   */
  private Duration reconnectTimeout = Duration.ofMinutes(2);

  /**
   * Whether to automatically reconnect to the server when a connection is lost. Default is true.
   */
  private boolean autoReconnect = true;

  /**
   * The reconnect strategy.
   */
  private ReconnectStrategy reconnectStrategy = ReconnectStrategy.ONE_RECONNECT_ATTEMPT;

  /**
   * Whether pending operations should be replayed after a reconnect. Default is true.
   */
  private boolean autoReplay = true;

  /**
   * The ssl configuration.
   */
  private SslProperties sslConfig = new SslProperties();

  /**
   * Connect to LDAP using startTLS.
   */
  private boolean useStartTls;

  /**
   * DN to bind as before performing operations.
   */
  private String bindDn;

  /**
   * Credential for the bind DN.
   */
  private String bindCredentials;

  /**
   * Perform a fast bind, if no credentials are present.
   */
  private boolean fastBind = false;

  /**
   * The connection strategy.
   */
  private ConnectionStrategy connectionStrategy = ConnectionStrategy.ACTIVE_PASSIVE;

  /**
   * The connection validator.
   */
  private ConnectionValidatorProperties connectionValidator = new ConnectionValidatorProperties();

  /**
   * Specifies whether the connection should be pooled or not. Default is {@code false}.
   */
  private boolean pooled = false;

  /**
   * The connection pool configuration.
   */
  private ConnectionPoolProperties connectionPool = new ConnectionPoolProperties();

  /**
   * The reconnection strategy.
   */
  public enum ReconnectStrategy {

    /**
     * One reconnect attempt strategy.
     */
    ONE_RECONNECT_ATTEMPT,

    /**
     * Infinite reconnect attempts strategy.
     */
    INFINITE_RECONNECT_ATTEMPTS,

    /**
     * Infinite reconnect attempts with backoff strategy.
     */
    INFINITE_RECONNECT_ATTEMPTS_WITH_BACKOFF

  }

  /**
   * The ssl configuration.
   */
  @Data
  public static class SslProperties {

    /**
     * Path of the trust certificates to use for the SSL connection.
     */
    private String trustCertificates;

    /**
     * Path of the authentication certificate to use for the SSL connection.
     */
    private String authenticationCertificate;

    /**
     * Path of the key to use for the SSL connection.
     */
    private String authenticationKey;

  }

  /**
   * The connection strategy.
   */
  public enum ConnectionStrategy {

    /**
     * Attempt each URL in the order provided for each connection. The URLs are always tried in the
     * order in which they were provided.
     */
    ACTIVE_PASSIVE,

    /**
     * Attempt a random URL from a list of URLs.
     */
    RANDOM,

    /**
     * Attempt the next URL in the order provided for each connection. URLs are rotated regardless
     * of connection success or failure.
     */
    ROUND_ROBIN,

    /**
     * Queries a DNS server for SRV records and uses those records to construct a list of URLs. When
     * configuring this strategy you must use your DNS server for {@code setLdapUrl(String)} in the
     * form {@code dns://my.server.com}.
     */
    DNS

  }

  /**
   * The search validator properties.
   */
  @Data
  public static class ConnectionValidatorProperties {

    /**
     * Validation period.
     */
    private Duration validatePeriod = Duration.ofMinutes(30);

    /**
     * Maximum length of time a connection validation should block.
     */
    private Duration validateTimeout = Duration.ofSeconds(5);

    /**
     * The search request.
     */
    private SearchRequestProperties searchRequest = new SearchRequestProperties();

    /**
     * The search request properties.
     */
    @Data
    public static class SearchRequestProperties {

      /**
       * The base dn (like {@code ou=peoples,dc=example,dc=org}).
       */
      private String baseDn;

      /**
       * The search filter.
       */
      private SearchFilterProperties searchFilter = new SearchFilterProperties();

      /**
       * The size limit.
       */
      private Integer sizeLimit;

      /**
       * The search scope.
       */
      private SearchScope searchScope;

      /**
       * The return attributes.
       */
      private List<String> returnAttributes = new ArrayList<>();

      /**
       * The search filter properties.
       */
      @Data
      public static class SearchFilterProperties {

        /**
         * The search filter (like {@code (&(objectClass=inetOrgPerson)(uid=administrator))}).
         */
        private String filter;

      }
    }
  }

  /**
   * The connection pool properties.
   */
  @Data
  public static class ConnectionPoolProperties {

    /**
     * Duration to wait for an available connection.
     */
    private Duration blockWaitTime = Duration.ofMinutes(1);

    /**
     * Minimum pool size.
     */
    private int minPoolSize = 3;

    /**
     * Maximum pool size.
     */
    private int maxPoolSize = 10;

    /**
     * Whether to connect to the ldap on connection creation.
     */
    private boolean connectOnCreate = true;

    /**
     * Whether initialize should throw if pooling configuration requirements are not met.
     */
    private boolean failFastInitialize = true;

    /**
     * Whether the ldap object should be validated when returned to the pool.
     */
    private boolean validateOnCheckIn = false;

    /**
     * Whether the ldap object should be validated when given from the pool.
     */
    private boolean validateOnCheckOut = false;

    /**
     * Whether the pool should be validated periodically.
     */
    private boolean validatePeriodically = false;

    /**
     * The validator for the connections in the pool.
     */
    private ConnectionValidatorProperties validator = new ConnectionValidatorProperties();

    /**
     * Prune period.
     */
    private Duration prunePeriod = Duration.ofMinutes(5);

    /**
     * Idle time.
     */
    private Duration idleTime = Duration.ofMinutes(10);
  }

  /**
   * The enum Search scope.
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
}
