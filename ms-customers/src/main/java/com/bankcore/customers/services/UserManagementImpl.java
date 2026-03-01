package com.bankcore.customers.services;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.CustomerDetailsValidateResponse;
import com.bankcore.customers.dto.responses.RegisterResponse;
import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.exceptions.ResourceConflictException;
import com.bankcore.customers.exceptions.UserProfileNotFoundException;
import com.bankcore.customers.model.UserEntity;
import com.bankcore.customers.repository.UserRepository;
import com.bankcore.customers.utils.enums.CustomerStatus;
import com.bankcore.customers.utils.enums.UserRole;
import com.bankcore.customers.utils.mappers.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Default implementation of {@link UserManagement}.
 * <p>
 * This service coordinates the user registration process by:
 * <ul>
 *     <li>Validating uniqueness constraints (DNI and email)</li>
 *     <li>Encrypting sensitive information (password and ATM PIN)</li>
 *     <li>Persisting the user entity</li>
 *     <li>Mapping the persisted entity to a response DTO</li>
 * </ul>
 * </p>
 *
 * <p>
 * Business rules and security requirements are enforced at this layer
 * before interacting with the persistence layer.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserManagementImpl implements UserManagement {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /**
     * Registers a new customer in the system.
     * <p>
     * This method performs the following steps:
     * <ol>
     *     <li>Validates that DNI and email are unique</li>
     *     <li>Hashes password and ATM PIN using {@link PasswordEncoder}</li>
     *     <li>Assigns default role and status</li>
     *     <li>Persists the entity</li>
     *     <li>Returns a non-sensitive response DTO</li>
     * </ol>
     * </p>
     *
     * @param request the registration request containing user data
     * @return a {@link RegisterResponse} containing non-sensitive user information
     * @throws ResourceConflictException if DNI or email already exists
     */
    @Override
    public RegisterResponse registerCustomer(RegisterRequest request) {

        if (userRepository.existsByDni(request.getDni())) {
            throw new ResourceConflictException("DNI already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException("Email already exists");
        }

        UserEntity userEntity =
                UserEntity.builder()
                        .dni(request.getDni())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .atmPin(passwordEncoder.encode(request.getAtmPin()))
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .role(UserRole.CUSTOMER)
                        .status(CustomerStatus.ACTIVE)
                        .build();

        userRepository.save(userEntity);

        return userMapper.toRegisterResponse(userEntity);
    }

    /**
     * Retrieves the profile data for a specific user based on their id.
     * <p>
     * This method performs a read-only transaction to fetch the user entity.
     * It validates the input and ensures that if the user does not exist in the
     * persistence layer, a specific business exception is thrown.
     * </p>
     *
     * @param id The id of the authenticated user to retrieve.
     * @return A {@link UserProfileResponse} containing the mapped profile data.
     * @throws IllegalArgumentException      If the provided email is null or empty.
     * @throws UserProfileNotFoundException If no user is found with the given email.
     * @see UserEntity
     * @see UserProfileResponse
     */
    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id must not be null or blank");
        }

        UserEntity user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() ->
                        new UserProfileNotFoundException("Authenticated user not found: " + id));


        return userMapper.toUserProfileResponse(user);
    }

    /**
     * Retrieves detailed information about a customer based on their unique identifier.
     *
     * <p>This method queries the {@link UserRepository} to find a {@link UserEntity}
     * associated with the provided customer ID. If no entity is found, a
     * {@link UserProfileNotFoundException} is thrown. The retrieved entity is then
     * mapped into a {@link CustomerDetailsValidateResponse} using the {@link UserMapper}.
     *
     * @param customerId the unique identifier of the customer; must not be null
     * @return a {@link CustomerDetailsValidateResponse} containing the validated customer details
     * @throws UserProfileNotFoundException if no customer exists with the provided ID
     * @see UserEntity
     * @see CustomerDetailsValidateResponse
     * @see UserRepository
     * @see UserMapper
     */

    @Override
    @Transactional(readOnly = true)
    public CustomerDetailsValidateResponse getDetailsCustomer(UUID customerId) {
        UserEntity customer = userRepository.findById(customerId)
                .orElseThrow(() -> {
                    return new UserProfileNotFoundException("There is no client with the provided ID");
                });

        return userMapper.toCustomerDetailsValidateResponse(customer);
    }
}
