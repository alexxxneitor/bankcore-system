package com.bankcore.customers.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.service.UserManagement;


@RestController
@RequestMapping("/api/customers")
public class ProfileController {
    
    private final UserManagement userManagement;
    
    public ProfileController(UserManagement userManagement) {
        this.userManagement = userManagement;
    }


    @GetMapping("/me")
    // TODO: reemplazar cuando HU-02 esté lista
    @PreAuthorize("isFullyAuthenticated() && hasRole(T(com.bankcore.customers.utils.UserRole).CUSTOMER.name())")
    public ResponseEntity<UserProfileResponse> me(Authentication auth){
        return ResponseEntity.status(HttpStatus.OK).body(userManagement.getCurrentUserProfile(auth.getName()));
    }


}
