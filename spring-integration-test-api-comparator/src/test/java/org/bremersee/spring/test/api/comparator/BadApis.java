/*
 * Copyright 2019 the original author or authors.
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

package org.bremersee.spring.test.api.comparator;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Bad apis for testing.
 *
 * @author Christian Bremer
 */
public class BadApis {

  /**
   * The interface One.
   */
  @Tag(name = "BadApiController")
  public interface One {

  }

  /**
   * The interface Two.
   */
  public interface Two {

  }


  /**
   * The interface Three.
   */
  @Tag(name = "BadApiController")
  public interface Three {

    /**
     * Gets models.
     *
     * @param query the query
     * @return the models
     */
    @RequestMapping(
        value = "/api/models",
        produces = {"application/json"},
        method = RequestMethod.GET)
    ResponseEntity<List<ExampleModel>> getExampleModels(
        @Parameter(description = "The query.") @RequestParam(name = "q", required = false)
            String query);

    /**
     * Update model response entity.
     *
     * @param id the id
     * @param model the model
     * @return the response entity
     */
    @PutMapping(path = "/api/models/{id}")
    ResponseEntity<Void> updateExampleModel(
        @PathVariable("id") String id,
        @RequestBody ExampleModel model);

    /**
     * Add model response entity.
     *
     * @param model the model
     * @return the response entity
     */
    @PostMapping(path = "/api/models")
    ResponseEntity<Void> addExampleModel(
        @RequestBody ExampleModel model);

    /**
     * Gets model.
     *
     * @param id the id
     * @return the model
     */
    @Operation(
        summary = "Get model by ID.",
        operationId = "getExampleModel",
        tags = {"model-controller"})
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = ExampleModel.class)))
    })
    @GetMapping(path = "/api/models/{id}")
    ResponseEntity<ExampleModel> getExampleModel(@PathVariable("id") String id);
  }

  /**
   * The interface Four.
   */
  @Tag(name = "BadApiController")
  public interface Four {

    /**
     * Update model response entity.
     *
     * @param model the model
     * @return the response entity
     */
    @PutMapping(path = "/api/models/{id}")
    ResponseEntity<Void> updateExampleModel(
        @RequestBody ExampleModel model);

    /**
     * Add model response entity.
     *
     * @param model the model
     * @return the response entity
     */
    ResponseEntity<Void> addExampleModel(
        @RequestBody ExampleModel model);

    /**
     * Gets model.
     *
     * @param id the id
     * @return the model
     */
    @Operation(
        summary = "Get model by ID.",
        operationId = "getExampleModel",
        tags = {"model-controller"})
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping(path = "/api/models/{id}")
    ResponseEntity<String> getExampleModel(@PathVariable("id") String id);
  }


  /**
   * The interface Five.
   */
  @Tag(name = "BadApiController")
  public interface Five {

    /**
     * Gets models.
     *
     * @param query the query
     * @return the models
     */
    @RequestMapping(
        value = "/api/models",
        produces = {"application/json"},
        method = RequestMethod.GET)
    ResponseEntity<List<ExampleModel>> getExampleModels(
        @Parameter(description = "The query.")
        @RequestParam(name = "q", required = false) String query);
  }

  /**
   * The interface Six.
   */
  @Tag(name = "BadApiController")
  public interface Six {

    /**
     * Gets models.
     *
     * @param query the query
     * @return the models
     */
    @GetMapping(
        value = "/api/models",
        produces = {"application/json"})
    ResponseEntity<List<ExampleModel>> getExampleModels(
        @Parameter(description = "The query.") @RequestParam(name = "q", required = false)
            String query);
  }


  /**
   * The interface Seven.
   */
  @Tag(name = "BadApiController")
  public interface Seven {

    /**
     * Gets models.
     *
     * @param query the query
     * @return the models
     */
    @RequestMapping(
        value = "/api/models",
        produces = {"application/json"},
        method = RequestMethod.GET)
    ResponseEntity<List<ExampleModel>> getExampleModels(
        @Parameter(description = "The query.") @RequestParam(name = "q", required = false)
            String query);
  }

  /**
   * The interface Eight.
   */
  @Tag(name = "BadApiController")
  public interface Eight {

    /**
     * Gets models.
     *
     * @param query the query
     * @return the models
     */
    @RequestMapping(
        value = "/api/models",
        produces = {"application/json"},
        method = RequestMethod.GET)
    ResponseEntity<List<ExampleModel>> getExampleModels(
        @RequestParam(name = "q", required = false) String query);
  }


}
