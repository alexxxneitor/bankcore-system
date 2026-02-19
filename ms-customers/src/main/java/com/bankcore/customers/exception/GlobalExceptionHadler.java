package com.bankcore.customers.exception;

import com.bankcore.customers.dto.responses.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHadler {

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleResourceConflictException(ResourceConflictException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex) {

        String description = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse("Validation error");

        return buildErrorResponse(HttpStatus.BAD_REQUEST, description);
    }

    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserProfileNotFoundException ex) {

        log.error("Security alert: Authenticated user missing from DB: {}", ex.getMessage());
    
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Account error. Please log in again.");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String description) {
        ErrorResponse body = ErrorResponse.builder()
                .code(status.value())
                .name(status.getReasonPhrase())
                .description(description)
                .build();
        return new ResponseEntity<>(body, status);
    }
}
