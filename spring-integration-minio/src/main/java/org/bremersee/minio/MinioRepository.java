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

import io.minio.ObjectWriteResponse;
import io.minio.http.Method;
import io.minio.messages.DeleteError;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

/**
 * The minio repository interface.
 *
 * @author Christian Bremer
 */
public interface MinioRepository {

  /**
   * Gets minio operations.
   *
   * @return the minio operations
   */
  MinioOperations getMinioOperations();

  /**
   * Gets region.
   *
   * @return the region
   */
  String getRegion();

  /**
   * Gets bucket.
   *
   * @return the bucket
   */
  String getBucket();

  /**
   * Is versioning enabled boolean.
   *
   * @return the boolean
   */
  boolean isVersioningEnabled();

  /**
   * Save multipart file.
   *
   * @param id the id
   * @param multipartFile the multipart file
   * @param deleteMode the delete mode
   * @return the write response; will be empty, if the multipart file is {@code null} or empty
   */
  Optional<ObjectWriteResponse> save(
      MinioObjectId id,
      @Nullable MultipartFile multipartFile,
      DeleteMode deleteMode);

  /**
   * Checks whether an object with the specified name exists or not.
   *
   * @param id the id
   * @return {@code true} if the object exists, otherwise {@code false}
   */
  boolean exists(MinioObjectId id);

  /**
   * Find one.
   *
   * @param id the id
   * @return the multipart file
   */
  Optional<MinioMultipartFile> findOne(MinioObjectId id);

  /**
   * Find all objects.
   *
   * @return the list
   */
  default List<MinioMultipartFile> findAll() {
    return findAll(null);
  }

  /**
   * Find all objects.
   *
   * @param prefix the prefix
   * @return the list
   */
  List<MinioMultipartFile> findAll(String prefix);

  /**
   * Delete.
   *
   * @param id the id
   */
  void delete(MinioObjectId id);

  /**
   * Delete all objects.
   *
   * @param ids the IDs
   * @return the list
   */
  List<DeleteError> deleteAll(Collection<MinioObjectId> ids);

  /**
   * Gets presigned object url.
   *
   * @param id the id
   * @param method the method
   * @return the presigned object url
   */
  default String getPresignedObjectUrl(MinioObjectId id, Method method) {
    return getPresignedObjectUrl(id, method, null);
  }

  /**
   * Gets presigned object url.
   *
   * @param id the id
   * @param method the method
   * @param duration the duration
   * @return the presigned object url
   */
  String getPresignedObjectUrl(
      MinioObjectId id,
      Method method,
      @Nullable Duration duration);

}
