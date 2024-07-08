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

import io.minio.errors.BucketPolicyTooLargeException;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.ErrorResponse;
import java.io.IOException;
import java.util.Optional;
import okhttp3.Response;
import org.springframework.util.StringUtils;

/**
 * The default minio error handler.
 *
 * @author Christian Bremer
 */
public class DefaultMinioErrorHandler extends AbstractMinioErrorHandler {

  /**
   * The error code prefix.
   */
  static final String ERROR_CODE_PREFIX = "MINIO_";

  @Override
  public MinioException map(Throwable t) {
    if (t instanceof IllegalArgumentException) {
      return new MinioException(
          400,
          ERROR_CODE_PREFIX + "BAD_REQUEST",
          StringUtils.hasText(t.getMessage()) ? t.getMessage() : "Bad request.",
          t);
    }
    if (t instanceof IOException) {
      return new MinioException(
          500,
          ERROR_CODE_PREFIX + "IO_ERROR",
          StringUtils.hasText(t.getMessage()) ? t.getMessage() : "IO operation failed.",
          t);
    }
    if (t instanceof io.minio.errors.MinioException) {
      return mapMinioException((io.minio.errors.MinioException) t);
    }
    return new MinioException(
        500,
        ERROR_CODE_PREFIX + "UNSPECIFIED",
        StringUtils.hasText(t.getMessage()) ? t.getMessage() : "Unmapped minio error.",
        t);
  }

  private MinioException mapMinioException(io.minio.errors.MinioException e) {
    int status = 500;
    String errorCode = ERROR_CODE_PREFIX + "UNSPECIFIED";
    String message = StringUtils.hasText(e.getMessage())
        ? e.getMessage()
        : "Unspecified minio error.";

    if (e instanceof BucketPolicyTooLargeException) {
      status = 400;
      errorCode = ERROR_CODE_PREFIX + "BUCKET_POLICY_TOO_LARGE";
      message = e.toString();

    } else if (e instanceof ErrorResponseException responseException) {
      status = getStatus(responseException);
      errorCode = getErrorCode(responseException);
      message = getMessage(responseException);

    } else if (e instanceof InsufficientDataException) {
      status = 400;
      errorCode = ERROR_CODE_PREFIX + "INSUFFICIENT_DATA";

    } else if (e instanceof InternalException) {
      errorCode = ERROR_CODE_PREFIX + "INTERNAL_ERROR";

    } else if (e instanceof InvalidResponseException) {
      errorCode = ERROR_CODE_PREFIX + "INVALID_RESPONSE";

    } else if (e instanceof ServerException) {
      errorCode = ERROR_CODE_PREFIX + "SERVER_EXCEPTION";

    } else if (e instanceof XmlParserException) {
      errorCode = ERROR_CODE_PREFIX + "XML_PARSER_ERROR";
    }
    return new MinioException(status, errorCode, message, e);
  }

  private int getStatus(ErrorResponseException e) {
    return Optional.of(e)
        .map(ErrorResponseException::errorResponse)
        .map(ErrorResponse::code)
        .map(code -> getStatus(code, e.response()))
        .orElse(500);
  }

  private int getStatus(String errorCode, Response response) {
    // see https://docs.aws.amazon.com/AmazonS3/latest/API/ErrorResponses.html#ErrorCodeList
    return switch (errorCode) {
      case "AmbiguousGrantByEmailAddress",
          "AuthorizationHeaderMalformed",
          "BadDigest",
          "CredentialsNotSupported",
          "EntityTooSmall",
          "EntityTooLarge",
          "ExpiredToken",
          "IllegalLocationConstraintException",
          "IllegalVersioningConfigurationException",
          "IncompleteBody",
          "IncorrectNumberOfFilesInPostRequest",
          "InlineDataTooLarge",
          "InvalidAccessPoint",
          "InvalidArgument",
          "InvalidBucketName",
          "InvalidDigest",
          "InvalidEncryptionAlgorithmError",
          "InvalidLocationConstraint",
          "InvalidPart",
          "InvalidPartOrder",
          "InvalidPolicyDocument",
          "InvalidRequest",
          "InvalidSOAPRequest",
          "InvalidStorageClass",
          "InvalidTargetBucketForLogging",
          "InvalidToken",
          "InvalidURI",
          "KeyTooLongError",
          "MalformedACLError",
          "MalformedPOSTRequest",
          "MalformedXML",
          "MaxMessageLengthExceeded",
          "MaxPostPreDataLengthExceededError",
          "MetadataTooLarge",
          "MissingRequestBodyError",
          "MissingSecurityElement",
          "MissingSecurityHeader",
          "NoLoggingStatusForKey",
          "RequestIsNotMultiPartContent",
          "RequestTimeout",
          "RequestTorrentOfBucketError",
          "ServerSideEncryptionConfigurationNotFoundError",
          "TokenRefreshRequired",
          "TooManyAccessPoints",
          "TooManyBuckets",
          "UnexpectedContent",
          "UnresolvableGrantByEmailAddress",
          "UserKeyMustBeSpecified",
          "InvalidTag",
          "MalformedPolicy" -> 400;
      case "UnauthorizedAccess" -> 401;
      case "AccessDenied",
          "AccountProblem",
          "AllAccessDisabled",
          "CrossLocationLoggingProhibited",
          "InvalidAccessKeyId",
          "InvalidObjectState",
          "InvalidPayer",
          "InvalidSecurity",
          "NotSignedUp",
          "RequestTimeTooSkewed",
          "SignatureDoesNotMatch" -> 403;
      case "ResourceNotFound",
          "NoSuchAccessPoint",
          "NoSuchBucket",
          "NoSuchKey",
          "NoSuchObject",
          "NoSuchUpload",
          "NoSuchVersion",
          "NoSuchLifecycleConfiguration",
          "NoSuchBucketPolicy",
          "NoSuchObjectLockConfiguration",
          "NoSuchOutpost",
          "NoSuchTagSet",
          "UnsupportedOperation" -> 404;
      case "MethodNotAllowed" -> 405;
      case "BucketAlreadyExists",
          "BucketAlreadyOwnedByYou",
          "BucketNotEmpty",
          "InvalidBucketState",
          "OperationAborted",
          "InvalidOutpostState" -> 409;
      case "MissingContentLength" -> 411;
      case "PreconditionFailed" -> 412;
      case "InvalidRange" -> 416;
      case "InternalError" -> 500;
      case "NotImplemented" -> 501;
      case "ServiceUnavailable", "SlowDown" -> 503;
      default -> Optional.ofNullable(response)
          .map(Response::code)
          .filter(code -> code >= 400)
          .orElse(400);
    };
  }

  private String getErrorCode(ErrorResponseException e) {
    return Optional.of(e)
        .map(ErrorResponseException::errorResponse)
        .map(ErrorResponse::code)
        .orElse(ERROR_CODE_PREFIX + "UNSPECIFIED_ERROR_RESPONSE");
  }

  private String getMessage(ErrorResponseException e) {
    return Optional.of(e)
        .map(ErrorResponseException::errorResponse)
        .map(ErrorResponse::message)
        .orElseGet(() -> StringUtils.hasText(e.getMessage())
            ? e.getMessage()
            : "Unspecified error response.");
  }

}
