package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.requests.TransferRequest;
import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.dto.responses.TransferResponse;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.services.complements.CustomerAccountValidator;
import com.bankcore.accounts.services.processors.DepositProcessor;
import com.bankcore.accounts.services.processors.TransferProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Coordinator service that manages deposit and transfer operations for customer accounts.
 * <p>
 * This service delegates the following responsibilities to specialized components:
 * <ul>
 *   <li>{@link CustomerAccountValidator}: validates customer and account status, including PIN verification.</li>
 *   <li>{@link DepositProcessor}: handles deposit processing and persistence.</li>
 *   <li>{@link TransferProcessor}: handles transfer processing, including destination resolution, balance updates, and transaction persistence.</li>
 * </ul>
 * <p>
 * By delegating responsibilities, this class maintains a clear orchestration role,
 * following the Single Responsibility Principle (SRP).
 * </p>
 *
 * @author BankCore
 * @author Sebastian Orjuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CustomerAccountValidator validator;
    private final DepositProcessor depositProcessor;
    private final TransferProcessor transferProcessor;

    /**
     * Executes a deposit transaction for a customer's account.
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Validates the customer and account state, including PIN verification using {@link CustomerAccountValidator}.</li>
     *   <li>Delegates deposit processing to {@link DepositProcessor}, which updates the account balance and saves the transaction.</li>
     * </ol>
     * </p>
     *
     * @param request    the {@link TransactionRequest} containing deposit details
     * @param accountId  the {@link UUID} of the account to deposit into
     * @param customerId the {@link UUID} of the customer performing the transaction
     * @return a {@link TransactionResponse} containing the result of the deposit
     */
    @Override
    public TransactionResponse makeDeposit(TransactionRequest request, UUID accountId, UUID customerId) {
        AccountEntity account = validator.validateCustomerAccountAndPin(customerId, accountId, request.getPin());
        return depositProcessor.processDeposit(account, request.getAmount(), request.getDescription());
    }

    /**
     * Executes a transfer from a source account to a destination account.
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Validates the source account and customer state, including PIN verification using {@link CustomerAccountValidator}.</li>
     *   <li>Delegates transfer processing to {@link TransferProcessor}, which resolves the destination account,
     *       calculates balances, and persists transfer and transaction records.</li>
     * </ol>
     * </p>
     *
     * @param request    the {@link TransferRequest} containing transfer details
     * @param customerId the {@link UUID} of the customer initiating the transfer
     * @return a {@link TransferResponse} containing the result of the transfer
     */
    @Override
    public TransferResponse makeTransfer(TransferRequest request, UUID customerId) {
        AccountEntity sourceAccount = validator.validateCustomerAccountAndPin(customerId, request.getSourceAccountId(), request.getPin());
        return transferProcessor.processTransfer(sourceAccount, request);
    }
}