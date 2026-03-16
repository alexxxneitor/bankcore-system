package com.bankcore.accounts.controllers;

import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.ErrorResponse;
import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "Transactions", description = "Controller that handles requests for the management of transactions to an account")
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
    @Operation(
            summary = "Record a Deposit in an account",
            description = "The deposit is captured and processed in the respective account",
            security = @SecurityRequirement(name = "Security Token")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Data for the account deposit",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = TransactionRequest.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Deposit registered in account",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TransactionResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error - Invalid input fields, incorrect pin",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication credentials were not provided or are invalid",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "The authenticated user does not have permission to access this endpoint - Client not active in the system",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "The resource is not found registered in the system",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - Current account statement not allowed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "423",
                    description = "Temporary or Permanent Account Lock",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Unexpected response received from customer service - Validation error in system",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "The Customers service is currently unavailable.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
    })
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<TransactionResponse> deposit(
            @RequestBody @Valid TransactionRequest request,
            @Parameter(description = "Unique identifier of the account to retrieve", required = true)
            @PathVariable UUID accountId,
            Authentication auth){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.makeDeposit(request, accountId, UUID.fromString(auth.getName())));
    }
}
