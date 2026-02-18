package com.bankcore.customers.utils.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashMap;
import java.util.Map;

public class AtmPinValidator implements ConstraintValidator<ValidAtmPin, String> {

    @Override
    public boolean isValid(String pin, ConstraintValidatorContext context){

        if (pin == null) {
            return true;
        }

        Map<Character, Integer> frequency = new HashMap<>();

        for (char digit : pin.toCharArray()){
            frequency.merge(digit, 1, Integer::sum);

            if (frequency.get(digit) > 3){
                return false;
            }
        }

        return true;
    }
}
