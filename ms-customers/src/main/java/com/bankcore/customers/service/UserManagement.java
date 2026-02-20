package com.bankcore.customers.service;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponses;
import com.bankcore.customers.dto.responses.UserProfileResponse;

public interface UserManagement {

    RegisterResponses registerCustomer(RegisterRequest request);

    UserProfileResponse getCurrentUserProfile(String email);
}
