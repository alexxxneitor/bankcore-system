package com.bankcore.customers.exception;

public class UserProfileNotFoundException extends RuntimeException {

    public UserProfileNotFoundException() {
    }

    public UserProfileNotFoundException(String message) {
        super(message);
    }

}
