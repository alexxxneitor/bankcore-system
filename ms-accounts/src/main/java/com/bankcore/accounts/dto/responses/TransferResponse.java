package com.bankcore.accounts.dto.responses;

import com.bankcore.accounts.utils.enums.TransferStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents the response payload for a transfer operation.
 * <p>
 * This Data Transfer Object (DTO) is returned after a transfer is created,
 * processed, or queried. It provides clients with details about the transfer,
 * including identifiers, status, accounts involved, financial amounts, and
 * timestamps.
 * </p>
 *
 * @author BankcoreTeam - Sebastian Orjuela
 * @version 1.0
 */
@Value
@Builder
public class TransferResponse {

    UUID transferId;
    TransferStatus status;
    String sourceAccount;
    String destinationAccount;
    String beneficiaryName;
    BigDecimal amount;
    String description;
    BigDecimal fee;
    BigDecimal totalDebited;
    Instant timestamp;
}
