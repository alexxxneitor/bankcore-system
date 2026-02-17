package com.bankcore.customers.dto.requests;


import com.bankcore.customers.utils.validators.ValidAtmPin;
import com.bankcore.customers.utils.validators.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterRequest {

    @NotBlank(message = "DNI is required")
    private int dni;

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

    @NotBlank(message = "ATM Pin is required")
    @Size(min = 4, max = 4, message = "ATM Pin must be exactly 4 digits")
    @ValidAtmPin
    private String atmPin;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;
}
