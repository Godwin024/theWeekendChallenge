package dev.swizzyTheGreat.safeBank.model;

import dev.swizzyTheGreat.safeBank.exceptions.InsufficientFundException;
import dev.swizzyTheGreat.safeBank.exceptions.InvalidAccountException;
import dev.swizzyTheGreat.safeBank.exceptions.InvalidAmountException;
import dev.swizzyTheGreat.safeBank.exceptions.WithdrawalLimitExceededException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Account {
    private final String accountName;
    private  BigDecimal balance;
    private final String accountNumber;
    private final List<Transaction> transactionsHistory = new ArrayList<>();

    public Account(String accountName, BigDecimal balance, String accountNumber) throws InvalidAccountException {
        if (accountName == null || accountName.isBlank()) {
            throw new InvalidAccountException("Account name cannot be empty");
        }
        if (balance == null || balance.compareTo(BigDecimal.ZERO) < 0 ) {
            throw new InvalidAccountException("An opening balance cannot be null or negative");
        }
        if(balance.scale() > 2){
            throw new InvalidAccountException("Balance cannot be have more than 2 decimal places");
        }

        if (accountNumber == null || accountNumber.isBlank()) {
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
        return Collections.unmodifiableList(transactionsHistory);
    }

    @Override
    public String toString() {
        return "Account{" +
                "accountName='" + accountName + '\'' +
                ", balance=" + balance +
                ", accountNumber='" + accountNumber + '\'' +
                ", transactionsHistory=" + transactionsHistory +
                '}';
    }

    public void deposit(BigDecimal depositAmount) {
        if(depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("Deposit amount cannot be null or negative");
        }

        if(depositAmount.scale() > 2){
            throw new InvalidAmountException("Deposit amount cannot be have more than 2 decimal places");
        }
        this.balance = this.balance.add(depositAmount);

        //make i record the transaction history first
        transactionsHistory.add(new Transaction(
                depositAmount,
                "Deposit Successfully",
                TransactionType.DEPOSIT,
                java.time.LocalDateTime.now()
        ));

    }

    public void withdraw(BigDecimal withdrawalAmount) {
        if(withdrawalAmount == null || withdrawalAmount.compareTo(BigDecimal.ZERO) <= 0){
            throw new InvalidAmountException("Withdrawal amount cannot be null or negative");
        }

        if(withdrawalAmount.scale() > 2){
            throw new InvalidAmountException("Withdrawal amount cannot be have more than 2 decimal places");
        }

        BigDecimal withdrawalLimit = new BigDecimal("100000");
        if(withdrawalAmount.compareTo(withdrawalLimit) > 0){
            throw new WithdrawalLimitExceededException("Withdrawal limit exceeded");
        }

        if(withdrawalAmount.compareTo(this.balance) > 0){
            throw new InsufficientFundException(
                    String.format("Insufficient funds. Available balance: .2f", this.balance)
            );

        }

        this.balance = this.balance.subtract(withdrawalAmount);

        transactionsHistory.add(new Transaction(
                withdrawalAmount,
                "Withdrawal Successfully",
                TransactionType.WITHDRAWAL,
                java.time.LocalDateTime.now()

        ));


    }
}
