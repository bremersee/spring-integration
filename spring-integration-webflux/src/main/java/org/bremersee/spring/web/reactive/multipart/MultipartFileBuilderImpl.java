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

package org.bremersee.spring.web.reactive.multipart;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.ObjectUtils.isEmpty;

import eu.maxschuster.dataurl.DataUrl;
import eu.maxschuster.dataurl.DataUrlSerializer;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bremersee.spring.web.multipart.FileAwareMultipartFile;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * The multipart file builder implementation.
 *
 * @author Christian Bremer
 */
public class MultipartFileBuilderImpl implements MultipartFileBuilder {

  private final File tmpDir;

  /**
   * Instantiates a new multipart file builder.
   */
  public MultipartFileBuilderImpl() {
    this((String) null);
  }

  /**
   * Instantiates a new multipart file builder.
   *
   * @param tmpDir the tmp dir
   */
  public MultipartFileBuilderImpl(String tmpDir) {
    this(Optional.ofNullable(tmpDir)
        .filter(str -> !str.isBlank())
        .map(File::new)
        .orElseGet(() -> new File(System.getProperty("java.io.tmpdir"))));
  }

  /**
   * Instantiates a new multipart file builder.
   *
   * @param tmpDir the tmp dir
   */
  public MultipartFileBuilderImpl(File tmpDir) {
    this.tmpDir = Optional.ofNullable(tmpDir)
        .filter(dir -> dir.exists() && dir.isDirectory() && dir.canRead() && dir.canWrite())
        .orElseGet(() -> new File(System.getProperty("java.io.tmpdir")));
  }

  private Mono<MultipartFile> build(FilePart filePart) {
    return Optional.ofNullable(filePart)
        .map(fp -> {
          File file = newTmpFile();
          return filePart.transferTo(file)
              .then(Mono.just(new FileAwareMultipartFile(
                  file,
                  filePart.name(),
                  filePart.filename(),
                  Optional.of(filePart)
                      .map(part -> part.headers().getContentType())
                      .map(MimeType::toString)
                      .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE))));
        })
        .orElseGet(() -> Mono.just(FileAwareMultipartFile.empty()))
        .cast(MultipartFile.class);
  }

  private Mono<MultipartFile> build(FormFieldPart formFieldPart) {
    return Optional.ofNullable(formFieldPart)
        .filter(ffp -> !isEmpty(ffp.value()))
        .map(ffp -> Mono.just(ffp)
            .handle((part, sink) -> {
              String value = part.value();
              byte[] data = null;
              String contentType = null;
              if (value.toLowerCase().startsWith("data:") && value.indexOf(',') > -1) {
                DataUrl dataUrl = null;
                try {
                  dataUrl = new DataUrlSerializer().unserialize(value);
                } catch (Exception ignored) {
                  // Parsing form field as data url failed, treating value as plain/text.
                }
                if (nonNull(dataUrl)) {
                  data = dataUrl.getData();
                  contentType = dataUrl.getMimeType();
                }
              }
              if (isNull(data)) {
                data = value.getBytes(StandardCharsets.UTF_8);
                contentType = MediaType.TEXT_PLAIN_VALUE;
              }
              try {
                sink.next(new FileAwareMultipartFile(
                    new ByteArrayInputStream(data),
                    tmpDir,
                    part.name(),
                    null,
                    contentType));

              } catch (IOException e) {
                sink.error(new MultipartException(
                    "Creating multipart file from form field part failed.", e));
              }
            }))
        .orElseGet(() -> Mono.just(FileAwareMultipartFile.empty()))
        .cast(MultipartFile.class);
  }

  @Override
  public Mono<MultipartFile> build(Part part) {
    if (part instanceof FilePart) {
      return build((FilePart) part);
    }
    if (part instanceof FormFieldPart) {
      return build((FormFieldPart) part);
    }
    return Mono.just(FileAwareMultipartFile.empty());
  }

  @Override
  public Mono<MultipartFile> build(Flux<? extends Part> parts) {
    //noinspection unchecked
    return ((Flux<Part>) parts)
        .last(new EmptyPart())
        .flatMap(this::build);
  }

  @Override
  public Mono<List<MultipartFile>> buildList(Flux<? extends Part> parts) {
    return parts.flatMap(this::build)
        .collectList();
  }

  @Override
  public Mono<List<MultipartFile>> buildList(
      MultiValueMap<String, Part> multiPartData, String... requestParameters) {

    return Flux.fromArray(Optional.ofNullable(requestParameters).orElseGet(() -> new String[0]))
        .flatMap(requestParameter -> build(multiPartData.getFirst(requestParameter)))
        .collectList();
  }

  @Override
  public Flux<List<MultipartFile>> buildLists(
      MultiValueMap<String, Part> multiPartData, String... requestParameters) {

    return Flux.fromArray(Optional.ofNullable(requestParameters).orElseGet(() -> new String[0]))
        .flatMap(reqParam -> createList(multiPartData, reqParam));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Mono<Map<String, MultipartFile>> buildMap(Flux<? extends Part>... parts) {
    return Flux.fromArray(parts)
        .flatMap(this::build)
        .collectMap(MultipartFile::getName, multipartFile -> multipartFile, LinkedHashMap::new);
  }

  @Override
  public Mono<Map<String, MultipartFile>> buildMap(
      MultiValueMap<String, Part> multiPartData, String... requestParameters) {

    return Flux.fromArray(Optional.ofNullable(requestParameters).orElseGet(() -> new String[0]))
        .flatMap(reqParam -> build(multiPartData.getFirst(reqParam))
            .zipWith(Mono.just(reqParam)))
        .collectMap(Tuple2::getT2, Tuple2::getT1, LinkedHashMap::new);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Mono<MultiValueMap<String, MultipartFile>> buildMultiValueMap(
      Flux<? extends Part>... parts) {
    return Flux.fromArray(parts)
        .flatMap(this::buildList)
        .filter(list -> !list.isEmpty())
        .collectMap(list -> list.get(0).getName(), list -> list, LinkedHashMap::new)
        .map(LinkedMultiValueMap::new);
  }

  @Override
  public Mono<MultiValueMap<String, MultipartFile>> buildMultiValueMap(
      MultiValueMap<String, Part> multiPartData, String... requestParameters) {

    return Flux.fromArray(Optional.ofNullable(requestParameters).orElseGet(() -> new String[0]))
        .flatMap(reqParam -> createList(multiPartData, reqParam)
            .zipWith(Mono.just(reqParam)))
        .collectMap(Tuple2::getT2, Tuple2::getT1, LinkedHashMap::new)
        .map(LinkedMultiValueMap::new);
  }

  private File newTmpFile() {
    try {
      return File.createTempFile("uploaded-", ".tmp", tmpDir);
    } catch (Exception e) {
      throw new MultipartException("Creating tmp file failed.", e);
    }
  }

  private Mono<List<MultipartFile>> createList(
      MultiValueMap<String, Part> multiPartData,
      String requestParameter) {

    List<Part> contentParts = multiPartData
        .getOrDefault(requestParameter, Collections.emptyList());
    return contentParts.isEmpty()
        ? Mono.empty()
        : Flux.fromIterable(contentParts).flatMap(this::build).collectList();
  }

  private static class EmptyPart implements Part {

    private EmptyPart() {
    }

    @NonNull
    @Override
    public String name() {
      return "";
    }

    @NonNull
    @Override
    public HttpHeaders headers() {
      return new HttpHeaders();
    }

    @NonNull
    @Override
    public Flux<DataBuffer> content() {
      return Flux.empty();
    }
  }

}
