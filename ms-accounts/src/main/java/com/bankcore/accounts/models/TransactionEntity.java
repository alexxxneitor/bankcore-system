package com.bankcore.accounts.models;

import com.bankcore.accounts.utils.enums.TransactionStatus;
import com.bankcore.accounts.utils.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a financial transaction associated with a bank account.
 * <p>
 * Each transaction records a monetary movement and captures the resulting balance,
 * counterparty details, and lifecycle timestamps.
 * </p>
 *
 * @author BankCore Team - Cristian Ortiz
 * @version 1.0
 */
@Entity
@Table(name = "transactions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private String concept;

    @Column(length = 34)
    private String counterpartyAccountNumber;

    @Column(length = 100)
    private String counterpartyName;

    @Column(unique = true, length = 36)
    private String referenceNumber;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(updatable = false)
    private LocalDate scheduledDate;

    @Column(updatable = false)
    private Instant executedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    private void prePersist() {
        this.createdAt = Instant.now();
    }

    public UUID getAccountId() {
        return account.getId();
    }

}
