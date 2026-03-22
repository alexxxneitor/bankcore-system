package com.bankcore.accounts.models;

import com.bankcore.accounts.utils.uuidConfig.UUIDv7;
import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Represents a financial transaction associated with an {@link AccountEntity}.
 * <p>
 * This entity is immutable and mapped to the {@code transactions} table.
 * Each transaction is uniquely identified by a generated {@link UUID} and
 * a derived {@code referenceNumber}. The entity captures essential details
 * such as type, amount, balance after the transaction, concept, counterparty
 * information, and status.
 * </p>
 *
 * <p>
 * Constraints and indexes:
 * <ul>
 *   <li>Unique constraint on {@code referenceNumber}.</li>
 *   <li>Index on {@code account_id} for efficient lookups by account.</li>
 *   <li>Index on {@code referenceNumber} for quick retrieval by reference.</li>
 *   <li>Composite index on {@code account_id, createdAt DESC} for chronological queries.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Lifecycle:
 * <ul>
 *   <li>On persist, {@code createdAt} is set to the current timestamp.</li>
 *   <li>A {@code referenceNumber} is generated based on the transaction {@link UUID}.</li>
 * </ul>
 * </p>
 *
 * @author Bankcore Team - Sebastian Orjuela
 * @version 1.0
 */

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Immutable
@Entity
@Table(
        name = "transactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_reference_number",
                        columnNames = "referenceNumber"
                )
        },
        indexes = {
                @Index(name = "idx_transaction_account", columnList = "account_id"),
                @Index(name = "idx_transaction_account_created", columnList = "account_id, created_at DESC")
        }
)
public class TransactionEntity {

    @Id
    @GeneratedValue(generator = "uuid7")
    @UUIDv7
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private AccountEntity account;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2, updatable = false)
    private BigDecimal balanceAfter;

    @Column(updatable = false)
    private String description;

    @Column(updatable = false)
    private String counterpartyAccountNumber;

    @Column(updatable = false)
    private String counterpartyName;

    @Column(nullable = false, updatable = false)
    private String referenceNumber;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    /**
     * Generates a unique reference number for the transaction.
     * <p>
     * The reference number is composed of the prefix {@code TXN}
     * followed by the first 16 characters of the transaction {@link UUID},
     * formatted in uppercase.
     * </p>
     */
    protected void generateReferenceNumber() {
        if (this.id != null) {
            String uuidPart = this.id.toString()
                    .replace("-", "")
                    .substring(0, 16)
                    .toUpperCase();
            this.referenceNumber = String.join("","TXN", uuidPart);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        generateReferenceNumber();
    }
}
