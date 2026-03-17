package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.exceptions.AccountInactiveException;
import com.bankcore.accounts.exceptions.AccountNotFoundException;
import com.bankcore.accounts.exceptions.AccountPermanentlyLockedException;
import com.bankcore.accounts.exceptions.AccountTemporarilyLockedException;
import com.bankcore.accounts.integrations.dto.responses.PinValidateResponse;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.models.AccountPinSecurity;
import com.bankcore.accounts.models.TransactionEntity;
import com.bankcore.accounts.repositories.AccountRepository;
import com.bankcore.accounts.repositories.TransactionRepository;
import com.bankcore.accounts.services.complements.CustomerValidationService;
import com.bankcore.accounts.services.complements.PinAttemptManagerService;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import com.bankcore.accounts.utils.mappers.TransactionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private CustomerValidationService validationService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private PinAttemptManagerService pinSecurityService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UUID accountId;
    private UUID customerId;
    private AccountEntity account;
    private TransactionRequest request;

    private final BigDecimal balance = BigDecimal.valueOf(1000.00);
    private final BigDecimal amount = BigDecimal.valueOf(100.00);

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        account = AccountEntity.builder()
                .id(accountId)
                .customerId(customerId)
                .balance(balance)
                .status(AccountStatus.ACTIVE)
                .security(AccountPinSecurity.builder().build())
                .build();

        request = TransactionRequest.builder()
                .amount(amount)
                .pin("1234")
                .description("Deposit test")
                .build();
    }

    @Test
    void shouldMakeDepositSuccessfully() {
        PinValidateResponse pinResponse = new PinValidateResponse(true);
        TransactionEntity savedTransaction = TransactionEntity.builder()
                .id(UUID.randomUUID())
                .account(account)
                .amount(request.getAmount())
                .balanceAfter(account.getBalance().add(request.getAmount()))
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription())
                .build();

        TransactionResponse expectedResponse = TransactionResponse.builder()
                .referenceNumber(savedTransaction.getReferenceNumber())
                .type(savedTransaction.getType())
                .amount(savedTransaction.getAmount())
                .balanceBefore(balance)
                .balanceAfter(savedTransaction.getBalanceAfter())
                .description(savedTransaction.getDescription())
                .timestamp(savedTransaction.getCreatedAt())
                .build();

        when(accountRepository.findByIdAndCustomerId(accountId, customerId))
                .thenReturn(Optional.of(account));

        when(validationService.validateCustomerPin(eq(customerId), any()))
                .thenReturn(pinResponse);

        when(transactionRepository.save(any(TransactionEntity.class)))
                .thenReturn(savedTransaction);

        when(accountRepository.save(account)).thenReturn(account);

        when(transactionMapper.toTransactionResponse(any(TransactionEntity.class)))
                .thenReturn(expectedResponse);

        TransactionResponse response = transactionService.makeDeposit(request, accountId, customerId);

        assertEquals(expectedResponse, response);

        BigDecimal expectedBalance = balance.add(amount);
        assertEquals(expectedBalance, account.getBalance());

        verify(validationService).validateCustomerIsActive(customerId);
        verify(pinSecurityService).checkPinLock(accountId);
        verify(pinSecurityService).processPinAttempt(accountId, pinResponse);
        verify(transactionRepository).save(any(TransactionEntity.class));
        verify(accountRepository).save(account);
    }
    @Test
    void shouldThrowAccountNotFound_whenAccountDoesNotExist() {
        when(accountRepository.findByIdAndCustomerId(accountId, customerId))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.makeDeposit(request, accountId, customerId));

        verify(validationService).validateCustomerIsActive(customerId);
    }

    @Test
    void shouldThrowAccountInactive_whenAccountIsNotActive() {
        account.setStatus(AccountStatus.FROZEN);

        when(accountRepository.findByIdAndCustomerId(accountId, customerId))
                .thenReturn(Optional.of(account));

        assertThrows(AccountInactiveException.class,
                () -> transactionService.makeDeposit(request, accountId, customerId));
    }

    @Test
    void shouldThrowTemporarilyLocked_whenPinCheckFails() {
        when(accountRepository.findByIdAndCustomerId(accountId, customerId))
                .thenReturn(Optional.of(account));

        doThrow(AccountTemporarilyLockedException.class)
                .when(pinSecurityService).checkPinLock(accountId);

        assertThrows(AccountTemporarilyLockedException.class,
                () -> transactionService.makeDeposit(request, accountId, customerId));
    }

    @Test
    void shouldThrowPermanentlyLocked_whenPinCheckFails() {
        when(accountRepository.findByIdAndCustomerId(accountId, customerId))
                .thenReturn(Optional.of(account));

        doThrow(AccountPermanentlyLockedException.class)
                .when(pinSecurityService).checkPinLock(accountId);

        assertThrows(AccountPermanentlyLockedException.class,
                () -> transactionService.makeDeposit(request, accountId, customerId));
    }
}
