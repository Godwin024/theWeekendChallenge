package dev.swizzyTheGreat.services;

import dev.swizzyTheGreat.exceptions.InvalidAccountException;
import dev.swizzyTheGreat.data.models.Account;

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
        if(accountNumber == null || accountNumber.isBlank()) {
            throw new InvalidAccountException("Account number cannot be empty");
        }
        Account account = accounts.get(accountNumber.trim());
        if(account == null) {
            throw new InvalidAccountException("Account does not exist" + accountNumber);
        }
        return account;
    }


    public void deposit(String accountNumber, BigDecimal depositAmount ) {
        Account account = findAccount(accountNumber);
        account.deposit(depositAmount);
    }

    public void withdraw(String accountNumber, BigDecimal withdrawAmount ) {
        Account account = findAccount(accountNumber);
        account.withdraw(withdrawAmount);
    }

    public void transfer(String senderAccountNumber, String recipientAccountNumber, BigDecimal amount) {
        Account senderAccount = findAccount(senderAccountNumber);
        Account recipientAccount = findAccount(recipientAccountNumber);
        if(senderAccount.equals(recipientAccount)) {
            throw new InvalidAccountException("Sender can not transfer money to the same account");
        }
        senderAccount.withdraw(amount);
        try{
            recipientAccount.deposit(amount);
        }catch (RuntimeException exception) {
            senderAccount.rollbackWithdrawal(amount);
            throw exception;
        }
    }




}
