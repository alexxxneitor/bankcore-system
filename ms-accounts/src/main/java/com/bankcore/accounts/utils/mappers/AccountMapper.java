package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.UserAccountResponse;
import com.bankcore.accounts.model.AccountEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    UserAccountResponse toResponse(AccountEntity account);

    List<UserAccountResponse> toResponseList(List<AccountEntity> accounts);

}
