package com.bankcore.accounts.exceptions;

import java.time.Instant;

public class AccountTemporarilyLockedException extends RuntimeException{
    public AccountTemporarilyLockedException(Instant until) {
        super("Account temporarily locked until " + until);
    }
}
