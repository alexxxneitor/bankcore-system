package com.bankcore.accounts.dto.responses;

import java.util.UUID;

/**
 * Response DTO for customer information.
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
public record CustomerResponse(UUID customerId, boolean exists, boolean active) {
}
