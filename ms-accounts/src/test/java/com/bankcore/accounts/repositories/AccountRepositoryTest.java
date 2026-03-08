package com.bankcore.accounts.repositories;

import com.bankcore.accounts.AbstractIntegrationTest;
import com.bankcore.accounts.AccountDataProvider;
import com.bankcore.accounts.models.AccountEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void shouldFindAllByCustomerId() {
        UUID customerId = UUID.fromString(AccountDataProvider.CUSTOMER_TEST_UUID);

        List<AccountEntity> accounts = List.of(
                AccountDataProvider.createMockAccount(customerId, "Some alias 1"),
                AccountDataProvider.createMockAccount(customerId, "Some alias 2"),
                AccountDataProvider.createMockAccount(customerId, "Some alias 3")
        );

        accountRepository.saveAll(accounts);

        List<AccountEntity> result = accountRepository.findAllByCustomerId(customerId);

        assertThat(result).hasSize(3);
        assertThat(result).allMatch(account -> account.getCustomerId().equals(customerId));
    }
}
