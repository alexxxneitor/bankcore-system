package com.bankcore.accounts.controllers;

import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.ErrorResponse;
import com.bankcore.accounts.services.AccountManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * account service controller
 * <p>
 *     It allows the reception of HTTP requests for the respective management of accounts according to the required service
 * </p>
 * @author Bankcore Team - Sebastian Orjuela
 * @version 1.0
 */
@RestController
@RequestMapping("/api/accounts")
@AllArgsConstructor
@Tag(name = "Accounts", description = "Controller that handles requests for the management of bank accounts")
public class AccountController {

    private final AccountManagementService accountManagementService;

    /**
     * verifies that the user is authenticated with the respective role, obtains the name of the token and the JSON
     * body of the request, and returns the corresponding response
     * @param request the {@link AccountRegisterRequest} contains the data for the creation of the account
     * @param auth The {@link Authentication} object containing the security context of the user
     * @return a {@link ResponseEntity} that contains the {@link AccountRegisterResponse} along with its Http code 201
     */
    @Operation(
            summary = "Register a new account",
            description = "Register a new account to the currently authenticated user",
            security = @SecurityRequirement(name = "Security Token")
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Account registration data",
            required = true,
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AccountRegisterRequest.class)
            )
    )
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "201",
                    description = "account registered successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AccountRegisterResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error - Invalid input fields",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),

            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - alias already registered",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Company policies - The request violates a company policy.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            ),
    })
    @PostMapping()
    public ResponseEntity<AccountRegisterResponse> registerAccount(@RequestBody @Valid AccountRegisterRequest request, Authentication auth){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountManagementService.registerAccount(request, UUID.fromString(auth.getName())));
    }
}
