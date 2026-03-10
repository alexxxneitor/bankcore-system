package com.bankcore.accounts.repositories;

import com.bankcore.accounts.models.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing AccountEntity instances in the database.
 * Extends JpaRepository to provide CRUD operations and custom query methods.
 * @author BankCore Team - Sebastian Orjuela
 * @version 1.0
 */
public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    /**
     * Custom query method to count the number of accounts associated with a specific customer ID.
     * @param customerId The UUID of the customer whose accounts are to be counted.
     * @return The number of accounts associated with the given customer ID.
     */
    long countByCustomerId(UUID customerId);

    /**
     * Custom query method to check if an account with a specific alias exists for a given customer ID.
     * @param alias The alias of the account to check for existence.
     * @param customerId The UUID of the customer to whom the account belongs.
     * @return true if an account with the specified alias exists for the given customer ID, false otherwise.
     */
    boolean existsByAliasAndCustomerId(String alias, UUID customerId);

    /**
     * Custom query method to check if an account with a specific IBAN exists in the database.
     * @param iban The IBAN of the account to check for existence.
     * @return true if an account with the specified IBAN exists, false otherwise.
     */
    boolean existsByAccountNumber(String iban);

    /**
     * Retrieves all bank accounts associated with the given customer ID.
     * <p>
     * Derived query method resolved by Spring Data JPA based on the {@code customerId} field
     * of {@link AccountEntity}.
     * </p>
     *
     * @param id the {@link UUID} of the customer whose accounts are to be retrieved
     * @return a {@link List} of {@link AccountEntity} belonging to the specified customer,
     *         or an empty list if no accounts are found
     */
    List<AccountEntity> findAllByCustomerId(UUID id);

    /**
     * Retrieves an account matching both the given account ID and customer ID.
     * <p>
     * This query enforces ownership validation at the database level, ensuring that
     * a customer can only access accounts that belong to them. Returns an empty
     * {@link Optional} if no match is found, whether the account does not exist
     * or belongs to a different customer — intentionally indistinguishable to prevent
     * account enumeration.
     * </p>
     *
     * @param id         the unique identifier of the account
     * @param customerId the unique identifier of the customer who should own the account
     * @return an {@link Optional} containing the matching {@link AccountEntity},
     *         or {@link Optional#empty()} if no account is found for the given combination
     */
    Optional<AccountEntity> findByIdAndCustomerId(UUID id, UUID customerId);
}
