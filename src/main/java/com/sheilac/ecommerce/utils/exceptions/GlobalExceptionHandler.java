package com.sheilac.ecommerce.utils.exceptions;

import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.enums.ResponseType;
import com.sheilac.ecommerce.utils.response.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // Our own business errors (thrown by services)
    // =========================================================

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<CustomResponse<Void>> handleApiException(ApiException ex) {
        if (ex.getErrorCode() == ErrorCode.INTERNAL_ERROR) {
            log.error("Internal error", ex);
        }
        return error(ex.getErrorCode(), ex, ex.getMessage());
    }

    // =========================================================
    // 400 - the client sent something invalid
    // =========================================================

    /** @Valid failed on a request body. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        // LinkedHashMap keeps the errors in the order they were found
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CustomResponse.<Map<String, String>>builder()
                        .responseType(ResponseType.ERROR)
                        .errorCode(ErrorCode.BAD_REQUEST)
                        .message("Validation failed")
                        .payload(errors)
                        .exceptionClass(ex.getClass().getSimpleName())
                        .messageTracker(System.currentTimeMillis())
                        .build());
    }

    /** e.g. GET /api/brands/abc when the id must be a number. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CustomResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        return error(ErrorCode.BAD_REQUEST, ex,
                "Invalid value '" + ex.getValue() + "' for parameter '" + ex.getName() + "'");
    }

    /** A required @RequestParam was not sent. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CustomResponse<Void>> handleMissingParam(
            MissingServletRequestParameterException ex
    ) {
        return error(ErrorCode.BAD_REQUEST, ex,
                "Required parameter '" + ex.getParameterName() + "' is missing");
    }

    /** Body is empty, not JSON, or has the wrong types (e.g. "price": "abc"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponse<Void>> handleUnreadableBody(
            HttpMessageNotReadableException ex
    ) {
        return error(ErrorCode.BAD_REQUEST, ex,
                "Request body is missing or is not valid JSON");
    }

    /** Safety net: an unknown field name reached a Spring Data query. */
//    @ExceptionHandler(PropertyReferenceException.class)
//    public ResponseEntity<CustomResponse<Void>> handleBadProperty(
//            PropertyReferenceException ex
//    ) {
//        return error(ErrorCode.BAD_REQUEST, ex,
//                "Unknown field '" + ex.getPropertyName() + "'");
//    }

    /** The "file" part is missing. */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<CustomResponse<Void>> handleMissingPart(
            MissingServletRequestPartException ex
    ) {
        return error(ErrorCode.BAD_REQUEST, ex,
                "Required file part '" + ex.getRequestPartName() + "' is missing");
    }

    /** Request was not multipart at all. */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<CustomResponse<Void>> handleMultipart(MultipartException ex) {
        return error(ErrorCode.BAD_REQUEST, ex, "Request must be multipart/form-data");
    }

    // =========================================================
    // 404 / 405 / 413 / 415 - wrong URL, method, size or format
    // =========================================================

    /** No controller or static file matches the URL. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CustomResponse<Void>> handleNoResource(NoResourceFoundException ex) {
        return error(ErrorCode.NOT_FOUND, ex, "The requested URL was not found");
    }

    /** e.g. DELETE on an endpoint that only supports GET. */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CustomResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex
    ) {
        return error(ErrorCode.METHOD_NOT_ALLOWED, ex,
                "Method " + ex.getMethod() + " is not supported for this URL");
    }

    /** Upload larger than spring.servlet.multipart limits. */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<CustomResponse<Void>> handleMaxUploadSize(
            MaxUploadSizeExceededException ex
    ) {
        return error(ErrorCode.CONTENT_TOO_LARGE, ex,
                "File is too large. Maximum size is 5MB per image.");
    }

    /** e.g. sending form data to an endpoint that expects JSON. */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<CustomResponse<Void>> handleMediaType(
            HttpMediaTypeNotSupportedException ex
    ) {
        return error(ErrorCode.UNSUPPORTED_MEDIA_TYPE, ex,
                "Content type '" + ex.getContentType() + "' is not supported");
    }

    // =========================================================
    // 409 - conflicts with data already in the database
    // =========================================================

    /**
     * A database constraint was broken, e.g. two requests created the same
     * brand name at the same moment and both passed the "exists" check.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomResponse<Void>> handleDataIntegrity(
            DataIntegrityViolationException ex
    ) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return error(ErrorCode.CONFLICT, ex,
                "The request conflicts with existing data (for example, a duplicate name or code)");
    }

    /** @Version check failed: someone else updated the same record first. */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<CustomResponse<Void>> handleOptimisticLock(
            ObjectOptimisticLockingFailureException ex
    ) {
        return error(ErrorCode.CONFLICT, ex,
                "This record was changed by someone else. Reload it and try again.");
    }

    // =========================================================
    // 500 - anything we did not expect
    // =========================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Unhandled exception", ex);
        return error(ErrorCode.INTERNAL_ERROR, ex, "An unexpected error occurred");
    }

    private ResponseEntity<CustomResponse<Void>> error(ErrorCode code, Exception ex, String message) {
        return ResponseEntity.status(code.getStatus()).body(
                CustomResponse.<Void>builder()
                        .responseType(ResponseType.ERROR)
                        .errorCode(code)
                        .exceptionClass(ex.getClass().getSimpleName())
                        .message(message)
                        .messageTracker(System.currentTimeMillis())
                        .build());
    }
}