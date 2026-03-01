package com.bankcore.customers.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class CustomerValidateResponse {

    private UUID customerId;
    private boolean exist;
    private boolean isActive;
}
