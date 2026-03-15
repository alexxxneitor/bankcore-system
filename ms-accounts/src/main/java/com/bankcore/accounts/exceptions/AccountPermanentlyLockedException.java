package com.bankcore.accounts.exceptions;

public class AccountPermanentlyLockedException extends RuntimeException{
    public AccountPermanentlyLockedException() {
        super("Account permanently blocked");
    }
}
