package safeBank;

import dev.swizzyTheGreat.exceptions.InsufficientFundsException;
import dev.swizzyTheGreat.exceptions.InvalidAccountException;
import dev.swizzyTheGreat.exceptions.InvalidAmountException;
import dev.swizzyTheGreat.exceptions.WithdrawalLimitExceededException;
import dev.swizzyTheGreat.data.models.Account;
import dev.swizzyTheGreat.data.models.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    @Test
    public void shouldCreateAccountWithValidDetails() throws InvalidAccountException {
        String accountName = "Godwin";
        BigDecimal balance = new BigDecimal("5000.00");
        String accountNumber = "1234567890";
        Account account = new Account(accountName, balance, accountNumber);
        assertNotNull(account);
        assertEquals(accountName, account.getAccountName());
        assertEquals(0, balance.compareTo(account.getBalance()));
        assertEquals(accountNumber, account.getAccountNumber());
    }
    @Test
    void shouldRejectNullAccountName() {

        assertThrows(InvalidAccountException.class, () ->
                new Account(null, new BigDecimal("5000.00"), "1234567890")
        );
    }

    @Test
    void shouldRejectBlankAccountName() {

        assertThrows(InvalidAccountException.class, () ->
                new Account("   ", new BigDecimal("5000.00"), "1234567890")
        );
    }


    @Test
    void shouldRejectNullOpeningBalance() {

        assertThrows(InvalidAccountException.class, () ->
                new Account("Godwin", null, "1234567890")
        );
    }

    @Test
    void shouldRejectNegativeOpeningBalance() {

        assertThrows(InvalidAccountException.class, () ->
                new Account("Godwin", new BigDecimal("-5000.00"), "1234567890")
        );
    }

    @Test
    void shouldRejectAccountNumberContainingOnlySpaces() {

        assertThrows(InvalidAccountException.class, () ->
                new Account("Godwin", new BigDecimal("5000.00"), "   ")
        );
    }
    @Test
    public  void testShouldDepositMoneySuccessfully() throws InvalidAccountException {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");

        BigDecimal depositAmount = new BigDecimal("5000.00");

        account.deposit(depositAmount);
        assertEquals(0, new BigDecimal("10000.00").compareTo(account.getBalance()));
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().get(0).transactionType());
    }

    @Test
    void shouldRejectNullDepositAmount() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");

        assertThrows(InvalidAmountException.class, () ->
                account.deposit(null)
        );
    }

    @Test
    void shouldRejectZeroDepositAmount() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");

        assertThrows(InvalidAmountException.class, () ->
                account.deposit(BigDecimal.ZERO)
        );
    }

    @Test
    void shouldWithdrawMoneySuccessfullyTest() {
        Account account = new Account("Godwin", new BigDecimal("10000.00"), "1234567890");

        account.withdraw(new BigDecimal("3000.00"));

        assertEquals(0, new BigDecimal("7000.00").compareTo(account.getBalance()));
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.WITHDRAWAL, account.getTransactions().get(0).transactionType());
    }

    @Test
    void shouldRejectNullWithdrawalAmount() {
        Account account = new Account("Godwin", new BigDecimal("10000.00"), "1234567890");

        assertThrows(InvalidAmountException.class, () ->
                account.withdraw(null)
        );
    }

    @Test
    void shouldRejectNegativeWithdrawalAmount() {
        Account account = new Account("Godwin", new BigDecimal("10000.00"), "1234567890");

        assertThrows(InvalidAmountException.class, () ->
                account.withdraw(new BigDecimal("-500"))
        );
    }

    @Test
    void shouldRejectWithdrawalExceedingBalance() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");

        assertThrows(InsufficientFundsException.class, () ->
                account.withdraw(new BigDecimal("6000.00"))
        );
    }

    @Test
    void shouldRejectWithdrawalExceedingLimit() {
        Account account = new Account("Godwin", new BigDecimal("200000.00"), "1234567890");

        assertThrows(WithdrawalLimitExceededException.class, () ->
                account.withdraw(new BigDecimal("120000.00"))
        );
    }

    @Test
    void shouldAllowWithdrawalOfExactly100000() {
        Account account = new Account("Godwin", new BigDecimal("150000.00"), "1234567890");

        account.withdraw(new BigDecimal("100000.00"));

        assertEquals(0, new BigDecimal("50000.00").compareTo(account.getBalance()));
    }

    @Test
    void shouldNotChangeBalanceWhenWithdrawalFails() throws InvalidAccountException {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new BigDecimal("6000.00")));
        assertEquals(0, new BigDecimal("5000.00").compareTo(account.getBalance()));
    }

    @Test
    void shouldNotRecordTransactionWhenWithdrawalFails() throws InvalidAccountException {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new BigDecimal("6000.00")));
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    void shouldRejectNegativeDepositAmountTest() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InvalidAmountException.class, () -> account.deposit(new BigDecimal("-500.00")));
    }

    @Test
    void shouldRejectDepositWithMoreThanTwoDecimalPlacesTest() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InvalidAmountException.class, () -> account.deposit(new BigDecimal("500.123")));
    }

    @Test
    void shouldRejectZeroWithdrawalAmountTest() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InvalidAmountException.class, () -> account.withdraw(BigDecimal.ZERO));
    }

    @Test
    void shouldRejectWithdrawalWithMoreThanTwoDecimalPlacesTest() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InvalidAmountException.class, () -> account.withdraw(new BigDecimal("100.123")));
    }

    @Test
    void shouldNotChangeBalanceOrHistoryWhenDepositFailsTest() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InvalidAmountException.class, () -> account.deposit(new BigDecimal("-500.00")));

        assertEquals(0, new BigDecimal("5000.00").compareTo(account.getBalance()));
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    void shouldAllowSuccessfulDepositAfterFailedWithdrawalTest() {
        Account account = new Account("Godwin", new BigDecimal("5000.00"), "1234567890");
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(new BigDecimal("6000.00")));
        account.deposit(new BigDecimal("2000.00"));
        assertEquals(0, new BigDecimal("7000.00").compareTo(account.getBalance()));
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().get(0).transactionType());
    }

}
