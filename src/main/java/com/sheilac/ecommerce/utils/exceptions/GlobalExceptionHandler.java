package com.sheilac.ecommerce.utils.exceptions;

import com.sheilac.ecommerce.utils.enums.ErrorCode;
import com.sheilac.ecommerce.utils.enums.ResponseType;
import com.sheilac.ecommerce.utils.response.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles all application-specific exceptions.
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<CustomResponse<Void>> handleApiException(
            ApiException ex
    ) {

        long tracker = System.currentTimeMillis();

        CustomResponse<Void> response = CustomResponse
                .<Void>builder()
                .responseType(ResponseType.ERROR)
                .errorCode(ex.getErrorCode())
                .exceptionClass(ex.getClass().getSimpleName())
                .message(ex.getMessage())
                .messageTracker(tracker)
                .build();

        return ResponseEntity
                .status(ex.getErrorCode().getStatus())
                .body(response);
    }


    /**
     * Handles validation errors from @Valid / @Validated.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        CustomResponse<Map<String, String>> response =
                CustomResponse
                        .<Map<String, String>>builder()
                        .responseType(ResponseType.ERROR)
                        .errorCode(ErrorCode.BAD_REQUEST)
                        .message("Validation failed")
                        .payload(errors)
                        .exceptionClass(ex.getClass().getSimpleName())
                        .messageTracker(System.currentTimeMillis())
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    /**
     * Handles unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Void>> handleGeneralException(
            Exception ex
    ) {

        long tracker = System.currentTimeMillis();

        // Log the actual exception here in production.
        // Do not expose ex.getMessage() to the client.

        CustomResponse<Void> response = CustomResponse
                .<Void>builder()
                .responseType(ResponseType.ERROR)
                .errorCode(ErrorCode.INTERNAL_ERROR)
                .exceptionClass(ex.getClass().getSimpleName())
                .message("An unexpected error occurred")
                .messageTracker(tracker)
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

}
