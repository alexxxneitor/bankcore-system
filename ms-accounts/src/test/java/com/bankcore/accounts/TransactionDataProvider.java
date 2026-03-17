package com.bankcore.accounts;

import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.models.TransactionEntity;
import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionDataProvider {

    public static TransactionEntity createMockTransaction(AccountEntity account) {
        return TransactionEntity.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceAfter(new BigDecimal("1100.00"))
                .description("Mock transaction")
                .counterpartyAccountNumber("MOCK9121000418450200051332")
                .counterpartyName("Mock Counterparty")
                .status(TransactionStatus.COMPLETED)
                .createdAt(Instant.now())
                .build();
    }
}