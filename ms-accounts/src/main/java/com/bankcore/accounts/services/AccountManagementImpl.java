package com.bankcore.accounts.services;

import com.bankcore.accounts.client.CustomerClient;
import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.UserAccountResponse;
import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.BusinessException;
import com.bankcore.accounts.exceptions.CustomerInactiveException;
import com.bankcore.accounts.exceptions.CustomerNotFoundException;
import com.bankcore.accounts.exceptions.ResourceConflictException;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.repositories.AccountRepository;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.mappers.AccountMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the AccountManagementService interface that provides methods for managing bank accounts.
 * This service interacts with the CustomerClient to validate customer information and uses the AccountRepository
 * to perform database operations related to accounts. It also utilizes the IbanGeneratorService to generate unique
 * IBANs for new accounts and the DailyWithdrawalLimit utility to set withdrawal limits based on account types.
 *
 * @author BankCore Team - Sebastian Orjuela - Cristian Ortiz
 * @version 1.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class AccountManagementImpl implements AccountManagementService {

    private final CustomerClient customerClient;
    private final AccountRepository accountRepository;
    private final IbanGeneratorService ibanGeneratorService;
    private final WithdrawalService withdrawalService;
    private final AccountMapper accountMapper;

    private static final int MAX_IBAN_GENERATION_ATTEMPTS = 5;

    /**
     * Validates if the customer associated with the given ID is active.
     * If the customer is not active, a CustomerInactiveException is thrown.
     *
     * @param idCustomer the UUID of the customer to validate
     * @throws CustomerNotFoundException if the consulted client does not exist
     * @throws CustomerInactiveException if the customer is not active
     */
    private void validateCustomerIsActive(UUID idCustomer) {
        CustomerResponse customer = customerClient.getCustomerById(idCustomer);
        if (!customer.exists()) {
            throw new CustomerNotFoundException("The customer is not registered in the system");
        }

        if (!customer.isActive()) {
            throw new CustomerInactiveException("The authenticated client is not active");
        }
    }

    /// Registers a new account for a customer
    @Override
    @Transactional
    public AccountRegisterResponse registerAccount(AccountRegisterRequest request, UUID id) {

        validateCustomerIsActive(id);

        if (accountRepository.countByCustomerId(id) >= 3) {
            throw new BusinessException("Customer has reached the maximum number of accounts allowed");
        }

        if (accountRepository.existsByAliasAndCustomerId(request.getAlias(), id)) {
            throw new ResourceConflictException("The alias is already in use on an account under your name");
        }

        String iban = generateUniqueIban();

        AccountEntity accountEntity =
                AccountEntity.builder()
                        .accountNumber(iban)
                        .customerId(id)
                        .accountType(request.getAccountType())
                        .currency(request.getCurrency())
                        .balance(BigDecimal.ZERO)
                        .alias(request.getAlias())
                        .status(AccountStatus.ACTIVE)
                        .dailyWithdrawalLimit(withdrawalService.resolveDailyLimit(request.getAccountType()))
                        .build();

        accountRepository.save(accountEntity);

        return accountMapper.toAccountRegisterResponse(accountEntity);
    }


    /**
     * Retrieves all bank accounts associated with the authenticated customer.
     * <p>
     * Validates that the provided ID is not null or blank, parses it as a {@link UUID},
     * verifies the customer exists and is active via the customer service,
     * and returns the mapped account list.
     * </p>
     *
     * @param id the string representation of the customer's {@link UUID}
     * @return a {@link List} of {@link UserAccountResponse} associated with the customer,
     * or an empty list if no accounts are found
     * @throws IllegalArgumentException                                        if {@code id} is null, blank, or not a valid UUID format
     * @throws com.bankcore.accounts.exceptions.CustomerNotFoundException      if the customer does not exist in the Customer Service
     * @throws CustomerInactiveException                                       if the customer account is inactive
     * @throws com.bankcore.accounts.exceptions.CustomExternalServiceException if the Customer Service returns an error or empty response
     */
    @Override
    public List<UserAccountResponse> getCurrentUserAccounts(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id must not be null or blank");
        }

        UUID customerId = UUID.fromString(id);

        customerClient.getCustomerById(customerId);

        List<AccountEntity> accounts = accountRepository.findAllByCustomerId(customerId);
        return accountMapper.toResponseList(accounts);
    }

    //IBAN generation
    private String generateUniqueIban() {

        for (int attempt = 1; attempt <= MAX_IBAN_GENERATION_ATTEMPTS; attempt++) {

            String iban = ibanGeneratorService.generateSpanishIban();

            if (!accountRepository.existsByAccountNumber(iban)) {
                return iban;
            }

            log.warn("IBAN collision detected (attempt {}): {}", attempt, iban);
        }

        throw new ResourceConflictException("Unable to generate a unique IBAN after multiple attempts");
    }
}
