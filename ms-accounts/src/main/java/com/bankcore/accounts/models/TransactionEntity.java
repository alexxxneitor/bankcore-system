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

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Immutable
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
                @Index(name = "idx_transaction_reference", columnList = "referenceNumber"),
                @Index(name = "idx_transaction_account_created", columnList = "account_id, createdAt DESC")
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

    @Column(nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4, updatable = false)
    private BigDecimal balanceAfter;

    @Column(nullable = false, updatable = false)
    private String concept;

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
