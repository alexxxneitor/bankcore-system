package com.bankcore.customers.controllers;

import com.bankcore.customers.dto.responses.CustomerDetailsValidateResponse;
import com.bankcore.customers.dto.responses.CustomerValidateResponse;
import com.bankcore.customers.dto.responses.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bankcore.customers.dto.responses.UserProfileResponse;
import com.bankcore.customers.services.UserManagement;

import java.util.UUID;

/**
 * REST controller for managing customer profile operations.
 * <p>
 * This controller provides endpoints that allow authenticated customers
 * to retrieve their own profile information from the BankCore system.
 * It integrates with Spring Security to ensure only authorized users
 * with the CUSTOMER role can access the data.
 * </p>
 *
 * @author BankCore Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Profile", description = "Endpoints for view profiles")
public class ProfileController {

    /**
     * Service layer dependency for user management and business logic.
     */
    private final UserManagement userManagement;

    /**
     * Retrieves the profile information for the currently authenticated customer.
     * <p>
     * This method extracts the username from the {@link Authentication} object
     * and delegates the retrieval to the {@link UserManagement} service.
     * It is restricted to fully authenticated users with the 'CUSTOMER' role.
     * </p>
     *
     * @param auth The {@link Authentication} object containing the security context of the user.
     * @return A {@link ResponseEntity} containing the {@link UserProfileResponse} and HTTP status 200.
     * @see UserManagement#getCurrentUserProfile(String)
     */
    @GetMapping("/me")
    @PreAuthorize("isFullyAuthenticated() && hasRole(T(com.bankcore.customers.utils.enums.UserRole).CUSTOMER.name())")
    @Operation(
            summary = "View Profile",
            description = "Returns the profile of the authenticated CUSTOMER",
            security = @SecurityRequirement(name = "Security Token"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserProfileResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - Invalid or missing JWT",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - User does not have CUSTOMER role",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Authenticated user not found in database",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<UserProfileResponse> me(Authentication auth) {
        return ResponseEntity.status(HttpStatus.OK).body(userManagement.getCurrentUserProfile(auth.getName()));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDetailsValidateResponse> getCustomerDetailsById(@PathVariable UUID customerId){
        return ResponseEntity.status(HttpStatus.OK).body(userManagement.getDetailsCustomer(customerId));
    }

    @GetMapping("/{customerId}/validate")
    public ResponseEntity<CustomerValidateResponse> getCustomerValidateById(@PathVariable UUID customerId){
        return ResponseEntity.status(HttpStatus.OK).body(userManagement.getCustomerIsActive(customerId));
    }
}
