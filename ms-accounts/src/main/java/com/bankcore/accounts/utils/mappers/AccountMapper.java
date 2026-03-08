package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.dto.responses.UserAccountResponse;
import com.bankcore.accounts.models.AccountEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper interface for converting {@link AccountEntity} instances into their corresponding response DTOs.
 *
 * @author BankCore Team - Sebastian Oejuela - Cristian Ortiz
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {

    /**
     * Converts an {@link AccountEntity} to an {@link AccountRegisterResponse}.
     *
     * @param accountEntity the account entity to convert
     * @return the mapped {@link AccountRegisterResponse}
     */
    AccountRegisterResponse toAccountRegisterResponse(AccountEntity accountEntity);

    /**
     * Converts an {@link AccountEntity} to a {@link UserAccountResponse}.
     *
     * @param account the account entity to convert
     * @return the mapped {@link UserAccountResponse}
     */
    UserAccountResponse toResponse(AccountEntity account);

    /**
     * Converts a list of {@link AccountEntity} instances to a list of {@link UserAccountResponse}.
     *
     * @param accounts the list of account entities to convert
     * @return a {@link List} of {@link UserAccountResponse}
     */
    List<UserAccountResponse> toResponseList(List<AccountEntity> accounts);

}