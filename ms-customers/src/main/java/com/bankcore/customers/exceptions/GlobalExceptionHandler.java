package com.bankcore.customers.exceptions;

import com.bankcore.customers.dto.responses.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Global exception handler responsible for centralizing application-wide
 * error handling logic.
 * <p>
 * This component intercepts exceptions thrown by controllers and transforms
 * them into structured {@link ErrorResponse} objects, ensuring consistent
 * API error responses across the system.
 * </p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link ResourceConflictException} thrown when a resource
     * already exists or violates a uniqueness constraint.
     *
     * @param ex the thrown {@code ResourceConflictException}
     * @return a {@link ResponseEntity} containing a structured error response
     *         with HTTP status {@code 409 Conflict}
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(
            ResourceConflictException ex) {

        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles validation errors triggered by {@link jakarta.validation.Valid}
     * when request payloads fail Bean Validation constraints.
     *
     * <p>
     * Aggregates all field validation errors into a single descriptive message.
     * </p>
     *
     * @param ex the {@link MethodArgumentNotValidException} containing validation details
     * @return a {@link ResponseEntity} containing a structured error response
     *         with HTTP status {@code 400 Bad Request}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String description = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, description);
    }

    /**
     * Handles exceptions when an authenticated user's profile is not found in the database.
     * <p>
     * This handler is triggered by {@link UserProfileNotFoundException}. It logs a security
     * alert since this scenario typically implies an inconsistency between the security
     * context (JWT) and the persistence layer.
     * </p>
     *
     * @param ex The {@link UserProfileNotFoundException} instance caught by the advice.
     * @return A {@link ResponseEntity} containing an {@link ErrorResponse} with
     * HTTP status 404 (Not Found) and a user-friendly message.
     */
    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserProfileNotFoundException ex) {

        log.error("Security alert: Authenticated user missing from DB: {}", ex.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, "Account error. Please log in again.");
    }

    /**
     * Builds a standardized {@link ErrorResponse} object.
     *
     * @param status      the HTTP status associated with the error
     * @param description a human-readable description of the error
     * @return a {@link ResponseEntity} wrapping the constructed error body
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String description) {

        ErrorResponse body =
                ErrorResponse.builder()
                        .code(status.value())
                        .name(status.getReasonPhrase())
                        .description(description)
                        .build();

        return new ResponseEntity<>(body, status);
    }
}