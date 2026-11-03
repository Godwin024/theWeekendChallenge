package dev.swizzyTheGreat.safeBank.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final BigDecimal amount;
    private final String description;
    private final TransactionType transactionType;
    private final LocalDateTime transactionDate;

    public Transaction(BigDecimal amount, String description, TransactionType transactionType, LocalDateTime transactionDate) {
        this.amount = amount;
        this.description = description;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;

    }


    public BigDecimal getAmount() {
        return amount;
    }


    public String getDescription() {
        return description;
    }


    public TransactionType getTransactionType() {
        return transactionType;
    }


    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "amount=" + amount +
                ", description='" + description + '\'' +
                ", transactionType=" + transactionType +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
