package com.bankcore.customers.controller;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponse;
import com.bankcore.customers.service.UserManagement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller responsible for handling authentication-related operations.
 * <p>
 * This controller exposes endpoints under {@code /api/auth} for user registration
 * and other authentication workflows.
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserManagement userManagement;

    /**
     * Constructs a new {@code UserController} with the required user management service.
     *
     * @param userManagement the service responsible for handling user registration logic
     */
    public UserController(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    /**
     * Handles user registration requests.
     * <p>
     * Validates the incoming request payload and delegates the registration
     * process to the application service layer.
     * </p>
     *
     * @param request the registration request containing user credentials and personal data
     * @return a {@link ResponseEntity} containing the registration response
     *         with HTTP status {@code 201 Created}
     * @throws org.springframework.web.bind.MethodArgumentNotValidException
     *         if the request validation fails
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagement.registerCustomer(request));
    }
}
