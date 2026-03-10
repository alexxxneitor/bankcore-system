package com.bankcore.accounts.dto.responses;

import com.bankcore.accounts.utils.enums.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

@Value
@Builder
public class TransactionResponse {

    String referenceNumber;
    TransactionType type;
    BigDecimal amount;
    BigDecimal balanceBefore;
    BigDecimal balanceAfter;
    String description;
    Instant timestamp;
}
