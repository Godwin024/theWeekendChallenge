package safeBank;

import dev.swizzyTheGreat.safeBank.model.Transaction;
import dev.swizzyTheGreat.safeBank.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTest {
    @Test
    public void testThatCreatesTransactionSuccessfully(){

        TransactionType transactionType = TransactionType.DEPOSIT;
        BigDecimal amount = new BigDecimal("5000");
        LocalDateTime transactionDate = LocalDateTime.now();
        String description = "salary";

        Transaction transaction = new Transaction(amount,description,transactionType,transactionDate);

        assertEquals(amount,transaction.getAmount());
        assertEquals(description,transaction.getDescription());
        assertEquals(transactionType,transaction.getTransactionType());
        assertEquals(transactionDate,transaction.getTransactionDate());




    }
}
