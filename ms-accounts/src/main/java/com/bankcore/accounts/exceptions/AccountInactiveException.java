package com.bankcore.accounts.exceptions;

import com.bankcore.accounts.utils.enums.AccountStatus;

public class AccountInactiveException extends RuntimeException{

    public AccountInactiveException(AccountStatus status){
        super(String.join(" ", "Account is not active. account status is", status.name()));
    }
}
