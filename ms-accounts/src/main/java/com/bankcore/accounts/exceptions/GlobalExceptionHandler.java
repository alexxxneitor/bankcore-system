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

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleEnumError(HttpMessageNotReadableException ex){
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException formatException && formatException.getTargetType().isEnum()) {
            String fieldName = formatException.getPath().get(0).getFieldName();
            Object invalidValue = formatException.getValue();

            String allowedValues = Arrays.stream(formatException.getTargetType().getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            String message = String.format(
                    "%s: Invalid value '%s'. Allowed values: [%s]",
                    fieldName,
                    invalidValue,
                    allowedValues
            );

            return buildErrorResponse(HttpStatus.BAD_REQUEST, message);
        }
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request payload");
    }

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
