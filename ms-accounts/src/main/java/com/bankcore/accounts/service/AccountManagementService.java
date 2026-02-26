package com.bankcore.accounts.service;

import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;

public interface AccountManagementService {

    AccountRegisterResponse registerAccount(AccountRegisterRequest request);
}
