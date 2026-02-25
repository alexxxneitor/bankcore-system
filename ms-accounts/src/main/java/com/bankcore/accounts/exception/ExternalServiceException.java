package com.bankcore.accounts.exception;

public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String errorCommunicatingWithCustomerService) {
        super(errorCommunicatingWithCustomerService);
    }
}
