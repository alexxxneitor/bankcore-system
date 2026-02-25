package com.bankcore.accounts.dto.responses;

public record CustomerResponse(String customerId, boolean exists, boolean isActive) {}
