package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.models.AccountEntity;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between AccountEntity and AccountRegisterResponse.
 * @author BankCore Team - Sebastian Oejuela
 * @version 1.0
 */
@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountRegisterResponse toAccountRegisterResponse(AccountEntity accountEntity);
}
