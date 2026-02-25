package com.bankcore.accounts.controller;

import com.bankcore.accounts.dto.responses.UserAccountResponse;
import com.bankcore.accounts.service.AccountManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountManagementService accountsService;

    @GetMapping
    @PreAuthorize("isFullyAuthenticated() && hasRole('CUSTOMER')")
    public ResponseEntity<List<UserAccountResponse>> getCustomerAccounts(Authentication auth){
        return ResponseEntity.status(HttpStatus.OK).body(accountsService.getCurrentUserAccounts(auth.getName()));

    }
}
