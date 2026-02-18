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
    @NotNull(message = "DNI cannot be null")
    private String dni;

    @NotBlank(message = "First name is required")
    @NotNull(message = "First name cannot be null")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @NotNull(message = "Last name cannot be null")
    private String lastName;

    @NotBlank(message = "Email is required")
    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Password cannot be null")
    @ValidPassword
    private String password;

    @NotNull(message = "ATM Pin cannot be null")
    @Size(min = 4, max = 4, message = "ATM Pin must be exactly 4 digits")
    @Pattern(regexp = "\\d+", message = "ATM Pin must contain only numbers")
    @ValidAtmPin
    private String atmPin;

    @NotBlank(message = "Phone number is required")
    @NotNull(message = "Phone number cannot be null")
    private String phone;

    @NotBlank(message = "Address is required")
    @NotNull(message = "Address cannot be null")
    private String address;
}
