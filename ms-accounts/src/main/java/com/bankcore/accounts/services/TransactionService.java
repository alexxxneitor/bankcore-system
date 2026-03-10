package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.responses.TransactionResponse;

import java.util.UUID;

public interface TransactionService {

    TransactionResponse makeDeposit(TransactionRequest request, UUID accountId, UUID customerId);
}
