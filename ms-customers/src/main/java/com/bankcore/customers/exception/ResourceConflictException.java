package com.bankcore.customers.exception;

/**
 * Exception thrown when an attempt is made to create a resource that already exists, typically during
 * registration.
 */
public class ResourceConflictException extends RuntimeException{

    private static final Long serialVersionUID = 1L;

    /**
     * Constructs a new {@code ResourceConflictException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public ResourceConflictException(String message) {
        super(message);
    }
}
