package com.bankcore.accounts.dto.responses;

import java.util.UUID;

public record CustomerResponse(UUID customerId, boolean exists, boolean isActive) {
}
