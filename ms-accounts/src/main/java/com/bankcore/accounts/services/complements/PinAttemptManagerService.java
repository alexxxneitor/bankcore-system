package com.bankcore.accounts.services.complements;

import com.bankcore.accounts.exceptions.*;
import com.bankcore.accounts.integrations.dto.responses.PinValidateResponse;
import com.bankcore.accounts.models.AccountPinSecurity;
import com.bankcore.accounts.repositories.AccountPinSecurityRepository;
import com.bankcore.accounts.utils.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PinAttemptManagerService {

    private static final int TEMP_LOCK_ATTEMPTS = 4;
    private static final int PERM_LOCK_ATTEMPTS = 8;
    private static final Duration TEMP_LOCK_DURATION = Duration.ofMinutes(15);

    private final AccountPinSecurityRepository accountPinSecurityRepository;

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            readOnly = true
    )
    public void checkPinLock(UUID accountId){
        AccountPinSecurity pinSecurity = getAccountPinSecurityEntity(accountId);
        checkAndThrowIfLocked(pinSecurity);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = {
            IncorrectPinException.class,
            AccountTemporarilyLockedException.class,
            AccountPermanentlyLockedException.class
    })
    public void processPinAttempt(UUID accountId, PinValidateResponse response) {

        AccountPinSecurity pinSecurity = getAccountPinSecurityEntity(accountId);

        if (response.valid()) {
            if (!isPinAlreadyClean(pinSecurity)) {
                resetAttempts(pinSecurity);
            }
            return;
        }

        registerFailedAttempt(pinSecurity);

        checkAndThrowIfLocked(pinSecurity);

        int remaining = calculateRemainingAttempts(pinSecurity);
        throw  new IncorrectPinException(remaining);
    }

    private AccountPinSecurity getAccountPinSecurityEntity(UUID accountId) {
        return accountPinSecurityRepository
                .findByAccount_Id(accountId)
                .orElseThrow(() -> {
                    log.error("Data inconsistency: AccountPinSecurity not found for accountId={}", accountId);
                    return new CustomInternalServiceException("Security entity validation error");
                });
    }

    private boolean isPinAlreadyClean(AccountPinSecurity pinSecurity) {
        return pinSecurity.getFailedAttempts() == 0 &&
                pinSecurity.getTemporaryLockUntil() == null &&
                !pinSecurity.isPermanentLock();
    }

    private void resetAttempts(AccountPinSecurity pinSecurity) {
        pinSecurity.setFailedAttempts(0);
        pinSecurity.setTemporaryLockUntil(null);
        pinSecurity.setPermanentLock(false);
        pinSecurity.setLastFailedAttemptAt(null);
    }

    private void registerFailedAttempt(AccountPinSecurity pinSecurity) {
        int attempts = pinSecurity.getFailedAttempts() + 1;
        pinSecurity.setFailedAttempts(attempts);
        pinSecurity.setLastFailedAttemptAt(Instant.now());

        if (attempts >= PERM_LOCK_ATTEMPTS) {
            pinSecurity.getAccount().setStatus(AccountStatus.FROZEN);
            pinSecurity.setPermanentLock(true);
            log.warn("Account permanently blocked: accountId={}, status={}",
                    pinSecurity.getAccount().getId(),
                    pinSecurity.getAccount().getStatus().name());

        } else if (attempts == TEMP_LOCK_ATTEMPTS) {
            pinSecurity.setTemporaryLockUntil(Instant.now().plus(TEMP_LOCK_DURATION));
            log.warn("Account temporarily locked until {}: accountId={}",
                    pinSecurity.getTemporaryLockUntil(),
                    pinSecurity.getAccount().getId());
        }
    }

    private int calculateRemainingAttempts(AccountPinSecurity pinSecurity) {
        if (pinSecurity.getFailedAttempts() < TEMP_LOCK_ATTEMPTS) {
            return TEMP_LOCK_ATTEMPTS - pinSecurity.getFailedAttempts();
        } else {
            return PERM_LOCK_ATTEMPTS - pinSecurity.getFailedAttempts();
        }
    }

    private void checkAndThrowIfLocked(AccountPinSecurity pinSecurity) {
        if (pinSecurity.isPermanentLock()) {
            throw new AccountPermanentlyLockedException();
        }

        if (pinSecurity.getTemporaryLockUntil() != null &&
                pinSecurity.getTemporaryLockUntil().isAfter(Instant.now())) {
            throw new AccountTemporarilyLockedException(pinSecurity.getTemporaryLockUntil());
        }
    }
}
