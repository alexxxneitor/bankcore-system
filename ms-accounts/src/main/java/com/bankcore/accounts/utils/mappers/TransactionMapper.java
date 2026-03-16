package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.models.TransactionEntity;
import org.mapstruct.Mapper;

import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between {@link TransactionEntity} and {@link TransactionResponse}.
 * <p>
 * This interface uses MapStruct to automatically generate the implementation
 * at compile time. It provides a concise and type-safe way to transform
 * transaction entities into response DTOs for API communication.
 * </p>
 *
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    /**
     * Converts a {@link TransactionEntity} into a {@link TransactionResponse}.
     *
     * @param transactionEntity the entity representing the transaction
     * @return a {@link TransactionResponse} containing mapped transaction details
     */
    TransactionResponse toTransactionResponse(TransactionEntity transactionEntity);
}