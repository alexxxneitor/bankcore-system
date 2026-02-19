package com.bankcore.customers.service;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponses;
import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.exception.ResourceConflictException;
import com.bankcore.customers.exception.UserProfileNotFoundException;
import com.bankcore.customers.model.UserEntity;
import com.bankcore.customers.repository.UserRepository;
import com.bankcore.customers.utils.CustomerStatus;
import com.bankcore.customers.utils.UserRole;
import com.bankcore.customers.utils.mappers.UserMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserManagementImpl implements UserManagement{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserManagementImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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
        return userMapper.toRegisterResponse(userEntity);
    }

    @Override
    public UserProfileResponse getCurrentUserProfile() {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        // TODO: Comprobar funcionamiento cuando HU-02 esté lista
        UserEntity user = userRepository.findByEmail(auth.getName()).orElseThrow(
        () -> new UserProfileNotFoundException("Authenticated user not found"));

        return userMapper.toUserProfileResponse(user);

    }
}
