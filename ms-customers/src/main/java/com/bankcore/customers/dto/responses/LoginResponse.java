package com.bankcore.customers.dto.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private String customerId;

}
