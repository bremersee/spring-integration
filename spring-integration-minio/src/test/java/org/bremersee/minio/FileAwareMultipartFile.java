/*
 * Copyright 2020 the original author or authors.
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

package org.bremersee.minio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * The file aware multipart file.
 *
 * @author Christian Bremer
 */
@EqualsAndHashCode(doNotUseGetters = true)
@ToString(doNotUseGetters = true)
public class FileAwareMultipartFile implements MultipartFile {

  private final File file;

  private final String parameterName;

  private final String originalFilename;

  private final String contentType;

  private FileAwareMultipartFile() {
    this.file = null;
    this.parameterName = null;
    this.originalFilename = null;
    this.contentType = null;
  }

  /**
   * Instantiates a new File aware multipart file.
   *
   * @param inputStream the input stream
   * @param parameterName the parameter name
   * @param originalFilename the original filename
   * @param contentType the content type
   * @throws IOException the io exception
   */
  public FileAwareMultipartFile(
      InputStream inputStream,
      String parameterName,
      String originalFilename,
      String contentType) throws IOException {
    this(inputStream, null, parameterName, originalFilename, contentType);
  }

  /**
   * Instantiates a new File aware multipart file.
   *
   * @param inputStream the input stream
   * @param tmpDir the tmp dir
   * @param parameterName the parameter name
   * @param originalFilename the original filename
   * @param contentType the content type
   * @throws IOException the io exception
   */
  public FileAwareMultipartFile(
      InputStream inputStream,
      File tmpDir,
      String parameterName,
      String originalFilename,
      String contentType) throws IOException {
    if (inputStream != null) {
      this.file = getTmpFile(tmpDir);
      FileCopyUtils.copy(
          inputStream,
          Files.newOutputStream(
              this.file.toPath(),
              StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));
    } else {
      this.file = null;
    }
    this.parameterName = parameterName;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
  }

  /**
   * Empty file aware multipart file.
   *
   * @return the file aware multipart file
   */
  public static FileAwareMultipartFile empty() {
    return new FileAwareMultipartFile();
  }

  private static File getTmpFile(File tmpDir) throws IOException {
    return File.createTempFile("uploaded-", ".tmp", tmpDir);
  }

  @NonNull
  @Override
  public String getName() {
    return parameterName == null ? "" : parameterName;
  }

  @Override
  public String getOriginalFilename() {
    return !StringUtils.hasText(originalFilename) && file != null
        ? file.getName()
        : originalFilename;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public boolean isEmpty() {
    return getSize() == 0;
  }

  @Override
  public long getSize() {
    return isFileValid() ? file.length() : 0;
  }

  @NonNull
  @Override
  public byte[] getBytes() throws IOException {
    return isEmpty() ? new byte[0] : FileCopyUtils.copyToByteArray(new FileInputStream(file));
  }

  @NonNull
  @Override
  public InputStream getInputStream() throws IOException {
    return isEmpty() ? new ByteArrayInputStream(new byte[0]) : new FileInputStream(file);
  }

  @NonNull
  @Override
  public Resource getResource() {
    return isEmpty() ? new EmptyResource() : new FileSystemResource(file);
  }

  @Override
  public void transferTo(@NonNull File dest) throws IOException, IllegalStateException {
    if (isFileValid()) {
      FileCopyUtils.copy(file, dest);
    }
  }

  private boolean isFileValid() {
    return file != null && file.exists() && file.isFile() && file.canRead();
  }

  @EqualsAndHashCode(callSuper = false)
  private static class EmptyResource extends AbstractResource {

    private EmptyResource() {
    }

    @NonNull
    @Override
    public String getDescription() {
      return "Empty resource.";
    }

    @NonNull
    @Override
    public InputStream getInputStream() {
      return new ByteArrayInputStream(new byte[0]);
    }

    @NonNull
    @Override
    public String toString() {
      return getDescription();
    }
  }

}
