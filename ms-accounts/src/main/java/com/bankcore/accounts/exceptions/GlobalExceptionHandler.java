package com.bankcore.accounts.exceptions;

import com.bankcore.accounts.dto.responses.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Global exception handler for handling custom exceptions in the accounts service.
 * <p>
 * This class intercepts specific exceptions thrown by the service and returns a structured
 * JSON response with details about the error using the {@link ErrorResponse} DTO.
 * </p>
 *
 * @author BankCore Team - Sebastian Orjuela - Cristian Ortiz
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle custom exceptions related to customer status and resource conflicts
    @ExceptionHandler(CustomerInactiveException.class)
    public ResponseEntity<ErrorResponse> handleCustomerInactiveException(CustomerInactiveException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // Handle custom exception for when a customer is not found
    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Handle custom exception for resource conflicts, such as duplicate entries
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Handle custom business logic exceptions that may occur during processing
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // Handle custom exceptions related to failures in external service calls
    @ExceptionHandler(CustomExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(CustomExternalServiceException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    //Custom exception handling: captures request errors when the received value cannot be parsed into an enum type.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> CustomHttpMessageNotReadableException(HttpMessageNotReadableException ex){
        Throwable cause = ex.getCause();
        String message = "Invalid request payload";

        if (cause instanceof InvalidFormatException formatException
                && formatException.getTargetType().isEnum()) {

            String fieldName = formatException.getPath().get(0).getFieldName();
            Object invalidValue = formatException.getValue();

            String allowedValues = Arrays.stream(formatException.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            message = String.format(
                    "%s: Invalid value '%s'. Allowed values: [%s]",
                    fieldName,
                    invalidValue,
                    allowedValues
            );
        }

        return badRequest(message);
    }

    //Custom exception handling: captures and manages errors in the request body when the user submits incorrect or malformed parameters.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> customMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        String description = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return badRequest(description);
    }

    //Handles custom exceptions - system validation error capture
    @ExceptionHandler(CustomInternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleCustomInvalidParameter(CustomInternalServiceException ex){
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, ex.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        String message = String.format("Invalid value '%s' for parameter '%s'", value, ex.getName());
        return badRequest(message);
    }

    @ExceptionHandler(AccountInactiveException.class)
    public ResponseEntity<ErrorResponse> handleAccountInactiveException( AccountInactiveException ex){
        return conflict(ex.getMessage());
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException ex){
        return notFound(ex.getMessage());
    }

    /**
     * Helper method for building a 400 BAD_REQUEST error response.
     *
     * @param message the error message
     * @return a BAD_REQUEST {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> badRequest(String message){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * Helper method for building a 404 NOT_FOUND error response.
     *
     * @param message the error message
     * @return a NOT_FOUND {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> notFound(String message){
        return buildErrorResponse(HttpStatus.NOT_FOUND, message);
    }

    /**
     * Helper method for building a 409 CONFLICT error response.
     *
     * @param message the error message
     * @return a CONFLICT {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> conflict(String message){
        return buildErrorResponse(HttpStatus.CONFLICT, message);
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

        return ResponseEntity.status(status).body(body);
    }
}
