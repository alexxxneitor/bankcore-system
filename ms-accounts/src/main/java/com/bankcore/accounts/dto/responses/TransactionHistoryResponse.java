package com.bankcore.accounts.dto.responses;

import com.bankcore.accounts.utils.enums.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * {@code TransactionHistoryResponse} is an immutable Data Transfer Object (DTO)
 * that represents the details of a single transaction in a user's history.
 *
 * <p>This class is typically used as an API response model to provide
 * clients with structured information about transactions, including
 * type, amount, balance, and descriptive details.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Expose transaction attributes in a read-only format.</li>
 *   <li>Provide contextual information such as description and timestamp.</li>
 *   <li>Ensure immutability through Lombok's {@link lombok.Value} annotation.</li>
 *   <li>Support builder pattern via {@link lombok.Builder} for easy instantiation.</li>
 * </ul>
 *
 * <p>This object is returned by APIs to provide clients with
 * transaction history details in a consistent and immutable format.</p>
 *
 * @author BankcoreTeam
 * @author Sebastian Orjuela
 * @version 1.0
 * @see TransactionType
 */
@Value
@Builder
public class TransactionHistoryResponse {

    UUID id;
    TransactionType type;
    BigDecimal amount;
    BigDecimal balance;
    String description;
    Instant timestamp;
}
