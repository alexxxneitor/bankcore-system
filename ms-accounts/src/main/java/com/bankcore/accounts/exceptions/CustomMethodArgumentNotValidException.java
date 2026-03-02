package com.bankcore.accounts.exceptions;

import com.bankcore.accounts.dto.responses.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Global exception handler for handling {@link MethodArgumentNotValidException} exceptions.
 * <p>
 * This class intercepts exceptions thrown when validation on an argument annotated with
 * {@code @Valid} fails. It extracts the validation error messages and returns a structured
 * JSON response with details about the error using the {@link ErrorResponse} DTO.
 * </p>
 *
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class CustomMethodArgumentNotValidException {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleValidationException(
            HttpServletRequest request,
            HttpServletResponse response,
            MethodArgumentNotValidException ex
    ) throws IOException {

        String description = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        ErrorResponse body =
                ErrorResponse.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .name(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .description(description)
                        .build();

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
