package dev.swizzyTheGreat.data.models;

import dev.swizzyTheGreat.exceptions.InvalidAmountException;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(BigDecimal amount, String description, TransactionType transactionType,
                          LocalDateTime transactionDate) {
    public Transaction {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount cannot be null or negative");
        }
        if (description == null || description.isBlank()) {
            throw new InvalidAmountException("Description cannot be blank");
        }
        if (transactionType == null) {
            throw new InvalidAmountException("Transaction type cannot be null");
        }
        if (transactionDate == null) {
            throw new InvalidAmountException("Transaction date cannot be null");
        }


}

}
