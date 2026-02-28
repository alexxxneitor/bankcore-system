package com.bankcore.accounts.exceptions;

import com.bankcore.accounts.dto.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerInactiveException.class)
    public ResponseEntity<ErrorResponse> hanldeCustomerInactiveException(CustomerInactiveException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> hanldeCustomerNotFoundException(CustomerNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(CustomExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(CustomExternalServiceException ex) {
        return buildErrorResponse(HttpStatus.BAD_GATEWAY, ex.getMessage());
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
