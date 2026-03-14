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
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import com.bankcore.accounts.utils.mappers.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    private final CustomerValidationService validationService;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    @Transactional
    public TransactionResponse makeDeposit(TransactionRequest request, UUID accountId, UUID customerId) {

        validationService.validateCustomerIsActive(customerId);

        PinValidateRequest pinRequest = PinValidateRequest.builder().pin(request.getPin()).build();

        PinValidateResponse pinValidateResponse = validationService.validateCustomerPin(customerId, pinRequest);

        AccountEntity account = accountRepository
                .findByIdAndCustomerId(accountId, customerId)
                .orElseThrow((AccountNotFoundException::new));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException();
        }

        BigDecimal newBalance = account.getBalance().add(request.getAmount());

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

        return transactionMapper.toTransactionResponse(transaction);
    }
}
