package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.UserAccountResponse;

import java.util.UUID;
import java.util.List;

/**
 * Interface for managing bank accounts, providing methods for account registration and other account-related operations.
 * @author BankCore Team - Sebastian Orjuela - Cristian Ortiz
 * @version 1.0
 */
public interface AccountManagementService {

    AccountRegisterResponse registerAccount(AccountRegisterRequest request, UUID id);
    List<UserAccountResponse> getCurrentUserAccounts(UUID id);

}
