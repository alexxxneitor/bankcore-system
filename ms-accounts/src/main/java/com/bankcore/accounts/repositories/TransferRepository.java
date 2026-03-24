package com.bankcore.accounts.repositories;

import com.bankcore.accounts.models.TransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for managing {@link TransferEntity} persistence operations.
 * <p>
 * Extends {@link JpaRepository}, providing built-in CRUD functionality and
 * query derivation for transfer records. This interface acts as the data access
 * layer for transfer-related operations, enabling interaction with the database
 * in a clean and declarative way.
 * </p>
 *
 * <h2>Design Notes</h2>
 * <ul>
 *   <li>Uses Spring Data JPA to reduce boilerplate code.</li>
 *   <li>Supports standard operations such as save, findById, findAll, and delete.</li>
 *   <li>Custom query methods can be added to extend functionality (e.g., find by account).</li>
 * </ul>
 *
 * @author BankcoreTeam - Sebastian Orjuela
 * @version 1.0
 */
public interface TransferRepository extends JpaRepository<TransferEntity, UUID> {
}
