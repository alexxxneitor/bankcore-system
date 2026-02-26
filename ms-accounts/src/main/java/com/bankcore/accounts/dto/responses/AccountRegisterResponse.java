package com.bankcore.accounts.dto.responses;

import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;
import com.bankcore.accounts.utils.enums.CurrencyCode;
import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@Builder
public class AccountRegisterResponse {

    private UUID id;
    private String accountNumber;
    private UUID customerId;
    private AccountType accountType;
    private CurrencyCode currency;
    private BigDecimal balance;
    private String alias;
    private AccountStatus status;
    private Instant createdAt;
}
