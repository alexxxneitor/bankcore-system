package com.bankcore.accounts.utils.enums;

/**
 * Enumeration representing the types of financial transactions supported by the system.
 * <p>
 * Each type indicates the nature of the transaction and its effect on the account balance.
 * </p>
 *
 * <ul>
 *   <li>{@link #DEPOSIT} – Funds added to the account.</li>
 *   <li>{@link #WITHDRAWAL} – Funds removed from the account.</li>
 *   <li>{@link #TRANSFER_IN} – Incoming transfer from another account.</li>
 *   <li>{@link #TRANSFER_OUT} – Outgoing transfer to another account.</li>
 *   <li>{@link #FEE} – Deduction applied as a service or transaction fee.</li>
 * </ul>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Used in {@code TransactionEntity} and DTOs to classify transactions.</li>
 *   <li>Supports business logic for calculating balances and auditing activity.</li>
 * </ul>
 * </p>
 *
 * @author BankcoreTeam - Sebastian Orjuela
 * @version 1.0
 */
public enum TransactionType {

    DEPOSIT,
    WITHDRAWAL,
    TRANSFER_IN,
    TRANSFER_OUT,
    FEE
}
