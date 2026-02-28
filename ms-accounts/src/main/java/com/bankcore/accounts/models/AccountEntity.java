package com.bankcore.accounts.models;

import com.bankcore.accounts.utils.enums.AccountStatus;
import com.bankcore.accounts.utils.enums.AccountType;
import com.bankcore.accounts.utils.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
public class AccountEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(unique = true, nullable = false, length = 24)
    private String accountNumber;

    @Column(nullable = false)
    private UUID customerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrencyCode currency;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    @Column(nullable = false)
    private BigDecimal dailyWithdrawalLimit;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant createdAt;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
