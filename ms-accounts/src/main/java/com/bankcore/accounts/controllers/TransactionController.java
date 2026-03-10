package com.bankcore.accounts.controllers;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.services.TransactionService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @RequestBody @Valid TransactionRequest request,
            @Parameter(description = "Unique identifier of the account to retrieve", required = true)
            @PathVariable UUID accountId,
            Authentication auth){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.makeDeposit(request, accountId, UUID.fromString(auth.getName())));
    }
}
