package dev.swizzyTheGreat.safeBank.services;

import dev.swizzyTheGreat.safeBank.exceptions.InvalidAccountException;
import dev.swizzyTheGreat.safeBank.model.Account;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BankingService {
    private final Map<String, Account> accounts = new HashMap<>();
    
    public void registerAccount(Account account) {
        String accountNumber = account.getAccountNumber();
        if (accounts.containsKey(accountNumber)) {
            throw new InvalidAccountException("Account already exists" + accountNumber);

        }
        accounts.put(accountNumber, account);

    }

    public Account findAccount(String accountNumber) {
        Account account = accounts.get(accountNumber);
        if(account == null) {
            throw new InvalidAccountException("Account does not exist" + accountNumber);
        }
        return account;
    }


    public void deposit(String accountNumber, BigDecimal depositAmount ) {
        Account account = findAccount(accountNumber);
        account.deposit(depositAmount);
    }
}
