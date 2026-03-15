package com.bankcore.accounts.exceptions;

public class IncorrectPinException extends RuntimeException{
    public IncorrectPinException(int remainingAttempts) {
        super("Incorrect PIN. You have " + remainingAttempts +" attempts left before temporary lockout.");
    }

}
