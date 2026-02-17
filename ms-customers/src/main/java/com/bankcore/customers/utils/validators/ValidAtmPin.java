package com.bankcore.customers.utils.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Documented
@Constraint(validatedBy = AtmPinValidator.class)
@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAtmPin {
    String message() default "Invalid ATM PIN";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
