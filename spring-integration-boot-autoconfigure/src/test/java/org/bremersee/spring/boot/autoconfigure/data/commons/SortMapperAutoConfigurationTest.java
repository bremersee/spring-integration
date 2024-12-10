package org.bremersee.spring.boot.autoconfigure.data.commons;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class SortMapperAutoConfigurationTest {

  @Test
  void sortMapper() {
    SortMapperProperties properties = new SortMapperProperties();
    properties.setNativeNullHandlingIsNullIsFirst(true);
    properties.setNullHandlingSupported(true);
    SortMapperAutoConfiguration target = new SortMapperAutoConfiguration(properties);
    target.init();
    assertNotNull(target.sortMapper());
  }
}