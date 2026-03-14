package com.bankcore.accounts.services.complements;

import com.bankcore.accounts.integrations.dto.responses.PinValidateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PinAttemptManagerService {

    public void PinAttemptManager(UUID accountId, PinValidateResponse response){}
}
