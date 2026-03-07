package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;

import java.util.UUID;

/**
 * Interface for managing bank accounts, providing methods for account registration and other account-related operations.
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
public interface AccountManagementService {

    AccountRegisterResponse registerAccount(AccountRegisterRequest request, UUID id);
}
