/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.spring.boot.autoconfigure.thymeleaf;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * The additional thymeleaf properties.
 *
 * @author Christian Bremer
 */
@ConfigurationProperties("bremersee.thymeleaf")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class AdditionalThymeleafProperties {

  private List<ResolverProperties> resolvers = new ArrayList<>();

  /**
   * The additional thymeleaf resolver properties.
   */
  @Getter
  @Setter
  @ToString
  @EqualsAndHashCode
  public static class ResolverProperties {

    /**
     * The Cacheable.
     */
    private boolean cacheable = false;

    /**
     * The Cacheable patterns.
     */
    private Set<String> cacheablePatterns = new LinkedHashSet<>();

    /**
     * The Cache ttlms.
     */
    private Long cacheTtlms;

    /**
     * The Character encoding.
     */
    private String characterEncoding = StandardCharsets.UTF_8.name();

    /**
     * The Check existence.
     */
    private boolean checkExistence = false;

    /**
     * The Css template mode patterns.
     */
    private Set<String> cssTemplateModePatterns = new LinkedHashSet<>();

    /**
     * The Force suffix.
     */
    private boolean forceSuffix = false;

    /**
     * The Force template mode.
     */
    private boolean forceTemplateMode = false;

    /**
     * The Html template mode patterns.
     */
    private Set<String> htmlTemplateModePatterns = new LinkedHashSet<>();

    /**
     * The Java script template mode patterns.
     */
    private Set<String> javaScriptTemplateModePatterns = new LinkedHashSet<>();

    /**
     * The Name.
     */
    private String name;

    /**
     * The Non cacheable patterns.
     */
    private Set<String> nonCacheablePatterns = new LinkedHashSet<>();

    /**
     * The Prefix.
     */
    private String prefix = "classpath:"; // important

    /**
     * The Raw template mode patterns.
     */
    private Set<String> rawTemplateModePatterns = new LinkedHashSet<>();

    /**
     * The Resolvable patterns.
     */
    private Set<String> resolvablePatterns = new LinkedHashSet<>(); // important

    /**
     * The Suffix.
     */
    private String suffix = ".html";  // important

    /**
     * The Template aliases.
     */
    private Map<String, String> templateAliases = new LinkedHashMap<>();

    /**
     * The Template mode.
     */
    private TemplateMode templateMode;

    /**
     * The Text template mode patterns.
     */
    private Set<String> textTemplateModePatterns = new LinkedHashSet<>();

    /**
     * The Use decoupled logic.
     */
    private boolean useDecoupledLogic = false;

    /**
     * The Xml template mode patterns.
     */
    private Set<String> xmlTemplateModePatterns = new LinkedHashSet<>();

    /**
     * Resolvable patterns or default.
     *
     * @return the resolvable patterns
     */
    public Set<String> resolvablePatternsOrDefault() {
      if (resolvablePatterns.isEmpty()) {
        return Collections.singleton("*");
      }
      return resolvablePatterns;
    }

  }

}
