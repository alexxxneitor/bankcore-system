package com.bankcore.accounts.utils;

import com.bankcore.accounts.utils.enums.AccountType;

import java.math.BigDecimal;

/**
 * Utility class to determine the daily withdrawal limit based on the account type.
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
public class DailyWithdrawalLimit {

    public BigDecimal resolveDailyLimit(AccountType type) {
        return switch (type) {
            case SAVINGS -> new BigDecimal("1000.00");
            case CHECKING -> new BigDecimal("3000.00");
        };
    }
}
