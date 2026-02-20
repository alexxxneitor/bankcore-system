package com.bankcore.customers.service;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponse;
import com.bankcore.customers.exception.ResourceConflictException;

/**
 * Application service interface responsible for managing user-related
 * business operations.
 * <p>
 * This interface defines the contract for user management workflows,
 * such as registration, without exposing implementation details.
 * Implementations are responsible for enforcing business rules,
 * coordinating persistence, and handling security requirements.
 * </p>
 */
public interface UserManagement {

    /**
     * Registers a new user in the system.
     * <p>
     * This operation validates business constraints (e.g., unique DNI and email),
     * encrypts sensitive information such as password and ATM PIN,
     * persists the user entity, and returns a response DTO.
     * </p>
     *
     * @param request the registration request containing user personal
     *                and authentication data
     * @return a {@link RegisterResponse} containing non-sensitive
     *         information of the registered user
     * @throws ResourceConflictException if a user with the same DNI
     *         or email already exists
     * @throws IllegalArgumentException if the request data violates
     *         business rules
     */
    RegisterResponse registerCustomer(RegisterRequest request);
}
