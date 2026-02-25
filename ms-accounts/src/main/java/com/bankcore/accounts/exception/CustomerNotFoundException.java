package com.bankcore.accounts.exception;

// 404 - not found
public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String id) {
        super(id);
    }

}
