package com.bankcore.accounts.client;

import com.bankcore.accounts.dto.responses.CustomerResponse;

import java.util.UUID;

public interface CustomerClient {

    void getCustomerById(UUID customerId);
}
