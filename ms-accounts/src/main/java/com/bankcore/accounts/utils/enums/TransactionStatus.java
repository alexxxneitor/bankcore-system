package com.bankcore.accounts.utils.enums;

/**
 * Enumeration representing the possible statuses of a financial transaction.
 * <p>
 * Each status reflects the current lifecycle state of the transaction
 * within the system.
 * </p>
 *
 * <ul>
 *   <li>{@link #COMPLETED} – The transaction has been successfully processed.</li>
 *   <li>{@link #PENDING} – The transaction is awaiting processing or confirmation.</li>
 *   <li>{@link #FAILED} – The transaction could not be completed due to an error or rejection.</li>
 *   <li>{@link #REVERSED} – The transaction was rolled back or canceled after initial processing.</li>
 * </ul>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Used in {@code TransactionEntity} to indicate the current state of a transaction.</li>
 *   <li>Helps clients and services handle business logic based on transaction outcomes.</li>
 * </ul>
 * </p>
 *
 * @author Bankcore Team - Sebastian Orjuela
 * @version 1.0
 */
public enum TransactionStatus {

    COMPLETED,
    PENDING,
    FAILED,
    REVERSED
}
