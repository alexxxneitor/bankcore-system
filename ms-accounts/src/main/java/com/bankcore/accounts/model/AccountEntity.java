package com.bankcore.accounts.model;

import com.bankcore.accounts.utils.AccountStatus;
import com.bankcore.accounts.utils.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Column(nullable = false)
    private String currency;

    @Digits(integer = 15, fraction = 4)
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String alias;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Digits(integer = 15, fraction = 4)
    @Column(precision = 19, scale = 4, nullable = false)
    private BigDecimal dailyWithdrawalLimit;

    /**
     * Timestamp indicating when the entity was created.
     * <p>
     * This field is automatically populated before persistence
     * and is not updatable.
     * </p>
     */
    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    /**
     * Timestamp indicating the last update time of the entity.
     * <p>
     * This field is automatically updated before entity updates.
     * </p>
     */
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    /**
     * Lifecycle callback executed before the entity is persisted.
     * Initializes creation and update timestamps.
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Lifecycle callback executed before the entity is updated.
     * Updates the modification timestamp.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
