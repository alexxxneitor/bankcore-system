package com.bankcore.customers;

import com.bankcore.customers.model.UserEntity;
import com.bankcore.customers.utils.CustomerStatus;
import com.bankcore.customers.utils.UserRole;

import java.time.Instant;

public class DataProvider {

    public static final String EMAIL = "juan@test.com";
    public static final String  CUSTOMER_ROLE = "CUSTOMER";

    public static UserEntity createMockUser(){
        return UserEntity.builder()
                .dni("12345678")
                .firstName("Juan")
                .lastName("Perez")
                .email("juan@test.com")
                .password("123456")
                .atmPin("1234")
                .phone("3001234567")
                .address("Bogotá")
                .role(UserRole.CUSTOMER)
                .status(CustomerStatus.ACTIVE)
                .createdDate(Instant.now())
                .updatedDate(Instant.now())
                .build();
    }


}
