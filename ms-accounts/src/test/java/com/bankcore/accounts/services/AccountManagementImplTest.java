package com.bankcore.accounts.services;

import com.bankcore.accounts.client.CustomerClient;
import com.bankcore.accounts.config.DailyWithdrawalLimit;
import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.BusinessException;
import com.bankcore.accounts.exceptions.CustomerInactiveException;
import com.bankcore.accounts.exceptions.CustomerNotFoundException;
import com.bankcore.accounts.exceptions.ResourceConflictException;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.repositories.AccountRepository;
import com.bankcore.accounts.utils.enums.AccountType;
import com.bankcore.accounts.utils.enums.CurrencyCode;
import com.bankcore.accounts.utils.mappers.AccountMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountManagementImplTest {

    @Mock
    private CustomerClient customerClient;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IbanGeneratorService ibanGeneratorService;

    @Mock
    private DailyWithdrawalLimit dailyWithdrawalLimit;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountManagementImpl accountManagement;

    private UUID customerId;
    private AccountRegisterRequest request;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();

        request = AccountRegisterRequest.builder()
                .accountType(AccountType.SAVINGS)
                .alias("my-account")
                .currency(CurrencyCode.EUR)
                .build();
    }

    @Test
    void shouldThrowCustomerNotFound_whenCustomerDoesNotExist() {

        CustomerResponse response = new CustomerResponse(customerId, false, false);

        when(customerClient.getCustomerById(customerId)).thenReturn(response);

        assertThrows(
                CustomerNotFoundException.class,
                () -> accountManagement.registerAccount(request, customerId)
        );
    }

    @Test
    void shouldThrowCustomerInactive_whenCustomerIsInactive() {

        CustomerResponse response = new CustomerResponse(customerId, true, false);

        when(customerClient.getCustomerById(customerId)).thenReturn(response);

        assertThrows(
                CustomerInactiveException.class,
                () -> accountManagement.registerAccount(request, customerId)
        );
    }

    @Test
    void shouldThrowBusinessException_whenCustomerHasThreeAccounts() {

        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        when(accountRepository.countByCustomerId(customerId))
                .thenReturn(3L);

        assertThrows(
                BusinessException.class,
                () -> accountManagement.registerAccount(request, customerId)
        );
    }

    @Test
    void shouldThrowConflict_whenAliasAlreadyExists() {

        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        when(accountRepository.countByCustomerId(customerId))
                .thenReturn(1L);

        when(accountRepository.existsByAliasAndCustomerId("my-account", customerId))
                .thenReturn(true);

        assertThrows(
                ResourceConflictException.class,
                () -> accountManagement.registerAccount(request, customerId)
        );
    }

    @Test
    void shouldGenerateNewIban_ifGeneratedIbanAlreadyExists() {

        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        when(accountRepository.countByCustomerId(customerId))
                .thenReturn(0L);

        when(accountRepository.existsByAliasAndCustomerId(any(), any()))
                .thenReturn(false);

        when(ibanGeneratorService.generateSpanishIban())
                .thenReturn("IBAN1")
                .thenReturn("IBAN2");

        when(accountRepository.existsByAccountNumber("IBAN1"))
                .thenReturn(true);

        when(accountRepository.existsByAccountNumber("IBAN2"))
                .thenReturn(false);

        when(dailyWithdrawalLimit.resolveDailyLimit(any()))
                .thenReturn(BigDecimal.valueOf(1000));

        when(accountMapper.toAccountRegisterResponse(any()))
                .thenReturn(AccountRegisterResponse.builder()
                        .accountNumber("IBAN2")
                        .build());

        AccountRegisterResponse response =
                accountManagement.registerAccount(request, customerId);

        assertEquals("IBAN2", response.getAccountNumber());
    }

    @Test
    void shouldRegisterAccountSuccessfully() {

        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        when(accountRepository.countByCustomerId(customerId))
                .thenReturn(0L);

        when(accountRepository.existsByAliasAndCustomerId(any(), any()))
                .thenReturn(false);

        when(ibanGeneratorService.generateSpanishIban())
                .thenReturn("VALID_IBAN");

        when(accountRepository.existsByAccountNumber("VALID_IBAN"))
                .thenReturn(false);

        when(dailyWithdrawalLimit.resolveDailyLimit(any()))
                .thenReturn(BigDecimal.valueOf(1000));

        AccountRegisterResponse mappedResponse = AccountRegisterResponse.builder()
                .accountNumber("VALID_IBAN")
                .build();

        when(accountMapper.toAccountRegisterResponse(any()))
                .thenReturn(mappedResponse);

        AccountRegisterResponse response =
                accountManagement.registerAccount(request, customerId);

        assertNotNull(response);
        assertEquals("VALID_IBAN", response.getAccountNumber());

        verify(accountRepository).save(any(AccountEntity.class));
    }
}
