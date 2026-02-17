package com.bankcore.customers.controller;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponses;
import com.bankcore.customers.service.UserManagement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserManagement userManagement;

    public UserController(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponses> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagement.registerCustomer(request));
    }
}
