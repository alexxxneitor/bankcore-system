package com.bankcore.customers.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * DTO class with response schema for the internal service query,
 * validation of active and existing client in the system
 * @author Bankcore Team - Sebastian Orjuela
 * @version 1.0
 */

@Builder
@Getter
@AllArgsConstructor
public class CustomerValidateResponse {

    private UUID customerId;
    private boolean exist;
    private boolean active;
}
