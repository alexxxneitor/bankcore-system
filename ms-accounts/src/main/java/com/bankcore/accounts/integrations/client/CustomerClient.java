package com.bankcore.accounts.integrations.client;

import com.bankcore.accounts.integrations.dto.responses.CustomerResponse;

import java.util.UUID;

/**
 * Client interface for interacting with the Customer service.
 * This interface defines methods to retrieve customer information based on their unique identifier.
 * @author BankCore Team - Sebastian Orjuela - Cristian Ortiz
 * @version 1.0
 */
public interface CustomerClient {

    CustomerResponse getCustomerById(UUID customerId);
}
