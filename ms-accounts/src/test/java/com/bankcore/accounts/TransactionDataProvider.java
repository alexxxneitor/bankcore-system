package com.bankcore.accounts;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.models.TransactionEntity;
import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionDataProvider {

    public static final String INVALID_UUID = "e7c6be34-c77b-4afa-aebb-327354a9fe0z";

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

    public static TransactionEntity createMockTransaction(AccountEntity account, Instant createdAt) {
        return TransactionEntity.builder()
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .balanceAfter(new BigDecimal("1100.00"))
                .description("Mock transaction")
                .counterpartyAccountNumber("MOCK9121000418450200051332")
                .counterpartyName("Mock Counterparty")
                .status(TransactionStatus.COMPLETED)
                .createdAt(createdAt)
                .build();
    }

    public static TransactionRequest createMockTransactionRequest(String pin){
        return TransactionRequest.builder()
                .amount(BigDecimal.valueOf(100.00))
                .description("test-description")
                .pin(pin)
                .build();
    }
}