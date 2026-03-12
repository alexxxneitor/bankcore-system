package com.bankcore.accounts.services.complements;

import com.bankcore.accounts.integrations.client.CustomerClient;
import com.bankcore.accounts.integrations.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.CustomerInactiveException;
import com.bankcore.accounts.exceptions.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerValidationServiceTest {

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private CustomerValidationService customerValidationService;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }

    @Test
    void shouldThrowCustomerNotFound_whenCustomerDoesNotExist() {
        CustomerResponse response = new CustomerResponse(customerId, false, false);

        when(customerClient.getCustomerById(customerId)).thenReturn(response);

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerValidationService.validateCustomerIsActive(customerId)
        );
    }

    @Test
    void shouldThrowCustomerInactive_whenCustomerIsInactive() {
        CustomerResponse response = new CustomerResponse(customerId, true, false);

        when(customerClient.getCustomerById(customerId)).thenReturn(response);

        assertThrows(
                CustomerInactiveException.class,
                () -> customerValidationService.validateCustomerIsActive(customerId)
        );
    }

    @Test
    void shouldPass_whenCustomerIsActive() {
        CustomerResponse response = new CustomerResponse(customerId, true, true);

        when(customerClient.getCustomerById(customerId)).thenReturn(response);

        assertDoesNotThrow(() -> customerValidationService.validateCustomerIsActive(customerId));
    }
}
