package org.bremersee.spring.boot.autoconfigure.web.reactive;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.bremersee.spring.boot.autoconfigure.web.UploadProperties;
import org.bremersee.spring.web.reactive.multipart.MultipartFileBuilder;
import org.junit.jupiter.api.Test;

class MultipartFileBuilderAutoConfigurationTest {

  @Test
  void multipartFileBuilder() {
    UploadProperties props = new UploadProperties();
    MultipartFileBuilderAutoConfiguration target = new MultipartFileBuilderAutoConfiguration(props);
    target.init();
    MultipartFileBuilder actual = target.multipartFileBuilder();
    assertNotNull(actual);
  }

}