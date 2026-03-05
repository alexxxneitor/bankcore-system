package com.bankcore.accounts;

import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountDataProvider {

    public static AccountEntity createMockAccount() {
        return AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber(generateRandomIban())
                .customerId(UUID.randomUUID())
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .alias("mock-account")
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(BigDecimal.valueOf(1000))
                .build();
    }

    public static AccountEntity createMockAccount(UUID customerId, String alias) {
        return AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber(generateRandomIban())
                .customerId(customerId)
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .alias(alias)
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(BigDecimal.valueOf(1000))
                .build();
    }

    public static AccountEntity createMockAccount(UUID customerId, String alias, String iban) {
        return AccountEntity.builder()
                .id(UUID.randomUUID())
                .accountNumber(iban)
                .customerId(customerId)
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.ZERO)
                .alias(alias)
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(BigDecimal.valueOf(1000))
                .build();
    }

    public static String generateRandomIban() {
        return "IBAN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
