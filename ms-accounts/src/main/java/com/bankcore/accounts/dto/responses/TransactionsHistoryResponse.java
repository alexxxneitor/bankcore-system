package com.bankcore.accounts.dto.responses;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * {@code TransactionsHistoryResponse} is an immutable Data Transfer Object (DTO)
 * that represents a paginated list of transaction history items.
 *
 * <p>This class is typically used as an API response model to provide clients
 * with structured transaction history data, including pagination metadata
 * such as page number and page size.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Expose a list of {@link TransactionHistoryItem} objects.</li>
 *   <li>Provide pagination details ({@code page}, {@code size}).</li>
 *   <li>Ensure immutability through Lombok's {@link lombok.Value} annotation.</li>
 *   <li>Support builder pattern via {@link lombok.Builder} for easy instantiation.</li>
 * </ul>
 *
 * <p>This object is returned by APIs to provide clients with a paginated
 * transaction history, supporting heterogeneous transaction types
 * via {@link TransactionHistoryItem}.</p>
 *
 * @author BankcoreTeam
 * @author Sebastian Orjuela
 * @version 1.0
 * @see TransactionHistoryItem
 * @see TransferHistoryResponse
 * @see TransactionHistoryResponse
 */
@Value
@Builder
public class TransactionsHistoryResponse {

    List<TransactionHistoryItem> content;
    int page;
    int size;
    int totalElements;
    int totalPages;
}
