package com.bankcore.customers.service;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponses;
import com.bankcore.customers.exception.ResourceConflictException;
import com.bankcore.customers.model.UserEntity;
import com.bankcore.customers.repository.UserRepository;
import com.bankcore.customers.utils.CustomerStatus;
import com.bankcore.customers.utils.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

public class UserManagementImpl implements UserManagement{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserManagementImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponses registerCustomer(RegisterRequest request) {
        if (userRepository.existsByDni(request.getDni())){
            throw new ResourceConflictException("DNI already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())){
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
        return RegisterResponses.builder()
                .id(userEntity.getId())
                .dni(userEntity.getDni())
                .fullName(userEntity.getFirstName() + " " + userEntity.getLastName())
                .email(userEntity.getEmail())
                .status(userEntity.getStatus())
                .createdDate(userEntity.getCreatedDate())
                .build();
    }
}
