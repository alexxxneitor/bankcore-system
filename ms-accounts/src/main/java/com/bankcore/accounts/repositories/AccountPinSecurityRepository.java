package com.bankcore.accounts.repositories;

import com.bankcore.accounts.models.AccountPinSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountPinSecurityRepository extends JpaRepository<AccountPinSecurity, UUID> {

    Optional<AccountPinSecurity> findByAccount_Id(UUID accountId);
}
