package com.bankcore.accounts.exceptions;

public class CustomerInactiveException extends RuntimeException{
    public CustomerInactiveException(String message) {
        super(message);
    }
}
