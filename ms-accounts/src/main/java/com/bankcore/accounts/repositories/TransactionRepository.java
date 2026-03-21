package com.bankcore.accounts.repositories;

import com.bankcore.accounts.models.TransactionEntity;
import com.bankcore.accounts.utils.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
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

    /**
     * Retrieves a paginated list of {@link TransactionEntity} objects
     * filtered by account ID and optional parameters such as type,
     * date range, and pagination settings.
     *
     * <p>This query supports flexible filtering:</p>
     * <ul>
     *   <li>Always filters by {@code accountId}.</li>
     *   <li>If {@code type} is provided, filters by transaction type.</li>
     *   <li>If {@code fromDate} is provided, includes only transactions
     *       with {@code timestamp} greater than or equal to {@code fromDate}.</li>
     *   <li>If {@code toDate} is provided, includes only transactions
     *       with {@code timestamp} less than or equal to {@code toDate}.</li>
     *   <li>Results are ordered by {@code timestamp} in descending order.</li>
     * </ul>
     *
     * <p>Responsibilities:</p>
     * <ul>
     *   <li>Provide a flexible query for transaction history retrieval.</li>
     *   <li>Support pagination via {@link Pageable}.</li>
     *   <li>Return results as a {@link Page} of {@link TransactionEntity}.</li>
     * </ul>
     *
     * @param accountId the unique identifier of the account
     * @param type optional transaction type filter (nullable)
     * @param fromDate optional start date filter (nullable)
     * @param toDate optional end date filter (nullable)
     * @param pageable pagination information (page number, size, sorting)
     * @return a paginated list of transactions matching the filters
     */
    @Query("""
        SELECT t FROM Transaction t
        WHERE t.account.id = :accountId
          AND (:type IS NULL OR t.type = :type)
          AND (:fromDate IS NULL OR t.timestamp >= :fromDate)
          AND (:toDate IS NULL OR t.timestamp <= :toDate)
        ORDER BY t.timestamp DESC
        """)
    Page<TransactionEntity> findByAccountAndFilters(
            @Param("accountId") UUID accountId,
            @Param("type") TransactionType type,
            @Param("fromDate") Instant fromDate,
            @Param("toDate") Instant toDate,
            Pageable pageable
    );
}