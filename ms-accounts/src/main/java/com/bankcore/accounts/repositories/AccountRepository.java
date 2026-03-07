package com.bankcore.accounts.repositories;

import com.bankcore.accounts.models.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
