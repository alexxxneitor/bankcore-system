package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.models.AccountEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountRegisterResponse toAccountRegisterResponse(AccountEntity accountEntity);
}
