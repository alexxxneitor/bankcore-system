package com.bankcore.customers.dto.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {

    private String id;
    private String dni;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String status;
    private String createdAt;

}
