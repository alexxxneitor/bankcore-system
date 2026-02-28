package com.bankcore.accounts.exceptions;

import com.bankcore.accounts.dto.responses.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Global exception handler for handling {@link HttpMessageNotReadableException} exceptions.
 * <p>
 * This class intercepts exceptions thrown when the request body cannot be read or parsed,
 * such as when an invalid enum value is provided. It returns a structured JSON response
 * with details about the error using the {@link ErrorResponse} DTO.
 * </p>
 *
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomHttpMessageNotReadableException {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleEnumError(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpMessageNotReadableException ex
    ) throws IOException {

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

        ErrorResponse body =
                ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .name(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .description(message)
                        .build();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
