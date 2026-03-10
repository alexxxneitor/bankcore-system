package com.bankcore.accounts.services;

import com.bankcore.accounts.dto.requests.TransactionRequest;
import com.bankcore.accounts.dto.responses.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService{
    @Override
    public TransactionResponse makeDeposit(TransactionRequest request, UUID accountId, UUID customerId) {
        return null;
    }
}
