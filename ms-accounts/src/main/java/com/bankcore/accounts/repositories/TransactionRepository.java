package com.bankcore.accounts.repositories;

import com.bankcore.accounts.models.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link TransactionEntity} persistence operations.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations and
 * exposes custom query methods for transaction-specific lookups.
 * </p>
 * @author BankCore Team - Cristian Ortiz
 */
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    /**
     * Retrieves the most recent transaction associated with a given account.
     * <p>
     * Results are ordered by {@code createdAt} in descending order,
     * returning only the latest record.
     * </p>
     *
     * @param accountId the UUID of the account whose latest transaction is requested
     * @return an {@link Optional} containing the latest {@link TransactionEntity},
     *         or empty if no transactions exist for the given account
     */
    Optional<TransactionEntity> findTopByAccount_IdOrderByCreatedAtDesc(UUID accountId);
}