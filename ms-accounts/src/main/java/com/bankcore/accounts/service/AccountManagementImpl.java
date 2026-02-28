package com.bankcore.accounts.service;

import com.bankcore.accounts.client.CustomerClient;
import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.CustomerResponse;
import com.bankcore.accounts.exceptions.BusinessException;
import com.bankcore.accounts.exceptions.CustomerInactiveException;
import com.bankcore.accounts.exceptions.ResourceConflictException;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.repositries.AccountRepository;
import com.bankcore.accounts.config.DailyWithdrawalLimit;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.mappers.AccountMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Implementation of the AccountManagementService interface that provides methods for managing bank accounts.
 * This service interacts with the CustomerClient to validate customer information and uses the AccountRepository
 * to perform database operations related to accounts. It also utilizes the IbanGeneratorService to generate unique
 * IBANs for new accounts and the DailyWithdrawalLimit utility to set withdrawal limits based on account types.
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
@Service
@AllArgsConstructor
public class AccountManagementImpl implements AccountManagementService{

    private final CustomerClient customerClient;
    private final AccountRepository accountRepository;
    private final IbanGeneratorService ibanGeneratorService;
    private final DailyWithdrawalLimit dailyWithdrawalLimit;
    private final AccountMapper accountMapper;

    /**
     * Validates if the customer associated with the given ID is active.
     * If the customer is not active, a CustomerInactiveException is thrown.
     *
     * @param idCustomer the UUID of the customer to validate
     * @throws CustomerInactiveException if the customer is not active
     */
    private void validateCustomerIsActive(UUID idCustomer){
        CustomerResponse customer = customerClient.getCustomerById(idCustomer);
         if(!customer.isActive()){
             throw new CustomerInactiveException("The authenticated client is not active");
         }
    }

    /// Registers a new account for a customer
    @Override
    public AccountRegisterResponse registerAccount(AccountRegisterRequest request, UUID id) {
       validateCustomerIsActive(id);

        if (accountRepository.countByCustomerId(id) >= 3) {
            throw new BusinessException("Customer has reached the maximum number of accounts allowed");
        }

        if(accountRepository.existsByAliasAndCustomerId(request.getAlias(), id)){
            throw new ResourceConflictException("The alias is already in use on an account under your name");
        }

        String iban;

        do {
            iban = ibanGeneratorService.generateSpanishIban();
        } while (accountRepository.existsByAccountNumber(iban));

        AccountEntity accountEntity =
                AccountEntity.builder()
                        .accountNumber(iban)
                        .customerId(id)
                        .accountType(request.getAccountType())
                        .currency(request.getCurrency())
                        .balance(BigDecimal.ZERO)
                        .alias(request.getAlias())
                        .status(AccountStatus.ACTIVE)
                        .dailyWithdrawalLimit(dailyWithdrawalLimit.resolveDailyLimit(request.getAccountType()))
                        .build();

        accountRepository.save(accountEntity);

        return accountMapper.toAccountRegisterResponse(accountEntity);
    }
}
