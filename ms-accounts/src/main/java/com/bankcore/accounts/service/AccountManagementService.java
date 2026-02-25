package com.bankcore.accounts.service;

import com.bankcore.accounts.dto.responses.UserAccountResponse;

import java.util.List;

public interface AccountManagementService {

    List<UserAccountResponse> getCurrentUserAccounts(String id);

}
