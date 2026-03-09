package com.bankcore.accounts.exceptions;


// Excepción limpia — sin accountId expuesto
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException() {
        super("Account not found");
    }
}