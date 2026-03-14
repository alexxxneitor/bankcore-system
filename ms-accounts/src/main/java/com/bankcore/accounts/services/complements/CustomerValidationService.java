package com.bankcore.accounts.services.complements;

import com.bankcore.accounts.integrations.client.CustomerClient;
import com.bankcore.accounts.integrations.dto.request.PinValidateRequest;
import com.bankcore.accounts.integrations.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.CustomerInactiveException;
import com.bankcore.accounts.exceptions.CustomerNotFoundException;
import com.bankcore.accounts.integrations.dto.responses.PinValidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for validating customer state before allowing
 * transactional or business operations.
 * <p>
 * This service interacts with the {@link CustomerClient} to retrieve
 * customer information and ensures that the customer exists and is active.
 * </p>
 *
 * @author BankcoreTeam - Sebastian Orjuela
 * @version 1.0
 */

@Service
@RequiredArgsConstructor
public class CustomerValidationService {

    private final CustomerClient customerClient;

    /**
     * Validates if the customer associated with the given ID is active.
     * If the customer is not active, a CustomerInactiveException is thrown.
     *
     * @param idCustomer the UUID of the customer to validate
     * @throws CustomerNotFoundException if the consulted client does not exist
     * @throws CustomerInactiveException if the customer is not active
     */
    public void validateCustomerIsActive(UUID idCustomer) {
        CustomerResponse customer = customerClient.getCustomerById(idCustomer);
        if (!customer.exists()) {
            throw new CustomerNotFoundException("The customer is not registered in the system");
        }

        if (!customer.isActive()) {
            throw new CustomerInactiveException("The authenticated client is not active");
        }
    }

    public PinValidateResponse validateCustomerPin(UUID customerId, PinValidateRequest request){
        return customerClient.validateCustomerPin(customerId, request);
    }
}
