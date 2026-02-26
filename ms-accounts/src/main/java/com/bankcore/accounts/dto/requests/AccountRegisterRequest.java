package com.bankcore.accounts.dto.requests;

import com.bankcore.accounts.utils.enums.AccountType;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountRegisterRequest {

    @NotBlank(message = "Customer ID is required")
    private AccountType accountType;

    @NotBlank(message = "Customer ID is required")
    private String currency;

    @NotBlank(message = "Customer ID is required")
    private String alias;
}
