package com.bankcore.accounts.config;

import com.bankcore.accounts.utils.enums.AccountType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Utility class to determine the daily withdrawal limit based on the account type.
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
@Component
@ConfigurationProperties(prefix = "accounts.withdrawal")
public class DailyWithdrawalLimit {

    private Map<AccountType, BigDecimal> limits;

    public BigDecimal resolveDailyLimit(AccountType type) {
        BigDecimal limit = limits.get(type);

        if (limit == null) {
            throw new IllegalArgumentException("No withdrawal limit configured for account type: " + type);
        }

        return limit;
    }

    public Map<AccountType, BigDecimal> getLimits() {
        return limits;
    }

    public void setLimits(Map<AccountType, BigDecimal> limits) {
        this.limits = limits;
    }
}
