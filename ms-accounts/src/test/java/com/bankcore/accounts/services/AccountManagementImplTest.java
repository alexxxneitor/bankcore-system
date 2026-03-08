package com.bankcore.accounts.services;

import com.bankcore.accounts.client.CustomerClient;
import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.dto.responses.UserAccountResponse;
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
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagementImplTest {

    @Mock
    private CustomerClient customerClient;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IbanGeneratorService ibanGeneratorService;

    @Mock
    private WithdrawalService withdrawalService;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountManagementImpl accountManagement;

    private UUID customerId;
    private AccountRegisterRequest request;

    private CustomerResponse activeCustomer() {
        return new CustomerResponse(UUID.randomUUID(), true, true);
    }

    private CustomerResponse inactiveCustomer() {
        return new CustomerResponse(UUID.randomUUID(), true, false);
    }

    private CustomerResponse nonExistentCustomer() {
        return new CustomerResponse(UUID.randomUUID(), false, false);
    }
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

        when(withdrawalService.resolveDailyLimit(any()))
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

        when(withdrawalService.resolveDailyLimit(any()))
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

    @Test
    void shouldReturnMappedAccounts_whenCustomerExistsAndHasAccounts() {
        List<AccountEntity> entities = List.of(mock(AccountEntity.class), mock(AccountEntity.class));
        List<UserAccountResponse> expected = List.of(mock(UserAccountResponse.class), mock(UserAccountResponse.class));

        when(accountRepository.findAllByCustomerId(customerId)).thenReturn(entities);
        when(accountMapper.toResponseList(entities)).thenReturn(expected);

        List<UserAccountResponse> result = accountManagement.getCurrentUserAccounts(customerId);

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyList_whenCustomerExistsButHasNoAccounts() {
        when(accountRepository.findAllByCustomerId(customerId)).thenReturn(List.of());
        when(accountMapper.toResponseList(List.of())).thenReturn(List.of());

        List<UserAccountResponse> result = accountManagement.getCurrentUserAccounts(customerId);

        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    void shouldThrowException_whenIdIsNull() {
        assertThatThrownBy(() -> accountManagement.getCurrentUserAccounts(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenIdIsNotValidUUID() {
        assertThatThrownBy(() -> accountManagement.getCurrentUserAccounts(UUID.fromString("not-a-uuid")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowException_whenIdIsBlank() {
        assertThatThrownBy(() -> accountManagement.getCurrentUserAccounts(UUID.fromString(" ")))
                .isInstanceOf(IllegalArgumentException.class);
    }



    @Test
    void shouldAlwaysCallClientBeforeRepository() {

        when(accountRepository.findAllByCustomerId(customerId)).thenReturn(List.of());
        when(accountMapper.toResponseList(any())).thenReturn(List.of());

        accountManagement.getCurrentUserAccounts(customerId);

        verify(accountRepository).findAllByCustomerId(customerId);
    }


    @Test
    void shouldPassRepositoryResultDirectlyToMapper() {
        List<AccountEntity> entities = List.of(mock(AccountEntity.class));

        when(accountRepository.findAllByCustomerId(customerId)).thenReturn(entities);
        when(accountMapper.toResponseList(entities)).thenReturn(List.of(mock(UserAccountResponse.class)));

        accountManagement.getCurrentUserAccounts(customerId);

        verify(accountMapper).toResponseList(entities);
    }

    @Test
    void shouldNeverCallMapper_whenRepositoryFails() {

        when(accountRepository.findAllByCustomerId(customerId)).thenThrow(new RuntimeException("DB error"));

        assertThatThrownBy(() -> accountManagement.getCurrentUserAccounts(customerId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");

        verifyNoInteractions(accountMapper);
    }
}
