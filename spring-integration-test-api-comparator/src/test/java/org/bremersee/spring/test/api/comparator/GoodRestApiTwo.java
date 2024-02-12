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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Rest api two for testing.
 *
 * @author Christian Bremer
 */
@Tag(value = "GoodRestApiController")
public interface GoodRestApiTwo {

  /**
   * Gets models.
   *
   * @param query the query
   * @return the models
   */
  @Operation(
      summary = "Get models.",
      operationId = "getExampleModels",
      tags = {"model-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "OK",
          content = @Content(
              array = @ArraySchema(
                  schema = @Schema(implementation = ExampleModel.class)))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @RequestMapping(
      value = "/api/models",
      produces = {"application/json"},
      method = RequestMethod.GET)
  List<ExampleModel> getExampleModels(
      @Parameter(description = "The query.")
      @RequestParam(name = "q", required = false) String query);

  /**
   * Add model model.
   *
   * @param model the model
   * @return the model
   */
  @Operation(
      summary = "Add model.",
      operationId = "addExampleModel",
      tags = {"model-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "OK",
          content = @Content(schema = @Schema(implementation = ExampleModel.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(
              implementation = java.lang.RuntimeException.class))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden")
  })
  @RequestMapping(
      value = "/api/models",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.POST)
  ExampleModel addExampleModel(
      @Parameter(description = "The model.", required = true)
      @RequestBody ExampleModel model);

  /**
   * Gets model.
   *
   * @param id the id
   * @return the model
   */
  @Operation(
      description = "Get model.",
      operationId = "getExampleModel",
      tags = {"model-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "OK",
          content = @Content(schema = @Schema(implementation = ExampleModel.class))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden"),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(
              implementation = java.lang.RuntimeException.class)))
  })
  @RequestMapping(
      value = "/api/models/{id}",
      produces = {"application/json"},
      method = RequestMethod.GET)
  ExampleModel getExampleModel(
      @Parameter(description = "The model ID.", required = true) @PathVariable("id") String id);

  /**
   * Update model model.
   *
   * @param id the id
   * @param model the model
   * @return the model
   */
  @Operation(
      summary = "Update model.",
      operationId = "updateExampleModel",
      tags = {"model-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "OK",
          content = @Content(schema = @Schema(implementation = ExampleModel.class))),
      @ApiResponse(
          responseCode = "400",
          description = "Bad Request",
          content = @Content(schema = @Schema(
              implementation = java.lang.RuntimeException.class))),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden"),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(
              implementation = java.lang.RuntimeException.class)))
  })
  @RequestMapping(
      value = "/api/models/{id}",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.PUT)
  ExampleModel updateExampleModel(
      @Parameter(description = "The model ID.", required = true) @PathVariable("id") String id,
      @Parameter(description = "The model.", required = true)
      @RequestBody ExampleModel model);

  /**
   * Delete model.
   *
   * @param id the id
   */
  @Operation(
      summary = "Delete model.",
      operationId = "deleteExampleModel",
      tags = {"model-controller"})
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "OK"),
      @ApiResponse(
          responseCode = "403",
          description = "Forbidden"),
      @ApiResponse(
          responseCode = "404",
          description = "Not Found",
          content = @Content(schema = @Schema(
              implementation = java.lang.RuntimeException.class)))
  })
  @RequestMapping(
      value = "/api/models/{id}",
      produces = {"application/json"},
      method = RequestMethod.DELETE)
  void deleteExampleModel(
      @Parameter(description = "The model ID.", required = true) @PathVariable("id") String id);

}
