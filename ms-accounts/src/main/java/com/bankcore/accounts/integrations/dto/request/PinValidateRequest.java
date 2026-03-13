package com.bankcore.accounts.integrations.dto.request;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PinValidateRequest {
    private String pin;
}
