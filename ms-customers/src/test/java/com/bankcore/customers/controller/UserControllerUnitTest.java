package com.bankcore.customers.controller;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponse;
import com.bankcore.customers.service.UserManagement;
import com.bankcore.customers.utils.CustomerStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerUnitTest {

    @Mock private UserManagement userManagement;

    @InjectMocks private UserController userController;

    @Test
    void register_Customer_Success(){
        RegisterRequest request =
                RegisterRequest.builder()
                        .dni("1234567890")
                        .firstName("John")
                        .lastName("Doe")
                        .email("johndoe@email.com")
                        .password("Password123!")
                        .atmPin("1234")
                        .phone("+573001234567")
                        .address("123 Main St")
                        .build();

        RegisterResponse expectedResponse =
                RegisterResponse.builder()
                        .id(java.util.UUID.randomUUID())
                        .dni(request.getDni())
                        .fullName(request.getFirstName() + " " + request.getLastName())
                        .email(request.getEmail())
                        .status(CustomerStatus.ACTIVE)
                        .createdDate(java.time.Instant.now())
                        .build();
        when(userManagement.registerCustomer(request)).thenReturn(expectedResponse);
        ResponseEntity<RegisterResponse> response = userController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(userManagement, times(1)).registerCustomer(request);
    }
}
