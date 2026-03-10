package com.bankcore.accounts.utils.mappers;

import com.bankcore.accounts.dto.responses.TransactionResponse;
import com.bankcore.accounts.models.TransactionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    TransactionResponse toTransactionResponse(TransactionEntity transactionEntity);
}
