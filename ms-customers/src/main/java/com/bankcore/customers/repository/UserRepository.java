package com.bankcore.customers.repository;

import com.bankcore.customers.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsByDni(String dni);

    boolean existsByEmail(String email);
}
