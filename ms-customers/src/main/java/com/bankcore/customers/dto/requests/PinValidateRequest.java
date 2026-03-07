package com.bankcore.customers.dto.requests;

import com.bankcore.customers.utils.validators.ValidAtmPin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PinValidateRequest {

    @NotNull(message = "ATM Pin cannot be null")
    @Size(min = 4, max = 4, message = "ATM Pin must be exactly 4 digits")
    @Pattern(regexp = "\\d+", message = "ATM Pin must contain only numbers")
    @ValidAtmPin
    private String pin;
}
