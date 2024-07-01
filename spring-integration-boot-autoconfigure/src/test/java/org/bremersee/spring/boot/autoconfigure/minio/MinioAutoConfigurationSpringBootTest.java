package org.bremersee.spring.boot.autoconfigure.minio;

import static org.assertj.core.api.Assertions.assertThat;

import io.minio.MinioClient;
import org.bremersee.minio.MinioTemplate;
import org.bremersee.spring.boot.autoconfigure.minio.app.TestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(
    classes = TestConfiguration.class,
    webEnvironment = WebEnvironment.NONE,
    properties = {
        "spring.application.name=minio-test",
        "bremersee.minio.url=https://play.min.io",
        "bremersee.minio.access-key=Q3AM3UQ867SPQQA43P2F",
        "bremersee.minio.secret-key=zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG",
    })
class MinioAutoConfigurationSpringBootTest {

  @Autowired(required = false)
  MinioClient minioClient;

  @Autowired(required = false)
  MinioTemplate minioTemplate;

  @Test
  void minioClient() {
    assertThat(minioClient)
        .isNotNull();
  }

  @Test
  void minioTemplate() {
    assertThat(minioTemplate)
        .isNotNull();
  }
}