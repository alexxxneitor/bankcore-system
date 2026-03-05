package com.bankcore.accounts.client;

import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.CustomExternalServiceException;
import com.bankcore.accounts.exceptions.CustomInternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerClientImplTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private CustomerClientImpl customerClient;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }

    @Test
    void shouldReturnCustomerResponse_when200() {
        CustomerResponse response = new CustomerResponse(
                customerId,
                true,
                true
        );

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/api/customers/{id}/validate", customerId))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CustomerResponse.class))
                .thenReturn(Mono.just(response));

        CustomerResponse result = customerClient.getCustomerById(customerId);

        assertNotNull(result);
        assertEquals(customerId, result.customerId());
        assertTrue(result.exists());
    }

    @Test
    void shouldThrowInternalException_when4xx() {

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(),any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.bodyToMono(CustomerResponse.class))
                .thenReturn(Mono.error(new CustomInternalServiceException("error")));

        assertThrows(
                CustomInternalServiceException.class,
                () -> customerClient.getCustomerById(customerId)
        );
    }

    @Test
    void shouldThrowExternalException_when5xx() {

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(),any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.bodyToMono(CustomerResponse.class))
                .thenReturn(Mono.error(new CustomExternalServiceException("error")));

        assertThrows(
                CustomExternalServiceException.class,
                () -> customerClient.getCustomerById(customerId)
        );
    }

    @Test
    void shouldThrowInternalException_whenBodyIsEmpty() {

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(),any(UUID.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
                .thenReturn(responseSpec);

        when(responseSpec.bodyToMono(CustomerResponse.class))
                .thenReturn(Mono.empty());

        assertThrows(
                CustomInternalServiceException.class,
                () -> customerClient.getCustomerById(customerId)
        );
    }
}
