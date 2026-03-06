package com.bankcore.accounts.controllers;

import com.bankcore.accounts.AbstractIntegrationTest;
import com.bankcore.accounts.client.CustomerClient;
import com.bankcore.accounts.config.DailyWithdrawalLimit;
import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.repositories.AccountRepository;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;
import com.bankcore.accounts.utils.enums.CurrencyCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


public class AccountControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DailyWithdrawalLimit dailyWithdrawalLimit;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        CustomerClient customerClient() {
            return Mockito.mock(CustomerClient.class);
        }
    }

    @Test
    void shouldRegisterAccountSuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();
        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .accountType(AccountType.CHECKING)
                .currency(CurrencyCode.EUR)
                .alias("MySavings")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles("CUSTOMER")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.alias").value(request.getAlias()))
                .andExpect(jsonPath("$.accountType").value(request.getAccountType().name()))
                .andExpect(jsonPath("$.currency").value(request.getCurrency().name()));
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, false, false));

        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .accountType(AccountType.CHECKING)
                .currency(CurrencyCode.EUR)
                .alias("TestAlias")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles("CUSTOMER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void shouldReturn403WhenCustomerInactive() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, false));

        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .alias("InactiveAlias")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles("CUSTOMER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void shouldReturn401WhenCustomerNotAuthenticated() throws Exception {

        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .alias("InactiveAlias")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void shouldReturnConflictWhenAliasAlreadyExists() throws Exception {
        UUID customerId = UUID.randomUUID();
        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        AccountEntity existingAccount = AccountEntity.builder()
                .customerId(customerId)
                .alias("DuplicateAlias")
                .accountNumber("ES1234567890123456789012")
                .accountType(AccountType.CHECKING)
                .currency(CurrencyCode.EUR)
                .balance(BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(dailyWithdrawalLimit.resolveDailyLimit(AccountType.CHECKING))
                .build();
        accountRepository.save(existingAccount);

        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .alias("DuplicateAlias")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles("CUSTOMER")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void shouldReturnUnprocessableEntityWhenCustomerAlreadyHasThreeAccounts() throws Exception {

        UUID customerId = UUID.randomUUID();

        when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        for (int i = 1; i <= 3; i++) {
            AccountEntity account = AccountEntity.builder()
                    .customerId(customerId)
                    .alias("Account" + i)
                    .accountNumber("ES12345678901234567890" + i)
                    .accountType(AccountType.SAVINGS)
                    .currency(CurrencyCode.EUR)
                    .balance(BigDecimal.ZERO)
                    .status(AccountStatus.ACTIVE)
                    .dailyWithdrawalLimit(
                            dailyWithdrawalLimit.resolveDailyLimit(AccountType.SAVINGS)
                    )
                    .build();

            accountRepository.save(account);
        }

        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .alias("FourthAccount")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .with(user(customerId.toString()).roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidRequest() throws Exception {
        UUID customerId = UUID.randomUUID();

        AccountRegisterRequest request = AccountRegisterRequest.builder()
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .alias("")
                .build();

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles("CUSTOMER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    void shouldReturnBadRequestForInvalidRequestEnums() throws Exception {

        UUID customerId = UUID.randomUUID();

        String invalidJson = """
        {
            "accountType": "EnumNotPermited",
            "currency": "EnumNotPermited",
            "alias": "my-alias"
        }
        """;

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson)
                        .with(user(customerId.toString()).roles("CUSTOMER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }
}