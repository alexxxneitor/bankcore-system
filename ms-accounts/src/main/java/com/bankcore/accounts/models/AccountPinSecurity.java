package com.bankcore.accounts.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(
        name = "account_pin_security",
        indexes = {
                @Index(name = "idx_account_pin_security_account", columnList = "account_id")
        })
public class AccountPinSecurity {

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column
    @Builder.Default
    private int failedAttempts = 0;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Builder.Default
    private Instant temporaryLockUntil = null;

    @Column
    @Builder.Default
    private boolean permanentLock = false;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @Builder.Default
    private Instant lastFailedAttemptAt = null;
}
