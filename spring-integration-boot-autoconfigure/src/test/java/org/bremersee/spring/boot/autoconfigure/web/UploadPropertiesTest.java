package org.bremersee.spring.boot.autoconfigure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.junit.jupiter.api.Test;

/**
 * The upload properties test.
 *
 * @author Christian Bremer
 */
class UploadPropertiesTest {

  /**
   * Equals and to string.
   */
  @Test
  void equalsAndToString() {
    UploadProperties a = new UploadProperties();
    UploadProperties b = new UploadProperties();
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertEquals(a.toString(), b.toString());
  }

  /**
   * Gets tmp dir.
   */
  @Test
  void getTmpDir() {
    UploadProperties properties = new UploadProperties();
    File value = new File(System.getProperty("java.io.tmpdir"));
    properties.setTmpDir(value);
    assertEquals(value, properties.getTmpDir());
  }
}