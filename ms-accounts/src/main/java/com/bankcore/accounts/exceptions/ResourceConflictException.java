package com.bankcore.accounts.exceptions;

public class ResourceConflictException extends RuntimeException{

    public ResourceConflictException(String message) {
        super(message);
    }
}
