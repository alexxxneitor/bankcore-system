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
import com.bankcore.accounts.utils.DailyWithdrawalLimit;
import com.bankcore.accounts.utils.IbanGeneratorService;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.mappers.AccountMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountManagementImpl implements AccountManagementService{

    private final CustomerClient customerClient;
    private final AccountRepository accountRepository;
    private final IbanGeneratorService ibanGeneratorService;
    private final DailyWithdrawalLimit dailyWithdrawalLimit;
    private final AccountMapper accountMapper;

    private void validateCustomerIsActive(UUID idCustomer){
        CustomerResponse customer = customerClient.getCustomerById(idCustomer);
         if(!customer.isActive()){
             throw new CustomerInactiveException("The authenticated client is not active");
         }
    }

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
        } while (accountRepository.existsByIban(iban));

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
