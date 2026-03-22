package com.bankcore.accounts.services.processors;

import com.bankcore.accounts.dto.requests.TransactionQueryParams;
import com.bankcore.accounts.dto.responses.TransactionHistoryResponse;
import com.bankcore.accounts.dto.responses.TransactionsHistoryResponse;
import com.bankcore.accounts.exceptions.AccountNotFoundException;
import com.bankcore.accounts.exceptions.NoTransactionHistoryException;
import com.bankcore.accounts.models.TransactionEntity;
import com.bankcore.accounts.repositories.AccountRepository;
import com.bankcore.accounts.repositories.TransactionRepository;
import com.bankcore.accounts.utils.enums.TransactionType;
import com.bankcore.accounts.utils.mappers.TransactionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * {@code TransactionHistoryProcessor} is a Spring service component
 * responsible for processing transaction history queries.
 *
 * <p>This service orchestrates the retrieval of transaction data from
 * the repository, applies filters based on query parameters, and maps
 * entities into API response DTOs.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Parse and validate query parameters such as date range and type.</li>
 *   <li>Delegate data retrieval to {@link TransactionRepository}.</li>
 *   <li>Apply pagination using {@link org.springframework.data.domain.Pageable}.</li>
 *   <li>Map {@link TransactionEntity} objects into {@link TransactionHistoryResponse} DTOs
 *       via {@link TransactionMapper}.</li>
 *   <li>Build a {@link TransactionsHistoryResponse} containing content and pagination metadata.</li>
 * </ul>
 *
 * <p>This service ensures that transaction history queries are handled
 * consistently, with proper validation, filtering, and mapping.</p>
 *
 * @author BankcoreTeam
 * @author Sebastian Orjuela
 * @version 1.0
 * @see TransactionRepository
 * @see AccountRepository
 * @see TransactionMapper
 * @see TransactionsHistoryResponse
 * @see TransactionHistoryResponse
 * @see TransactionQueryParams
 */
@Service
@RequiredArgsConstructor
public class TransactionHistoryProcessor {

    private final TransactionRepository repository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;

    /**
     * Retrieves a paginated transaction history for the given account,
     * applying optional filters such as type and date range.
     *
     * @param accountId the unique identifier of the account
     * @param params the query parameters including pagination, type, and date filters
     * @return a {@link TransactionsHistoryResponse} containing transaction history and pagination metadata
     * @throws AccountNotFoundException if the account does not exist
     * @throws NoTransactionHistoryException if no transaction history is found for the account
     */
    @Transactional(readOnly = true)
    public TransactionsHistoryResponse getTransactions(UUID accountId, TransactionQueryParams params) {

        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException();
        }

        Instant from = params.getFromDate() != null ? Instant.parse(params.getFromDate()) : null;
        Instant to = params.getToDate() != null ? Instant.parse(params.getToDate()) : null;

        TransactionType type = null;
        if (params.getType() != null) {
            type = TransactionType.valueOf(params.getType().toUpperCase());
        }

        Pageable pageable = PageRequest.of(params.getPage(), params.getSize());

        Page<TransactionEntity> pageResult = repository.findByAccountAndFilters(
                accountId,
                type,
                from,
                to,
                pageable
        );

        if (pageResult.isEmpty()) {
            throw new NoTransactionHistoryException("There is no transaction history associated with this account");
        }

        List<TransactionHistoryResponse> content = pageResult.stream()
                .map(transactionMapper::toTransactionHistory)
                .toList();

        return TransactionsHistoryResponse.builder()
                .content(content)
                .page(params.getPage())
                .size(params.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();
    }
}