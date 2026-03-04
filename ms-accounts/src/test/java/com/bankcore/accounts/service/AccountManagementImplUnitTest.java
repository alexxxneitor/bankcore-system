package com.bankcore.accounts.service;

import com.bankcore.accounts.client.CustomerClient;
import com.bankcore.accounts.dto.responses.UserAccountResponse;
import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.repositries.AccountRepository;
import com.bankcore.accounts.utils.mappers.AccountMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountManagementImplUnitTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private CustomerClient client;

    @InjectMocks
    private AccountManagementImpl service;

    private static final String VALID_UUID = UUID.randomUUID().toString();
    private static final UUID CUSTOMER_ID = UUID.fromString(VALID_UUID);

    // ─── Helpers ────────────────────────────────────────────────

    private CustomerResponse activeCustomer() {
        return new CustomerResponse(CUSTOMER_ID, true, true);
    }

    private CustomerResponse inactiveCustomer() {
        return new CustomerResponse(CUSTOMER_ID, true, false);
    }

    private CustomerResponse nonExistentCustomer() {
        return new CustomerResponse(CUSTOMER_ID, false, false);
    }

    @Nested
    class HappyPath {

        @Test
        void shouldReturnMappedAccounts_whenCustomerExistsAndHasAccounts() {
            List<AccountEntity> entities = List.of(mock(AccountEntity.class), mock(AccountEntity.class));
            List<UserAccountResponse> expected = List.of(mock(UserAccountResponse.class), mock(UserAccountResponse.class));

            when(client.getCustomerById(CUSTOMER_ID)).thenReturn(activeCustomer());
            when(accountRepository.findAllByCustomerId(CUSTOMER_ID)).thenReturn(entities);
            when(accountMapper.toResponseList(entities)).thenReturn(expected);

            List<UserAccountResponse> result = service.getCurrentUserAccounts(VALID_UUID);

            assertThat(result)
                    .isNotNull()
                    .hasSize(2)
                    .isEqualTo(expected);
        }

        @Test
        void shouldReturnEmptyList_whenCustomerExistsButHasNoAccounts() {
            when(client.getCustomerById(CUSTOMER_ID)).thenReturn(activeCustomer());
            when(accountRepository.findAllByCustomerId(CUSTOMER_ID)).thenReturn(List.of());
            when(accountMapper.toResponseList(List.of())).thenReturn(List.of());

            List<UserAccountResponse> result = service.getCurrentUserAccounts(VALID_UUID);

            assertThat(result).isNotNull().isEmpty();
        }
    }

    @Nested
    class InputValidation {

        @Test
        void shouldThrowException_whenIdIsNull() {
            assertThatThrownBy(() -> service.getCurrentUserAccounts(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowIllegalArgumentException_whenIdIsNotValidUUID() {
            assertThatThrownBy(() -> service.getCurrentUserAccounts("not-a-uuid"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void shouldThrowException_whenIdIsBlank() {
            assertThatThrownBy(() -> service.getCurrentUserAccounts(" "))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class CustomerClientExceptions {

        @Test
        void shouldPropagateException_whenCustomerNotFound() {
            RuntimeException notFound = new RuntimeException("Customer not found.");
            when(client.getCustomerById(CUSTOMER_ID)).thenThrow(notFound);

            assertThatThrownBy(() -> service.getCurrentUserAccounts(VALID_UUID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Customer not found");

            verifyNoInteractions(accountRepository, accountMapper);
        }

        @Test
        void shouldPropagateException_whenCustomerIsInactive() {
            RuntimeException inactive = new RuntimeException("Customer is inactive");
            when(client.getCustomerById(CUSTOMER_ID)).thenThrow(inactive);

            assertThatThrownBy(() -> service.getCurrentUserAccounts(VALID_UUID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("inactive");

            verifyNoInteractions(accountRepository, accountMapper);
        }

        @Test
        void shouldPropagateException_whenExternalServiceFails() {
            RuntimeException serviceError = new RuntimeException("Error communicating with Customer Service");
            when(client.getCustomerById(CUSTOMER_ID)).thenThrow(serviceError);

            assertThatThrownBy(() -> service.getCurrentUserAccounts(VALID_UUID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Customer Service");

            verifyNoInteractions(accountRepository, accountMapper);
        }
    }

    @Nested
    class Contracts {

        @Test
        void shouldAlwaysCallClientBeforeRepository() {
            when(client.getCustomerById(CUSTOMER_ID)).thenReturn(activeCustomer());
            when(accountRepository.findAllByCustomerId(CUSTOMER_ID)).thenReturn(List.of());
            when(accountMapper.toResponseList(any())).thenReturn(List.of());

            service.getCurrentUserAccounts(VALID_UUID);

            var order = inOrder(client, accountRepository);
            order.verify(client).getCustomerById(CUSTOMER_ID);
            order.verify(accountRepository).findAllByCustomerId(CUSTOMER_ID);
        }

        @Test
        void shouldNeverCallRepository_whenClientFails() {
            when(client.getCustomerById(CUSTOMER_ID)).thenThrow(new RuntimeException("fail"));

            assertThatThrownBy(() -> service.getCurrentUserAccounts(VALID_UUID))
                    .isInstanceOf(RuntimeException.class);

            verifyNoInteractions(accountRepository, accountMapper);
        }

        @Test
        void shouldPassRepositoryResultDirectlyToMapper() {
            List<AccountEntity> entities = List.of(mock(AccountEntity.class));

            when(client.getCustomerById(CUSTOMER_ID)).thenReturn(activeCustomer());
            when(accountRepository.findAllByCustomerId(CUSTOMER_ID)).thenReturn(entities);
            when(accountMapper.toResponseList(entities)).thenReturn(List.of(mock(UserAccountResponse.class)));

            service.getCurrentUserAccounts(VALID_UUID);

            verify(accountMapper).toResponseList(entities);
        }

        @Test
        void shouldNeverCallMapper_whenRepositoryFails() {
            when(client.getCustomerById(CUSTOMER_ID)).thenReturn(activeCustomer());
            when(accountRepository.findAllByCustomerId(CUSTOMER_ID)).thenThrow(new RuntimeException("DB error"));

            assertThatThrownBy(() -> service.getCurrentUserAccounts(VALID_UUID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("DB error");

            verifyNoInteractions(accountMapper);
        }
    }
}

