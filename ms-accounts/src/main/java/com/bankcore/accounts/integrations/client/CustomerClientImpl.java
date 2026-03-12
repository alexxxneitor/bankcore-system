package com.bankcore.accounts.integrations.client;

import java.util.UUID;

import com.bankcore.accounts.exceptions.CustomInternalServiceException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.bankcore.accounts.integrations.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.CustomExternalServiceException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Implementation of the CustomerClient interface that uses WebClient to communicate with the Customer service.
 * This class handles the retrieval of customer information and includes error handling for various scenarios.
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class CustomerClientImpl implements CustomerClient {

    private final WebClient customersWebClient;

    @Override
    public CustomerResponse getCustomerById(UUID id) {

        return customersWebClient
                .get()
                .uri("/api/customers/{id}/validate", id)
                .retrieve()

                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("No body")
                                .flatMap(body -> {
                                    log.error(
                                            "Customer Service 4xx error. ID: {}, Status: {}, Body: {}",
                                            id,
                                            response.statusCode(),
                                            body
                                    );

                                    return Mono.error(new CustomInternalServiceException(
                                            "Customer Service rejected the request"
                                    ));
                                })
                )

                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("No body")
                                .flatMap(body -> {
                                    log.error(
                                            "Customer Service 5xx error. ID: {}, Status: {}, Body: {}",
                                            id,
                                            response.statusCode(),
                                            body
                                    );
                                    return Mono.error(new CustomExternalServiceException(
                                            "Customer Service is unavailable"
                                    ));
                                })
                )

                .bodyToMono(CustomerResponse.class)
                .blockOptional()
                .orElseThrow(() -> {
                    log.error("Customer Service returned empty body. ID: {}", id);
                    return new CustomInternalServiceException(
                            "Empty response from Customer Service"
                    );
                });
    }
}
