package com.bankcore.accounts.dto.requests;

import com.bankcore.accounts.utils.validators.iban.ValidIban;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Represents a request to initiate a transfer operation.
 * <p>
 * This Data Transfer Object (DTO) is used to capture the necessary
 * information for creating a new transfer, including source account,
 * destination IBAN, amount, and optional description.
 * </p>
 *
 * <h2>Validation</h2>
 * <ul>
 *   <li>Ensures required fields are present using Bean Validation annotations.</li>
 *   <li>Validates destination IBAN format via {@link ValidIban}.</li>
 *   <li>Enforces minimum amount constraints.</li>
 * </ul>
 *
 * @author BankcoreTeam - Sebastian Orjuela
 */
@Data
@Builder
public class TransferRequest {

    @NotNull(message = "Source account ID is required")
    private UUID sourceAccountId;

    @NotNull(message = "The IBAN of the destination account is required")
    @ValidIban(message = "The destination IBAN is not valid")
    private String destinationAccountNumber;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.0", message = "Amount must be at least 1.0")
    private BigDecimal amount;

    private String description;
}
