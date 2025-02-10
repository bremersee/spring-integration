package org.bremersee.spring.boot.autoconfigure.data.commons;

import lombok.extern.slf4j.Slf4j;
import org.bremersee.comparator.spring.mapper.SortMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.util.ClassUtils;

/**
 * The sort order mapper autoconfiguration.
 */
@ConditionalOnClass(name = {
    "org.bremersee.comparator.spring.mapper.SortMapper"
})
@AutoConfiguration
@Slf4j
public class SortOrderMapperAutoConfiguration {

  /**
   * Init.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void init() {
    log.info("""
            
            *********************************************************************************
            * {}
            *********************************************************************************""",
        ClassUtils.getUserClass(getClass()).getSimpleName());
  }

  /**
   * Creates sort mapper.
   *
   * @return the sort mapper
   */
  @ConditionalOnMissingBean
  @Bean
  public SortMapper defaultSortMapper() {
    return SortMapper.defaultSortMapper();
  }

}
