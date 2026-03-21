package com.bankcore.accounts.dto.responses;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * {@code TransactionHistoryItem} is a polymorphic interface that represents
 * a generic item in a user's transaction history.
 *
 * <p>This interface is designed to support multiple transaction categories
 * through JSON polymorphic serialization and deserialization. The specific
 * subtype is determined by the {@code transactionCategory} property in the
 * JSON payload.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Serve as a common contract for transaction history responses.</li>
 *   <li>Enable polymorphic handling of different transaction types.</li>
 *   <li>Provide flexibility for extending transaction categories in the future.</li>
 * </ul>
 *
 * <p>Serialization Details:</p>
 * <ul>
 *   <li>Uses {@link com.fasterxml.jackson.annotation.JsonTypeInfo} to include
 *       the {@code transactionCategory} property in JSON.</li>
 *   <li>Maps subtypes via {@link com.fasterxml.jackson.annotation.JsonSubTypes}:</li>
 *   <ul>
 *     <li>{@code TRANSFER} → {@link TransferHistoryResponse}</li>
 *     <li>{@code BASIC} → {@link TransactionHistoryResponse}</li>
 *   </ul>
 * </ul>
 *
 * <p>This interface allows APIs to return heterogeneous transaction history
 * items while maintaining a unified contract for clients.</p>
 *
 * @author BankcoreTeam
 * @author Sebastian Orjuela
 * @version 1.0
 * @see TransferHistoryResponse
 * @see TransactionHistoryResponse
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "transactionCategory"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TransferHistoryResponse.class, name = "TRANSFER"),
        @JsonSubTypes.Type(value = TransactionHistoryResponse.class, name = "BASIC")
})
public interface TransactionHistoryItem {
}
