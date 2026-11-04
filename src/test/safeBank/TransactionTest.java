package safeBank;

import dev.swizzyTheGreat.safeBank.exceptions.InvalidAmountException;
import dev.swizzyTheGreat.safeBank.model.Transaction;
import dev.swizzyTheGreat.safeBank.model.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransactionTest {
    @Test
    public void testThatCreatesTransactionSuccessfully(){

        TransactionType transactionType = TransactionType.DEPOSIT;
        BigDecimal amount = new BigDecimal("5000");
        LocalDateTime transactionDate = LocalDateTime.now();
        String description = "salary";

        Transaction transaction = new Transaction(amount,description,transactionType,transactionDate);

        assertEquals(amount,transaction.amount());
        assertEquals(description,transaction.description());
        assertEquals(transactionType,transaction.transactionType());
        assertEquals(transactionDate,transaction.transactionDate());

    }

    @Test
    public void testThatThrowsExceptionWhenAmountIsNull() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> new Transaction(null, "salary", TransactionType.DEPOSIT, LocalDateTime.now())
        );

        assertEquals("Amount cannot be null or negative", exception.getMessage());
    }

    @Test
    public void testThatThrowsExceptionWhenAmountIsNegative() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class, () -> new Transaction(new BigDecimal("-100"),"salary", TransactionType.DEPOSIT, LocalDateTime.now())
        );
        assertEquals("Amount cannot be null or negative", exception.getMessage());
    }

    @Test
    void testThatThrowsExceptionWhenDescriptionIsBlank() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> new Transaction(new BigDecimal("5000"), "   ", TransactionType.DEPOSIT, LocalDateTime.now())
        );

        assertEquals("Description cannot be blank", exception.getMessage());
    }

    @Test
    void testThatThrowsExceptionWhenTransactionTypeIsNull() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> new Transaction(new BigDecimal("5000"), "salary", null, LocalDateTime.now())
        );

        assertEquals("Transaction type cannot be null", exception.getMessage());
    }

    @Test
    void testThatThrowsExceptionWhenTransactionDateIsNull() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> new Transaction(new BigDecimal("5000"), "salary", TransactionType.DEPOSIT, null)
        );

        assertEquals("Transaction date cannot be null", exception.getMessage());
    }

}
