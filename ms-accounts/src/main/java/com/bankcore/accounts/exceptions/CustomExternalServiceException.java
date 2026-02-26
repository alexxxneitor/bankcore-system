package com.bankcore.accounts.exceptions;

public class CustomExternalServiceException extends RuntimeException{
    public CustomExternalServiceException(String message) {
        super(message);
    }
}
