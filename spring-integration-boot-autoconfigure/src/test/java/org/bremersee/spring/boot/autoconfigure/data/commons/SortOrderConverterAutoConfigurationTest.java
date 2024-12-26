package org.bremersee.spring.boot.autoconfigure.data.commons;

import static org.assertj.core.api.Assertions.assertThat;

import org.bremersee.comparator.spring.converter.SortOrderConverter;
import org.bremersee.comparator.spring.converter.SortOrderItemConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The sort order converter autoconfiguration test.
 */
class SortOrderConverterAutoConfigurationTest {

  private static SortOrderConverterAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeAll
  static void init() {
    target = new SortOrderConverterAutoConfiguration(new SortOrderConverterProperties());
    target.init();
  }

  /**
   * Sort order converter.
   */
  @Test
  void sortOrderConverter() {
    SortOrderConverter actual = target.sortOrderConverter();
    assertThat(actual).isNotNull();
  }

  /**
   * Sort order iten converter.
   */
  @Test
  void sortOrderItemConverter() {
    SortOrderItemConverter actual = target.sortOrderItemConverter();
    assertThat(actual).isNotNull();
  }
}