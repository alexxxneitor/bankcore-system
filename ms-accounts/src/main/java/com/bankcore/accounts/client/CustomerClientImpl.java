package com.bankcore.accounts.client;

import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.CustomerInactiveException;
import com.bankcore.accounts.exceptions.CustomerNotFoundException;
import com.bankcore.accounts.exceptions.CustomExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class CustomerClientImpl implements CustomerClient {

    private final WebClient customersWebClient;

    public CustomerClientImpl(WebClient customersWebClient) {
        this.customersWebClient = customersWebClient;
    }

    @Override
    public CustomerResponse getCustomerById(UUID id) {

        return customersWebClient
                .get()
                .uri("/api/customers/{id}/validate", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, response -> {
                    log.warn("Customer not found in Customer Service. ID: {}", id);
                    return Mono.error(
                            new CustomerNotFoundException("Customer with ID " + id + " not found")
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("No body")
                                .flatMap(body -> {
                                    log.error("5xx error from Customer Service. Status: {}, Body: {}",
                                            response.statusCode(), body);
                                    return Mono.error(
                                            new CustomExternalServiceException("Error communicating with Customer Service")
                                    );
                                })
                )
                .bodyToMono(CustomerResponse.class)
                .flatMap(customer -> {
                    if (!customer.exists()) {
                        return Mono.error(new CustomerNotFoundException(id.toString()));
                    }
                    if (!customer.isActive()) {
                        return Mono.error(
                                new CustomerInactiveException("Customer with ID " + id + " is inactive")
                        );
                    }
                    return Mono.just(customer);
                })
                .block();
    }
}
