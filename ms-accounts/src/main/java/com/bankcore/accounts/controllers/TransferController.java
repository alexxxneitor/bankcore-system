package com.bankcore.accounts.controllers;

import com.bankcore.accounts.dto.requests.TransferRequest;
import com.bankcore.accounts.dto.responses.TransferResponse;
import com.bankcore.accounts.services.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST controller responsible for handling transfer operations between accounts.
 * <p>
 * This controller exposes endpoints to initiate money transfers. It delegates
 * the business logic to {@link TransactionService}.
 * </p>
 *
 * <p>
 * All requests require authentication. The authenticated user's identifier
 * is extracted from the {@link Authentication} object.
 * </p>
 *
 * @author BankCore
 * @author Sebastian Orjuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransactionService transactionService;

    /**
     * Executes a transfer from a source account to a destination account.
     * <p>
     * The method performs the following:
     * <ul>
     *   <li>Validates the incoming request body.</li>
     *   <li>Extracts the authenticated customer ID.</li>
     *   <li>Delegates the transfer operation to {@link TransactionService}.</li>
     * </ul>
     * </p>
     *
     * @param request the {@link TransferRequest} containing transfer details
     * @param auth the {@link Authentication} object containing the authenticated user
     * @return a {@link ResponseEntity} containing the {@link TransferResponse}
     */
    @PostMapping
    public ResponseEntity<TransferResponse> transfer(@RequestBody @Valid TransferRequest request, Authentication auth){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.makeTransfer(request, UUID.fromString(auth.getName())));
    }
}
