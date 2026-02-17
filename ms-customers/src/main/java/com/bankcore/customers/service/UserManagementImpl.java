package com.bankcore.customers.service;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.dto.responses.RegisterResponses;
import com.bankcore.customers.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserManagementImpl implements UserManagement{

    private final UserRepository userRepository;

    public UserManagementImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public RegisterResponses registerCustomer(RegisterRequest request) {
        if (userRepository.existsByDni(request.getDni())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "DNI already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        return null;
    }
}
