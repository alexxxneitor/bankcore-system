package com.bankcore.accounts;

import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;
import com.bankcore.accounts.utils.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountDataProvider {

    public static AccountEntity createMockAccount() {
        return AccountEntity.builder()
                .accountNumber(generateRandomIban())
                .customerId(UUID.randomUUID())
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .balance(BigDecimal.ZERO)
                .alias("mock-account")
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(BigDecimal.valueOf(1000))
                .build();
    }

    public static AccountEntity createMockAccount(UUID customerId, String alias) {
        return AccountEntity.builder()
                .accountNumber(generateRandomIban())
                .customerId(customerId)
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .balance(BigDecimal.ZERO)
                .alias(alias)
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(BigDecimal.valueOf(1000))
                .build();
    }

    private static String generateRandomIban() {
        return "IBAN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}