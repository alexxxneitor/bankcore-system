package com.bankcore.accounts.dto.responses;

import com.bankcore.accounts.utils.AccountStatus;
import com.bankcore.accounts.utils.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class UserAccountResponse {

    private UUID id;
    private String accountNumber;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
    private String alias;
    private AccountStatus status;

}
