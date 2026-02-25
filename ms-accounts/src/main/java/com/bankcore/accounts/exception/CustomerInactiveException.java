package com.bankcore.accounts.exception;


public class CustomerInactiveException extends RuntimeException {
    public CustomerInactiveException(String id) {
        super("Customer is inactive: " + id);
    }
}
