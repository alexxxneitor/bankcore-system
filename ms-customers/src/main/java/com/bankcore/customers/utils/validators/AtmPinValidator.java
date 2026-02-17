package com.bankcore.customers.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtmPinValidator implements ConstraintValidator<ValidAtmPin, String> {

    @Override
    public boolean isValid(String pin, ConstraintValidatorContext context){
        if (pin == null) return false;
        if (!pin.matches("^\\d{4}$")) return false;

        return !pin.equals("0000") && !pin.equals("1234") && !pin.equals("1111") && !pin.equals("2222") &&
               !pin.equals("3333") && !pin.equals("4444") && !pin.equals("5555") && !pin.equals("6666") &&
               !pin.equals("7777") && !pin.equals("8888") && !pin.equals("9999");
    }
}
