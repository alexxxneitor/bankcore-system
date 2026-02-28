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
    public void getCustomerById(UUID id) {

        CustomerResponse response = customersWebClient
                .get()
                .uri("/api/customers/{id}/validate", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, clientResponse -> {
                    log.warn("Customer not found in Customer Service. ID: {}", id);
                    return Mono.error(
                            new CustomerNotFoundException("The authenticated client is not registered")
                    );
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .defaultIfEmpty("No body")
                                .flatMap(body -> {
                                    log.error(
                                            "5xx error from Customer Service. ID: {}, Status: {}, Body: {}",
                                            id,
                                            clientResponse.statusCode(),
                                            body
                                    );
                                    return Mono.error(
                                            new CustomExternalServiceException(
                                                    "Error communicating with Customer Service"
                                            )
                                    );
                                })
                )

                .bodyToMono(CustomerResponse.class)
                .block();

        if (!response.exists()) {
            log.warn("Customer does not exist according to validation endpoint. ID: {}", id);
            throw new CustomerNotFoundException(id.toString());
        }

        if (!response.isActive()) {
            throw new CustomerInactiveException("The authenticated client is inactive");
        }
    }
}
