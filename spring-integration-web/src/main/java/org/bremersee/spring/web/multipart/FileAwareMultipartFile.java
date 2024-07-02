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

package org.bremersee.spring.web.multipart;

import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.util.FileCopyUtils;
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
   * Instantiates a new file aware multipart file.
   *
   * @param multipartFile the multipart file
   * @throws IOException the io exception
   */
  public FileAwareMultipartFile(MultipartFile multipartFile) throws IOException {
    this(multipartFile, (File) null);
  }

  /**
   * Instantiates a new file aware multipart file.
   *
   * @param multipartFile the multipart file
   * @param tmpDir the tmp dir
   * @throws IOException the io exception
   */
  public FileAwareMultipartFile(MultipartFile multipartFile, String tmpDir) throws IOException {
    this(multipartFile, getTmpDir(tmpDir));
  }

  /**
   * Instantiates a new file aware multipart file.
   *
   * @param multipartFile the multipart file
   * @param tmpDir the tmp dir
   * @throws IOException the io exception
   */
  public FileAwareMultipartFile(MultipartFile multipartFile, File tmpDir) throws IOException {
    if (multipartFile == null) {
      this.file = null;
      this.parameterName = null;
      this.originalFilename = null;
      this.contentType = null;
    } else {
      if (multipartFile.isEmpty()) {
        this.file = null;
      } else {
        if (multipartFile instanceof FileAwareMultipartFile) {
          this.file = ((FileAwareMultipartFile) multipartFile).file;
        } else {
          this.file = getTmpFile(getTmpDir(tmpDir));
          FileCopyUtils.copy(
              multipartFile.getInputStream(),
              Files.newOutputStream(
                  this.file.toPath(),
                  StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING));
        }
      }
      this.parameterName = multipartFile.getName();
      this.originalFilename = multipartFile.getOriginalFilename();
      this.contentType = multipartFile.getContentType();
    }
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
    this(inputStream, (File) null, parameterName, originalFilename, contentType);
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
      String tmpDir,
      String parameterName,
      String originalFilename,
      String contentType) throws IOException {
    this(inputStream, getTmpDir(tmpDir), parameterName, originalFilename, contentType);
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
    if (nonNull(inputStream)) {
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
   * Instantiates a new File aware multipart file.
   *
   * @param file the file
   * @param parameterName the parameter name
   * @param originalFilename the original filename
   * @param contentType the content type
   */
  public FileAwareMultipartFile(
      Path file,
      String parameterName,
      String originalFilename,
      String contentType) {
    this.file = Optional.ofNullable(file)
        .map(Path::toFile)
        .orElse(null);
    this.parameterName = parameterName;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
  }

  /**
   * Instantiates a new File aware multipart file.
   *
   * @param file the file
   * @param parameterName the parameter name
   * @param originalFilename the original filename
   * @param contentType the content type
   */
  public FileAwareMultipartFile(
      File file,
      String parameterName,
      String originalFilename,
      String contentType) {
    this.file = file;
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

  /**
   * Delete.
   *
   * @param multipartFile the multipart file
   */
  public static void delete(MultipartFile multipartFile) {
    if (nonNull(multipartFile) && multipartFile.getResource().isFile()) {
      try {
        Files.delete(multipartFile.getResource().getFile().toPath());
      } catch (Exception ignored) {
        // ignored
      }
    }
  }

  private static File getTmpDir(String tmpDir) {
    return Optional.ofNullable(tmpDir)
        .filter(dir -> !dir.isBlank())
        .map(File::new)
        .map(FileAwareMultipartFile::getTmpDir)
        .orElse(null);
  }

  private static File getTmpDir(File tmpDir) {
    return Optional.ofNullable(tmpDir)
        .filter(dir -> dir.exists() && dir.isDirectory() && dir.canRead() && dir.canWrite())
        .orElseGet(() -> new File(System.getProperty("java.io.tmpdir")));
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
    return Optional.ofNullable(originalFilename)
        .filter(name -> !name.isBlank())
        .or(() -> Optional.ofNullable(file).map(File::getName))
        .orElse(null);
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
    return nonNull(file) && file.exists() && file.isFile() && file.canRead();
  }

  @SuppressWarnings("Lombok")
  @EqualsAndHashCode
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
