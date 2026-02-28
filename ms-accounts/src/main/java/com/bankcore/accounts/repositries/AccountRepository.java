package com.bankcore.accounts.repositries;

import com.bankcore.accounts.models.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {

    long countByCustomerId(UUID customerId);
}
