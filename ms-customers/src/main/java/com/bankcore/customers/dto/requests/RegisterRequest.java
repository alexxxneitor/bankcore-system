package com.bankcore.customers.dto.requests;


import com.bankcore.customers.utils.validators.ValidAtmPin;
import com.bankcore.customers.utils.validators.ValidPassword;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequest {

    @NotBlank(message = "DNI is required")
    private String dni;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @ValidPassword
    private String password;

    @NotNull(message = "ATM Pin cannot be null")
    @Size(min = 4, max = 4, message = "ATM Pin must be exactly 4 digits")
    @Pattern(regexp = "\\d+", message = "ATM Pin must contain only numbers")
    @ValidAtmPin
    private String atmPin;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;
}
