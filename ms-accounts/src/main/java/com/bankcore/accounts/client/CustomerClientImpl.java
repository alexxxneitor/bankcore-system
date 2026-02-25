package com.bankcore.accounts.client;

import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.exception.CustomerInactiveException;
import com.bankcore.accounts.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerClientImpl implements CustomerClient{

    private final WebClient customersWebClient;
    /**
     * @param id
     * @return
     */
    @Override
    public CustomerResponse getCustomer(UUID id) {
        CustomerResponse customer =  customersWebClient
                .get()
                .uri("/api/customers/{id}/validate", id)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        response -> Mono.error(
                                new CustomerNotFoundException(id.toString())
                        )
                )
                .bodyToMono(CustomerResponse.class)
                .block();

        if (customer != null && !customer.isActive()) {
            throw new CustomerInactiveException(customer.customerId());
        }

        return customer;
    }
}
