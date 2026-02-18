package com.bankcore.customers.dto.responses;

import com.bankcore.customers.utils.CustomerStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RegisterResponses {
    private UUID id;
    private String dni;
    private String fullName;
    private String email;
    private CustomerStatus status;
    private Instant createdDate;
}
