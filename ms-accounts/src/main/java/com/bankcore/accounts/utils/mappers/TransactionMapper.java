package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.dto.responses.TransferResponse;
import com.bankcore.accounts.models.TransactionEntity;
import com.bankcore.accounts.models.TransferEntity;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

import java.math.BigDecimal;

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
     * <p>
     * This method uses MapStruct to transform transaction entity data into
     * a response DTO suitable for API exposure. It also incorporates the
     * balance before the transaction as an additional contextual field.
     * </p>
     *
     * <h2>Mapping Details</h2>
     * <ul>
     *   <li>{@code transactionEntity.createdAt} → {@code timestamp}</li>
     *   <li>{@code balanceBefore} → {@code balanceBefore}</li>
     * </ul>
     *
     * @param transactionEntity the entity representing the transaction
     * @param balanceBefore     the account balance before the transaction occurred
     * @return a {@link TransactionResponse} containing mapped transaction details
     */
    @Mapping(source = "transactionEntity.createdAt", target = "timestamp")
    @Mapping(source = "balanceBefore", target = "balanceBefore")
    @Mapping(source = "transactionEntity.status", target = "status")
    TransactionResponse toTransactionResponse(TransactionEntity transactionEntity, BigDecimal balanceBefore);

    /**
     * Maps a {@link TransferEntity} and additional contextual information
     * into a {@link TransferResponse} DTO.
     * <p>
     * This method leverages MapStruct to transform entity data into a response
     * object suitable for API exposure. It combines persisted transfer details
     * with runtime-provided values such as source account, beneficiary name,
     * and total debited amount.
     * </p>
     *
     * <h2>Mapping Details</h2>
     * <ul>
     *   <li>{@code transferEntity.id} → {@code transferId}</li>
     *   <li>{@code transferEntity.destinationAccountNumber} → {@code destinationAccount}</li>
     *   <li>{@code transferEntity.createdAt} → {@code timestamp}</li>
     *   <li>{@code sourceAccount} → {@code sourceAccount}</li>
     *   <li>{@code beneficiaryName} → {@code beneficiaryName}</li>
     *   <li>{@code totalDebited} → {@code totalDebited}</li>
     * </ul>
     *
     * @param transferEntity   the persisted transfer entity containing core details
     * @param sourceAccount    the IBAN or identifier of the source account
     * @param beneficiaryName  the name of the beneficiary receiving the transfer
     * @param totalDebited     the total amount debited, including fees
     * @return a {@link TransferResponse} populated with mapped values
     */
    @Mapping(source = "transferEntity.id", target = "transferId")
    @Mapping(source = "transferEntity.destinationAccountNumber", target = "destinationAccount")
    @Mapping(source = "transferEntity.createdAt", target = "timestamp")
    TransferResponse toTransferResponse(
            TransferEntity transferEntity,
            String sourceAccount,
            String beneficiaryName,
            BigDecimal totalDebited
    );
}