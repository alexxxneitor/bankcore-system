package com.bankcore.customers.repository;

import com.bankcore.customers.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface responsible for managing persistence operations
 * related to {@link UserEntity}.
 * <p>
 * Extends {@link JpaRepository} to provide standard CRUD operations
 * and defines additional query methods for uniqueness validation.
 * </p>
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Checks whether a user exists with the given DNI.
     *
     * @param dni the government-issued identification number to verify
     * @return {@code true} if a user with the specified DNI exists;
     *         {@code false} otherwise
     */
    boolean existsByDni(String dni);

    /**
     * Checks whether a user exists with the given email address.
     *
     * @param email the email address to verify
     * @return {@code true} if a user with the specified email exists;
     *         {@code false} otherwise
     */
    boolean existsByEmail(String email);
}
