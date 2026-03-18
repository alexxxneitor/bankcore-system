package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.exceptions.AccountInactiveException;
import com.bankcore.accounts.exceptions.AccountNotFoundException;
import com.bankcore.accounts.integrations.dto.request.PinValidateRequest;
import com.bankcore.accounts.integrations.dto.responses.PinValidateResponse;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.models.TransactionEntity;
import com.bankcore.accounts.repositories.AccountRepository;
import com.bankcore.accounts.repositories.TransactionRepository;
import com.bankcore.accounts.services.complements.CustomerValidationService;
import com.bankcore.accounts.services.complements.PinAttemptManagerService;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import com.bankcore.accounts.utils.mappers.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of the {@link TransactionService} interface that handles
 * deposit transactions for customer accounts.
 * <p>
 * This service coordinates validation of customer state, account status,
 * and PIN security before executing a deposit. It ensures that business
 * rules are enforced and that both the account and transaction records
 * are updated consistently.
 * </p>
 *
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final CustomerValidationService validationService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final PinAttemptManagerService pinSecurityService;

    /**
     * Executes a deposit transaction after validating customer, account, and PIN state.
     * <p>
     * The method performs the following steps:
     * <ol>
     *   <li>Validates that the customer exists and is active.</li>
     *   <li>Retrieves the account and ensures it belongs to the customer.</li>
     *   <li>Checks that the account is active.</li>
     *   <li>Checks PIN lock state and validates the PIN.</li>
     *   <li>Processes PIN attempts and applies lockout policies if necessary.</li>
     *   <li>Updates the account balance and persists the transaction.</li>
     * </ol>
     * </p>
     *
     * @param request    the {@link TransactionRequest} containing deposit details
     * @param accountId  the {@link UUID} of the account to deposit into
     * @param customerId the {@link UUID} of the customer performing the transaction
     * @return a {@link TransactionResponse} containing the result of the deposit
     * @throws AccountNotFoundException if the account does not exist
     * @throws AccountInactiveException if the account is inactive
     */
    @Override
    @Transactional
    public TransactionResponse makeDeposit(TransactionRequest request, UUID accountId, UUID customerId) {

        validationService.validateCustomerIsActive(customerId);

        AccountEntity account = accountRepository
                .findByIdAndCustomerId(accountId, customerId)
                .orElseThrow(AccountNotFoundException::new);

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException(account.getStatus());
        }

        pinSecurityService.checkPinLock(accountId);

        PinValidateRequest pinRequest = PinValidateRequest.builder().pin(request.getPin()).build();
        PinValidateResponse pinResponse = validationService.validateCustomerPin(customerId, pinRequest);

        pinSecurityService.processPinAttempt(accountId, pinResponse);

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal newBalance = balanceBefore.add(request.getAmount());

        TransactionEntity transaction = TransactionEntity.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceAfter(newBalance)
                .description(request.getDescription())
                .status(TransactionStatus.COMPLETED)
                .build();

        account.setBalance(newBalance);

        transactionRepository.save(transaction);
        accountRepository.save(account);

        return transactionMapper.toTransactionResponse(transaction, balanceBefore);
    }
}
