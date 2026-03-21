package com.bankcore.accounts.dto.requests;

import com.bankcore.accounts.utils.enums.TransactionType;
import com.bankcore.accounts.utils.validators.query.TransactionQueryValidator;
import com.bankcore.accounts.utils.validators.query.ValidTransactionQuery;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

/**
 * {@code TransactionQueryParams} is a Data Transfer Object (DTO)
 * that encapsulates query parameters for retrieving transactions.
 *
 * <p>This class is annotated with {@link ValidTransactionQuery},
 * which ensures that date ranges and transaction types are valid
 * according to business rules.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Provide pagination parameters ({@code page}, {@code size}).</li>
 *   <li>Hold optional filtering parameters ({@code fromDate}, {@code toDate}, {@code type}).</li>
 *   <li>Enforce validation constraints such as minimum/maximum values and ISO-8601 date formats.</li>
 * </ul>
 *
 * <p>Validation Rules:</p>
 * <ul>
 *   <li>{@code page} must be greater than or equal to 0.</li>
 *   <li>{@code size} must be between 1 and 50.</li>
 *   <li>{@code fromDate} and {@code toDate} must be ISO-8601 formatted strings.</li>
 *   <li>{@code fromDate} cannot be after {@code toDate}.</li>
 *   <li>{@code type} must correspond to a valid {@link TransactionType}.</li>
 * </ul>
 *
 * <p>This object is typically used in service or repository layers
 * to filter transaction queries based on user-provided parameters.</p>
 *
 * @author BankcoreTeam
 * @author Sebastian Orjuela
 * @version 1.0
 * @see ValidTransactionQuery
 * @see TransactionQueryValidator
 */
@Data
@Builder
@ValidTransactionQuery
public class TransactionQueryParams {

    /**
     * The page number for pagination.
     * Must be greater than or equal to 0.
     */
    @Builder.Default
    @Min(value = 0, message = "Page must be greater than or equal to 0")
    private int page = 0;

    /**
     * The page size for pagination.
     * Must be between 1 and 50.
     */
    @Builder.Default
    @Min(value = 1, message = "Size must be greater than or equal to 1")
    @Max(value = 50, message = "Size must be less than or equal to 50")
    private int size = 20;

    /**
     * The start date of the transaction query.
     * Must be in ISO-8601 format.
     */
    private String fromDate;

    /**
     * The end date of the transaction query.
     * Must be in ISO-8601 format.
     */
    private String toDate;

    /**
     * The type of transaction to filter.
     * Must correspond to a valid {@link TransactionType}.
     */
    private String type;
}
