package dev.swizzyTheGreat.safeBank.model;

import dev.swizzyTheGreat.safeBank.exceptions.InvalidAccountException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private final String accountName;
    private  BigDecimal balance;
    private final String accountNumber;
    private final List<Transaction> transactions = new ArrayList<>();

    public Account(String accountName, BigDecimal balance, String accountNumber) throws InvalidAccountException {
        if (accountName == null || accountName.isEmpty()) {
            throw new InvalidAccountException("Account name cannot be empty");
        }
        if (balance == null ) {
            throw new InvalidAccountException("An opening balance cannot be null");
        }
        if(balance.scale() > 2){
            throw new InvalidAccountException("Balance cannot be have more than 2 decimal places");
        }

        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new InvalidAccountException("Account number cannot be empty");
        }
        this.accountName = accountName.trim();
        this.balance = balance;
        this.accountNumber = accountNumber.trim();
    }

    public String getAccountName() {
        return accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }


    public String getAccountNumber() {
        return accountNumber;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", accountNumber='" + accountNumber + '\'' +
                ", transactions=" + transactions +
                '}';
    }
}
