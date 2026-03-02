package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.AccountRegisterResponse;
import com.bankcore.accounts.models.AccountEntity;
import com.bankcore.accounts.dto.responses.UserAccountResponse;
import com.bankcore.accounts.model.AccountEntity;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between AccountEntity and AccountRegisterResponse.
 * @author BankCore Team - Sebastian Oejuela - Cristian Ortiz
 * @version 1.0
 */
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountRegisterResponse toAccountRegisterResponse(AccountEntity accountEntity);
    UserAccountResponse toResponse(AccountEntity account);
    List<UserAccountResponse> toResponseList(List<AccountEntity> accounts);

}
