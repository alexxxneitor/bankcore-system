package com.bankcore.accounts.exceptions;

/**
 * Exception thrown when a request contains invalid or unsupported parameters.
 *
 * @author Bankcore Team - Sebastian Orjuela
 * @version 1.0
 */
public class CustomInvalidParameter extends RuntimeException {

    public CustomInvalidParameter(String message) {
        super(message);
    }
}
