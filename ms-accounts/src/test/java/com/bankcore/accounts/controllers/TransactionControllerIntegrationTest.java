package com.bankcore.accounts.controllers;

import com.bankcore.accounts.AbstractIntegrationTest;
import com.bankcore.accounts.AccountDataProvider;
import com.bankcore.accounts.TransactionDataProvider;
import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.integrations.client.CustomerClient;
import com.bankcore.accounts.integrations.dto.request.PinValidateRequest;
import com.bankcore.accounts.integrations.dto.responses.CustomerResponse;
import com.bankcore.accounts.integrations.dto.responses.PinValidateResponse;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.models.AccountPinSecurity;
import com.bankcore.accounts.repositories.AccountPinSecurityRepository;
import com.bankcore.accounts.repositories.AccountRepository;
import com.bankcore.accounts.repositories.TransactionRepository;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import com.bankcore.accounts.utils.enums.UserRole;
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
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerClient customerClient;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountPinSecurityRepository accountPinSecurityRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private AccountEntity account;

    @BeforeEach
    public void setUp() {
        Mockito.reset(customerClient);
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        account = accountRepository.save(AccountDataProvider.createMockAccount());
    }

    @TestConfiguration
    public static class TestConfig {
        @Bean
        CustomerClient customerClient() {
            return Mockito.mock(CustomerClient.class);
        }
    }

    @Test
    public void shouldRegisterDepositInAccountSuccessfully() throws Exception{
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        PinValidateRequest requestPin = PinValidateRequest.builder().pin(request.getPin()).build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        Mockito.when(customerClient.validateCustomerPin(customerId, requestPin))
                .thenReturn(new PinValidateResponse(true));

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").exists())
                .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.name()))
                .andExpect(jsonPath("$.amount").value(request.getAmount()))
                .andExpect(jsonPath("$.balanceBefore").value(account.getBalance().doubleValue()))
                .andExpect(jsonPath("$.balanceAfter").value(account.getBalance().add(request.getAmount()).doubleValue()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void shouldRegisterDepositInAccountSuccessfullyWithDescriptionNull() throws Exception{
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description(null)
                .pin("1234")
                .build();

        PinValidateRequest requestPin = PinValidateRequest.builder().pin(request.getPin()).build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        Mockito.when(customerClient.validateCustomerPin(customerId, requestPin))
                .thenReturn(new PinValidateResponse(true));

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.referenceNumber").exists())
                .andExpect(jsonPath("$.type").value(TransactionType.DEPOSIT.name()))
                .andExpect(jsonPath("$.amount").value(request.getAmount()))
                .andExpect(jsonPath("$.balanceBefore").value(account.getBalance().doubleValue()))
                .andExpect(jsonPath("$.balanceAfter").value(account.getBalance().add(request.getAmount()).doubleValue()))
                .andExpect(jsonPath("$.description").value(request.getDescription()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void shouldRegisterDepositInAccountNotFound() throws Exception{
        UUID customerId = account.getCustomerId();
        UUID accountId = UUID.randomUUID();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        PinValidateRequest requestPin = PinValidateRequest.builder().pin(request.getPin()).build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        Mockito.when(customerClient.validateCustomerPin(customerId, requestPin))
                .thenReturn(new PinValidateResponse(true));

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn409WhenDepositingToNonexistentOrInactiveAccount() throws Exception{
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        AccountEntity account1 = account;
        account1.setStatus(AccountStatus.INACTIVE);
        accountRepository.save(account1);

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.description").value(containsString(String.join(" ", "status is", account1.getStatus().name()))));
    }

    @Test
    public void shouldRegisterDepositWithCustomerNotActive() throws Exception{
        UUID customerId = UUID.randomUUID();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, false));

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldRegisterDepositWithCustomerNotExists() throws Exception{
        UUID customerId = UUID.randomUUID();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, false, false));

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldRegisterDepositWithBalanceZero() throws Exception{
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.ZERO)
                .description("test deposit")
                .pin("1234")
                .build();

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldRegisterDepositWithPinInvalid() throws Exception{
        UUID accountId = account.getId();
        UUID customerId = account.getCustomerId();

        TransactionRequest pinMinimumSize = TransactionDataProvider.createMockTransactionRequest("12");
        TransactionRequest pinMaximumSize = TransactionDataProvider.createMockTransactionRequest("12345");
        TransactionRequest pinLetters = TransactionDataProvider.createMockTransactionRequest("12mp");
        TransactionRequest pinSameDigits = TransactionDataProvider.createMockTransactionRequest("3333");
        TransactionRequest pinBlankSpace = TransactionDataProvider.createMockTransactionRequest("");
        TransactionRequest pinNull = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin(null)
                .build();

        List<TransactionRequest> invalidRequests = List.of(
                pinMinimumSize,
                pinMaximumSize,
                pinLetters,
                pinSameDigits,
                pinBlankSpace,
                pinNull
        );

        for (TransactionRequest request : invalidRequests) {

            mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                    .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                    .andExpect(jsonPath("$.description").exists());
        }
    }

    @Test
    public void shouldReturn403WhenUserDoesNotHaveRequiredRole() throws Exception{
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.ADMIN.name())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.FORBIDDEN.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn401WhenRequestIsNotAuthenticated() throws Exception{
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldRegisterDepositInAccountWithPinIncorrect() throws Exception{
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        PinValidateRequest requestPin = PinValidateRequest.builder().pin(request.getPin()).build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        Mockito.when(customerClient.validateCustomerPin(customerId, requestPin))
                .thenReturn(new PinValidateResponse(false));

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").value(containsString("3 attempts")));
    }

    @Test
    public void shouldLockAccountAfterFourthIncorrectPinAttemptWith423Status() throws Exception {
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        PinValidateRequest requestPin = PinValidateRequest.builder().pin(request.getPin()).build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        Mockito.when(customerClient.validateCustomerPin(customerId, requestPin))
                .thenReturn(new PinValidateResponse(false));

        Instant expectedUnlock = Instant.now().plus(15, ChronoUnit.MINUTES);

        int[] remainingAttempts = {3, 2, 1};

        // Hacer 4 requests consecutivos
        for (int i = 1; i <= 4; i++) {
            ResultActions result = mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())));

            if (i < 4) {
                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                        .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                        .andExpect(jsonPath("$.description").value(
                                containsString(remainingAttempts[i - 1] + " attempts")
                        ));
            } else {
                result.andExpect(status().isLocked())
                        .andExpect(jsonPath("$.code").value(HttpStatus.LOCKED.value()))
                        .andExpect(jsonPath("$.name").value(HttpStatus.LOCKED.getReasonPhrase()))
                        .andExpect(jsonPath("$.description").value(
                                containsString("temporarily locked")
                        ))
                        .andExpect(jsonPath("$.description").value(
                                containsString(expectedUnlock.truncatedTo(ChronoUnit.MINUTES).toString().substring(0,16))
                        ));
            }
        }
    }

    @Test
    public void shouldPermanentlyBlockAccountOnFourthFailedPinAttempt() throws Exception {
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        AccountPinSecurity pinSecurity = account.getSecurity();
        pinSecurity.setFailedAttempts(4);
        pinSecurity.setTemporaryLockUntil(Instant.now().minus(1, ChronoUnit.MINUTES));
        pinSecurity.setLastFailedAttemptAt(Instant.now().minus(16, ChronoUnit.MINUTES));
        pinSecurity.setPermanentLock(false);

        accountPinSecurityRepository.save(pinSecurity);

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        PinValidateRequest requestPin = PinValidateRequest.builder().pin(request.getPin()).build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        Mockito.when(customerClient.validateCustomerPin(customerId, requestPin))
                .thenReturn(new PinValidateResponse(false));

        int[] remainingAttempts = {3, 2, 1};

        // Hacer 4 requests consecutivos
        for (int i = 1; i <= 4; i++) {
            ResultActions result = mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())));

            if (i < 4) {
                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                        .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                        .andExpect(jsonPath("$.description").value(
                                containsString(remainingAttempts[i - 1] + " attempts")
                        ));
            } else {
                result.andExpect(status().isLocked())
                        .andExpect(jsonPath("$.code").value(HttpStatus.LOCKED.value()))
                        .andExpect(jsonPath("$.name").value(HttpStatus.LOCKED.getReasonPhrase()))
                        .andExpect(jsonPath("$.description").value(
                                containsString("permanently blocked")
                        ));
            }
        }
    }

    @Test
    public void shouldPermanentlyBlockAccountAndFreezeOnNextAttempt() throws Exception {
        UUID customerId = account.getCustomerId();
        UUID accountId = account.getId();

        AccountPinSecurity pinSecurity = account.getSecurity();
        pinSecurity.setFailedAttempts(4);
        pinSecurity.setTemporaryLockUntil(Instant.now().minus(1, ChronoUnit.MINUTES));
        pinSecurity.setLastFailedAttemptAt(Instant.now().minus(16, ChronoUnit.MINUTES));
        pinSecurity.setPermanentLock(false);

        accountPinSecurityRepository.save(pinSecurity);

        TransactionRequest request = TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test deposit")
                .pin("1234")
                .build();

        PinValidateRequest requestPin = PinValidateRequest.builder().pin(request.getPin()).build();

        Mockito.when(customerClient.getCustomerById(customerId))
                .thenReturn(new CustomerResponse(customerId, true, true));

        Mockito.when(customerClient.validateCustomerPin(customerId, requestPin))
                .thenReturn(new PinValidateResponse(false));

        // Hacer 4 requests consecutivos
        for (int i = 1; i <= 4; i++) {
            ResultActions result = mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
                    .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())));

            if (i < 4) {
                result.andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                        .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                        .andExpect(jsonPath("$.description").exists());
            } else {
                result.andExpect(status().isLocked())
                        .andExpect(jsonPath("$.code").value(HttpStatus.LOCKED.value()))
                        .andExpect(jsonPath("$.name").value(HttpStatus.LOCKED.getReasonPhrase()))
                        .andExpect(jsonPath("$.description").exists());
            }
        }

        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.CONFLICT.getReasonPhrase()))
                .andExpect(jsonPath("$.description").value(containsString("status is FROZEN")));
    }

    @Test
    public void shouldReturn400WhenUuidFormatIsInvalid() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", TransactionDataProvider.INVALID_UUID)
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenPageNumberIsBelowOne() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("page", "0")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenPageIsAlphabetic() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("page", "o")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenSizeExceedsLimit() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("size", "51")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenSizeIsLessThanOne() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("size", "0")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenSizeIsAlphabetic() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("size", "o")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenFromDateHasInvalidFormat() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("fromDate", "2024-03-20 10:15:30")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenToDateHasInvalidFormat() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("toDate", "2024-03-20 10:15:30")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn400WhenFromDateIsAfterToDate() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("fromDate", "2024-03-21T10:00:00Z")
                        .param("toDate", "2024-03-20T10:00:00Z")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldAllowSameFromDateAndToDate() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("fromDate", "2024-03-20T10:00:00Z")
                        .param("toDate", "2024-03-20T10:00:00Z")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn400WhenTypeIsNotInEnum() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("type", "type")
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn200WhenTypeIsEnumAndLowercase() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("type", TransactionType.DEPOSIT.name().toLowerCase())
                        .with(user(customerId.toString()).roles(UserRole.CUSTOMER.name())))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturn401WhenIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.UNAUTHORIZED.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void shouldReturn403WhenUserDoesNotHaveRequiredRole_() throws Exception {
        UUID customerId = account.getCustomerId();

        mockMvc.perform(get("/api/accounts/{accountId}/transactions", account.getId())
                        .param("type", "type")
                        .with(user(customerId.toString()).roles(UserRole.ADMIN.name())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.name").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andExpect(jsonPath("$.description").exists());
    }
}
