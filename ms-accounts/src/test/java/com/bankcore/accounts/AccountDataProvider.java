package com.bankcore.accounts;

import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.models.AccountPinSecurity;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;
import com.bankcore.accounts.utils.enums.CurrencyCode;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountDataProvider {

    public static final String CUSTOMER_TEST_UUID = "e7c6be34-c77b-4afa-aebb-327354a9fe0b";

    public static AccountEntity createMockAccount() {
        AccountEntity account = AccountEntity.builder()
                .accountNumber(generateRandomIban())
                .customerId(UUID.randomUUID())
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .balance(BigDecimal.ZERO)
                .alias("mock-account")
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(BigDecimal.valueOf(1000))
                .security(AccountPinSecurity.builder().build())
                .build();

        account.getSecurity().setAccount(account);
        return account;
    }

    public static AccountEntity createMockAccount(UUID customerId, String alias) {
        AccountEntity account = AccountEntity.builder()
                .accountNumber(generateRandomIban())
                .customerId(customerId)
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.EUR)
                .balance(BigDecimal.ZERO)
                .alias(alias)
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(BigDecimal.valueOf(1000))
                .security(AccountPinSecurity.builder().build())
                .build();

        account.getSecurity().setAccount(account);
        return account;
    }

    private static String generateRandomIban() {
        return "IBAN" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}