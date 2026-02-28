package com.bankcore.accounts.controllers;

import com.bankcore.accounts.dto.requests.AccountRegisterRequest;
import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.service.AccountManagementService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class AccountController {

    private final AccountManagementService accountManagementService;

    /**
     * verifies that the user is authenticated with the respective role, obtains the name of the token and the JSON
     * body of the request, and returns the corresponding response
     * @param request the {@link AccountRegisterRequest} contains the data for the creation of the account
     * @param auth The {@link Authentication} object containing the security context of the user
     * @return a {@link ResponseEntity} that contains the {@link AccountRegisterResponse} along with its Http code 201
     */
    @PostMapping()
    @PreAuthorize("isFullyAuthenticated() && hasRole('CUSTOMER')")
    public ResponseEntity<AccountRegisterResponse> registerAccount(@RequestBody @Valid AccountRegisterRequest request, Authentication auth){
        return ResponseEntity.status(HttpStatus.CREATED).body(accountManagementService.registerAccount(request, UUID.fromString(auth.getName())));
    }
}
