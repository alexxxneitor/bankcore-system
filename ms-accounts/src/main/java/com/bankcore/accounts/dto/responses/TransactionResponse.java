package com.bankcore.accounts.dto.responses;

import com.bankcore.accounts.utils.enums.TransactionType;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Data Transfer Object (DTO) representing the response details of a financial transaction.
 * <p>
 * This class is immutable and built using Lombok's {@link Builder} and {@link Value} annotations.
 * It provides a structured view of transaction information for API clients, ensuring
 * clarity and consistency in responses.
 * </p>
 *
 * <p>
 * Fields:
 * <ul>
 *   <li>{@code referenceNumber} – Unique identifier assigned to the transaction.</li>
 *   <li>{@code type} – Type of transaction (e.g., DEPOSIT, WITHDRAWAL).</li>
 *   <li>{@code amount} – Monetary value of the transaction.</li>
 *   <li>{@code balanceBefore} – Account balance before the transaction was applied.</li>
 *   <li>{@code balanceAfter} – Account balance immediately after the transaction.</li>
 *   <li>{@code description} – Human-readable description or concept of the transaction.</li>
 *   <li>{@code timestamp} – Exact time when the transaction occurred.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Returned by service or controller layers to expose transaction details to clients.</li>
 *   <li>Ensures immutability and thread-safety for response objects.</li>
 *   <li>Can be easily serialized to JSON for REST APIs.</li>
 * </ul>
 * </p>
 *
 * @author BankcoreTeam - Sebastian Orjuela
 * @version 1.0
 */
@Value
@Builder
public class TransactionResponse {

    String referenceNumber;
    TransactionType type;
    BigDecimal amount;
    BigDecimal balanceBefore;
    BigDecimal balanceAfter;
    String description;
    Instant timestamp;
}
