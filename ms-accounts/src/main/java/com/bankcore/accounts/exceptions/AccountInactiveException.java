package com.bankcore.accounts.exceptions;


public class AccountInactiveException extends RuntimeException{

    public AccountInactiveException(){
        super("Account is not active");
    }
}
