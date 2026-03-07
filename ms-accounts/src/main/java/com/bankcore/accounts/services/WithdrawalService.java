package com.bankcore.accounts.services;

import com.bankcore.accounts.config.DailyWithdrawalLimit;
import com.bankcore.accounts.utils.enums.AccountType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WithdrawalService {

    private final DailyWithdrawalLimit withdrawalLimit;

    public WithdrawalService(DailyWithdrawalLimit withdrawalLimit) {
        this.withdrawalLimit = withdrawalLimit;
    }

    public BigDecimal resolveDailyLimit(AccountType type) {
        BigDecimal limit = withdrawalLimit.getLimits().get(type);

        if (limit == null) {
            throw new IllegalArgumentException(
                    "No withdrawal limit configured for account type: " + type
            );
        }

        return limit;
    }
}
