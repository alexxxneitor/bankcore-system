package com.bankcore.accounts.models;

import com.bankcore.accounts.utils.uuidConfig.UUIDv7;
import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private String concept;

    @Column
    private String counterpartyAccountNumber;

    @Column
    private String counterpartyName;

    @Column(nullable = false)
    private String referenceNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
