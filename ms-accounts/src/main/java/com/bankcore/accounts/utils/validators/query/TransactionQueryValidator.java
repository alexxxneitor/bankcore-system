package com.bankcore.accounts.utils.validators.query;

import com.bankcore.accounts.dto.requests.TransactionQueryParams;
import com.bankcore.accounts.utils.enums.TransactionType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * {@code TransactionQueryValidator} is a custom implementation of
 * {@link ConstraintValidator} that validates {@link TransactionQueryParams}
 * objects annotated with {@link ValidTransactionQuery}.
 *
 * <p>This validator enforces business rules on transaction query parameters,
 * ensuring that date ranges, transaction types, and formats are consistent
 * and valid.</p>
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Validate that {@code fromDate} and {@code toDate} are in ISO-8601 format.</li>
 *   <li>Ensure that {@code fromDate} is not after {@code toDate}.</li>
 *   <li>Verify that {@code type} corresponds to a valid {@link TransactionType}.</li>
 *   <li>Accumulate constraint violation messages for invalid parameters.</li>
 * </ul>
 *
 * <p>Validation Strategy:</p>
 * <ul>
 *   <li>If {@code values} is {@code null}, validation passes (no constraints).</li>
 *   <li>Dates are parsed using {@link Instant#parse}, with errors reported
 *       if parsing fails.</li>
 *   <li>Transaction type is validated against {@link TransactionType} enum.</li>
 *   <li>Custom error messages are added via {@link ConstraintValidatorContext}.</li>
 * </ul>
 *
 * @author BankcoreTeam
 * @author Sebastian Orjuela
 * @version 1.0
 * @see ValidTransactionQuery
 * @see TransactionQueryParams
 * @see TransactionType
 */
public class TransactionQueryValidator implements ConstraintValidator<ValidTransactionQuery, TransactionQueryParams> {

    /**
     * Validates the given {@link TransactionQueryParams} object.
     *
     * @param values the transaction query parameters to validate
     * @param context the validation context used to report errors
     * @return {@code true} if the parameters are valid, {@code false} otherwise
     */
    @Override
    public boolean isValid(TransactionQueryParams values, ConstraintValidatorContext context) {

        if (values == null) return true;

        context.disableDefaultConstraintViolation();
        boolean isValid = true;

        Instant from = null;
        Instant to = null;

        try {
            if (values.getFromDate() != null) {
                from = Instant.parse(values.getFromDate());
            }
        } catch (DateTimeParseException ex) {
            addError(context, "fromDate must be ISO-8601");
            isValid = false;
        }

        try {
            if (values.getToDate() != null) {
                to = Instant.parse(values.getToDate());
            }
        } catch (DateTimeParseException ex) {
            addError(context, "toDate must be ISO-8601");
            isValid = false;
        }

        if (from != null & to != null && from.isAfter(to)) {
            addError(context, "fromDate cannot be after toDate");
            isValid = false;
        }

        if (values.getType() != null) {
            try {
                TransactionType.valueOf(values.getType().toUpperCase());
            } catch (IllegalArgumentException ex) {
                addError(context, "Invalid transaction type");
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Adds a custom error message to the validation context.
     *
     * @param context the validation context
     * @param message the error message to add
     */
    private void addError(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
