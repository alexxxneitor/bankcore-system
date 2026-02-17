package com.bankcore.customers.service;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponses;

public interface UserManagement {

    RegisterResponses registerCustomer(RegisterRequest request);
}
