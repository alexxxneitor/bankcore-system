package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.responses.TransactionResponse;

import java.util.UUID;

import java.util.UUID;

/**
 * Service interface for handling account transactions.
 * <p>
 * This interface defines operations related to financial transactions
 * such as deposits. Implementations are responsible for applying business
 * rules, validating customer and account state, and returning structured
 * responses.
 * </p>
 *
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
public interface TransactionService {

    /**
     * Executes a deposit transaction for the given account and customer.
     *
     * @param request    the {@link TransactionRequest} containing deposit details
     * @param accountId  the {@link UUID} representing the account to deposit into
     * @param customerId the {@link UUID} representing the customer performing the transaction
     * @return a {@link TransactionResponse} containing the result of the deposit
     */
    TransactionResponse makeDeposit(TransactionRequest request, UUID accountId, UUID customerId);
}
