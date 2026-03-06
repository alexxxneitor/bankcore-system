package com.bankcore.accounts.repositories;

import com.bankcore.accounts.AbstractIntegrationTest;
import com.bankcore.accounts.AccountDataProvider;
import com.bankcore.accounts.models.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class AccountRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void shouldFindById() {
        AccountEntity acc = AccountDataProvider.createMockAccount();
        AccountEntity saved = accountRepository.save(acc);

        Optional<AccountEntity> found = accountRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void shouldCountByCustomerId() {
        UUID customerId = UUID.randomUUID();

        accountRepository.save(AccountDataProvider.createMockAccount(customerId, "acc1"));
        accountRepository.save(AccountDataProvider.createMockAccount(customerId, "acc2"));

        long count = accountRepository.countByCustomerId(customerId);

        assertEquals(2, count);
    }

    @Test
    void shouldReturnTrueIfAliasExists() {
        UUID customerId = UUID.randomUUID();

        accountRepository.save(
                AccountDataProvider.createMockAccount(customerId, "my-alias")
        );

        assertTrue(accountRepository.existsByAliasAndCustomerId("my-alias", customerId));
        assertFalse(accountRepository.existsByAliasAndCustomerId("other-alias", customerId));
    }

    @Test
    void shouldReturnTrueIfAccountNumberExists() {
        AccountEntity saved = accountRepository.save(
                AccountDataProvider.createMockAccount()
        );

        assertTrue(accountRepository.existsByAccountNumber(saved.getAccountNumber()));
        assertFalse(accountRepository.existsByAccountNumber("IBAN999"));
    }
}
