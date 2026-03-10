package com.bankcore.accounts.exceptions;

public class AccountNotFoundException extends RuntimeException{

    public AccountNotFoundException(){
        super("Account not found");
    }
}
