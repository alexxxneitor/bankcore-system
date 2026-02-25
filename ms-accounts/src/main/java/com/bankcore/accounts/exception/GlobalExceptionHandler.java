package com.bankcore.accounts.exception;

import com.bankcore.accounts.dto.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomerInactiveException.class)
    public ResponseEntity<ErrorResponse> hanldeCustomerInactiveException(CustomerInactiveException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource.");
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ErrorResponse> hanldeCustomerNotFoundException(CustomerNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Customer not found for id: " + ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceError(ExternalServiceException ex) {
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

        if (status.is4xxClientError() || status.is5xxServerError()){
            return ResponseEntity
                    .status(status)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(body);
        }

        return new ResponseEntity<>(body, status);
    }
}
