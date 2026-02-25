package com.bankcore.accounts;

import com.bankcore.accounts.model.AccountEntity;
import com.bankcore.accounts.utils.AccountStatus;
import com.bankcore.accounts.utils.AccountType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class DataProvider {

    public static final String CUSTOMER_TEST_UUID = "e7c6be34-c77b-4afa-aebb-327354a9fe0b";

    public static AccountEntity createDummyAccount() {
        return AccountEntity.builder()
                .accountNumber("1234567890")
                .customerId(UUID.fromString(CUSTOMER_TEST_UUID))
                .accountType(AccountType.SAVINGS)
                .currency("USD")
                .balance(new BigDecimal("1500.50"))
                .alias("Ahorros Nómina")
                .status(AccountStatus.ACTIVE)
                .dailyWithdrawalLimit(new BigDecimal("500.00"))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
