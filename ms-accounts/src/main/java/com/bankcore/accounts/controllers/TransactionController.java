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

/**
 * REST controller that exposes endpoints for managing transactions
 * related to accounts.
 * <p>
 * This controller provides operations such as deposits, withdrawals,
 * and transfers. It delegates business logic to the {@link TransactionService}.
 * </p>
 *
 * <p>
 * Security:
 * <ul>
 *   <li>Requires authentication. The authenticated user's identifier
 *   is extracted from {@link Authentication#getName()} and passed to the service layer.</li>
 * </ul>
 * </p>
 *
 * @author BankcoreTeam - Sebastian Orjuela
 * @version 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Creates a deposit transaction for the specified account.
     * <p>
     * The deposit amount and optional description are provided in the
     * {@link TransactionRequest}. The account is identified by the
     * {@code accountId} path variable, and the authenticated user is
     * resolved from the {@link Authentication} context.
     * </p>
     *
     * @param request   the transaction request containing the deposit amount and description
     * @param accountId the unique identifier of the account to deposit into
     * @param auth      the authentication context, providing the user identifier
     * @return a {@link ResponseEntity} containing the {@link TransactionResponse}
     *         with details of the completed deposit
     */
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @RequestBody @Valid TransactionRequest request,
            @Parameter(description = "Unique identifier of the account to retrieve", required = true)
            @PathVariable UUID accountId,
            Authentication auth){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.makeDeposit(request, accountId, UUID.fromString(auth.getName())));
    }
}
