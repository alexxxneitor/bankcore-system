package com.bankcore.accounts;


import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;
import com.bankcore.accounts.utils.enums.CurrencyCode;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;

public class DataProvider {

    public static final String CUSTOMER_TEST_UUID = "e7c6be34-c77b-4afa-aebb-327354a9fe0b";

    public static AccountEntity createDummyAccount(String accountNumber) {
        return AccountEntity.builder()
                .accountNumber(accountNumber)
                .customerId(UUID.fromString(CUSTOMER_TEST_UUID))
                .accountType(AccountType.SAVINGS)
                .currency(CurrencyCode.USD)
                .balance(new BigDecimal("1500.50"))
                .alias("Ahorros Nómina")
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(new BigDecimal("500.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
