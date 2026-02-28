package com.bankcore.customers.service;

import com.bankcore.customers.dto.responses.LoginResponse;
import com.bankcore.customers.dto.requests.LoginRequest;
import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponse;
import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.exception.ResourceConflictException;
import com.bankcore.customers.exception.UserProfileNotFoundException;

/**
 * Application service interface responsible for managing user-related
 * business operations.
 * <p>
 * This interface defines the contract for user management workflows,
 * such as registration, without exposing implementation details.
 * Implementations are responsible for enforcing business rules,
 * coordinating persistence, and handling security requirements.
 * </p>
 * @author BankCore Team - Sebastian Orjuela - Cristian Ortiz
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

    /**
     * Authenticates a user and returns a session token.
     * <p>
     * This method validates the provided credentials and, upon success,
     * issues a JSON Web Token (JWT) for subsequent authenticated requests.
     *
     * @param request the {@link LoginRequest} containing the user's credentials
     * @return a {@link LoginResponse} containing the generated access token and metadata
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException if the account associated with the email does not exist
     * @throws org.springframework.security.core.AuthenticationException if the credentials
     * are invalid or the account is disabled/locked
     */
    LoginResponse login(LoginRequest request);

    /**
     * Retrieves the profile information for a user identified by their email.
     * <p>
     * This method serves as the main entry point for obtaining customer profile details.
     * Implementations are expected to handle validation and ensure the returned
     * data is properly mapped to a response DTO.
     * </p>
     *
     * @param id The unique id of the user.
     * @return A {@link UserProfileResponse} object containing the user's profile details.
     * @throws UserProfileNotFoundException If no user exists with the specified id.
     * @throws IllegalArgumentException     If the id parameter is invalid (null or blank).
     */
    UserProfileResponse getCurrentUserProfile(String id);
}
