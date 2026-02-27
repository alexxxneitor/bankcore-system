package com.bankcore.accounts.service;

import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;

import java.util.UUID;

public interface AccountManagementService {

    AccountRegisterResponse registerAccount(AccountRegisterRequest request, UUID id);
}
