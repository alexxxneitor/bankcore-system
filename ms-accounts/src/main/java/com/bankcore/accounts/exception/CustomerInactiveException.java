package com.bankcore.accounts.exception;

// 403 - forbidden
public class CustomerInactiveException extends RuntimeException {
    public CustomerInactiveException(String id) {
        super("Customer is inactive: " + id);
    }
}
