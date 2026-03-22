package com.bankcore.accounts.exceptions;

/**
 * {@code NoTransactionHistoryException} is a custom runtime exception
 * that indicates a requested account has no transaction history available.
 *
 * <p>This exception is typically thrown by service or repository layers
 * when a query for transaction history returns no results.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Signal that an account exists but has no associated transactions.</li>
 *   <li>Allow controllers or higher layers to handle the case gracefully,
 *       for example by returning HTTP 204 (No Content) or a custom error response.</li>
 * </ul>
 *
 * @author Banckore Team
 * @author Sebastián
 * @version 1.0
 */
public class NoTransactionHistoryException extends RuntimeException {
    public NoTransactionHistoryException(String message) {
        super(message);
    }
}
