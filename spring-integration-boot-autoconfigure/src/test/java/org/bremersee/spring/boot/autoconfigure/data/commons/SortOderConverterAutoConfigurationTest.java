package org.bremersee.spring.boot.autoconfigure.data.commons;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.bremersee.comparator.model.SortOrdersTextProperties;
import org.bremersee.comparator.spring.converter.SortOrderConverter;
import org.bremersee.comparator.spring.converter.SortOrdersConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

/**
 * The type Sort oder converter autoconfiguration test.
 */
class SortOderConverterAutoConfigurationTest {

  private static SortOderConverterAutoConfiguration target;

  /**
   * Init.
   */
  @BeforeAll
  static void init() {
    target = new SortOderConverterAutoConfiguration();
    target.init();
  }

  /**
   * Sort order converter.
   */
  @Test
  void sortOrderConverter() {
    @SuppressWarnings("unchecked")
    ObjectProvider<SortOrdersTextProperties> provider = mock(ObjectProvider.class);
    doReturn(SortOrdersTextProperties.defaults())
        .when(provider)
        .getIfAvailable(any());
    SortOrderConverter actual = target.sortOrderConverter(provider);
    assertThat(actual).isNotNull();
  }

  /**
   * Sort orders converter.
   */
  @Test
  void sortOrdersConverter() {
    @SuppressWarnings("unchecked")
    ObjectProvider<SortOrdersTextProperties> provider = mock(ObjectProvider.class);
    doReturn(SortOrdersTextProperties.defaults())
        .when(provider)
        .getIfAvailable(any());
    SortOrdersConverter actual = target.sortOrdersConverter(provider);
    assertThat(actual).isNotNull();
  }
}