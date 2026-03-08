package com.bankcore.accounts.dto.responses;

import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO representing a bank account belonging to the authenticated customer.
 * <p>
 * Returned as part of the account listing endpoint, containing
 * the core details of each account associated with the user.
 * </p>
 * @author BankCore Team - Cristian Ortiz
 */
@Data
@Builder
public class UserAccountResponse {

    /** Unique identifier of the account. */
    private UUID id;

    /** The account number used for transactions and identification. */
    private String accountNumber;

    /** The type of account. */
    private AccountType accountType;

    /** Currency code associated with the account (e.g., USD, EUR). */
    private String currency;

    /** Current available balance of the account. */
    private BigDecimal balance;

    /** Optional user-defined alias or label for the account. */
    private String alias;

    /** Current status of the account (e.g., ACTIVE, INACTIVE, BLOCKED). */
    private AccountStatus status;

}