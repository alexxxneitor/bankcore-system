package com.bankcore.accounts.service;

import com.bankcore.accounts.client.CustomerClient;
import com.bankcore.accounts.dto.responses.UserAccountResponse;
import com.bankcore.accounts.model.AccountEntity;
import com.bankcore.accounts.repository.AccountRepository;
import com.bankcore.accounts.utils.mappers.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountManagementImpl implements AccountManagementService{

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final CustomerClient client;

    /**
     * @return
     */
    @Override
    public List<UserAccountResponse> getCurrentUserAccounts(String id) {

        client.getCustomer(UUID.fromString(id));

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id must not be null or blank");
        }

        List<AccountEntity> accounts =  accountRepository.findAllByCustomerId(UUID.fromString(id));
        return accountMapper.toResponseList(accounts);
    }
}
