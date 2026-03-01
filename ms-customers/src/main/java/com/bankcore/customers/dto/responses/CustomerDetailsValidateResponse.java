package com.bankcore.customers.dto.responses;

import com.bankcore.customers.utils.enums.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * DTO response for the service to consult details of a specific client
 * @author Bankcore Team - Sebastian Orjuea
 * @version 1.0
 */
@Builder
@Getter
@AllArgsConstructor
public class CustomerDetailsValidateResponse {

    private UUID id;
    private String dni;
    private String fullName;
    private String email;
    private CustomerStatus status;
}
