package safeBank;

import dev.swizzyTheGreat.safeBank.exceptions.InvalidAccountException;
import dev.swizzyTheGreat.safeBank.exceptions.InvalidAmountException;
import dev.swizzyTheGreat.safeBank.model.Account;
import dev.swizzyTheGreat.safeBank.model.TransactionType;
import dev.swizzyTheGreat.safeBank.services.BankingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BankingServiceTest {

    private BankingService bankingService;
    private Account account;

    @BeforeEach
    void setUp() {
        bankingService = new BankingService();
        account = new Account(
                "Godwin",
                new BigDecimal("2000"),
                "1234567890"

        );
    }

    @Test
    void shouldRegisterAccountAndFindAccount() {
        bankingService.registerAccount(account);

        Account foundAccount = bankingService.findAccount("1234567890");
        assertNotNull(foundAccount);
        assertEquals("1234567890", foundAccount.getAccountNumber());
        assertEquals(account, foundAccount);

    }

    @Test
    void shouldNotRegisterDuplicateAccountNumber() {
        Account secondAccount = new Account("AJ", new BigDecimal("3000"), "1234567890");
        bankingService.registerAccount(account);
        assertThrows(InvalidAccountException.class, ()->{bankingService.registerAccount(secondAccount);});

    }


    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {
        assertThrows(
                InvalidAccountException.class,
                () -> bankingService.findAccount("9999999999")
        );
    }

    @Test
    void shouldDepositMoneyIntoAccountTest(){
        bankingService.registerAccount(account);
        bankingService.deposit(
                "1234567890",
                new BigDecimal("2000")

        );
        assertEquals(
                new BigDecimal("4000"),
                account.getBalance()
        );
        assertEquals(1,  account.getTransactions().size());
        assertEquals(
                TransactionType.DEPOSIT,
                account.getTransactions().get(0).transactionType()
        );
    }


    @Test
    void shouldRejectZeroDeposit() {
        bankingService.registerAccount(account);
        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit(
                        "1234567890",
                        BigDecimal.ZERO
                )
        );
        assertEquals(new BigDecimal("2000"), account.getBalance());
    }

    @Test
    void shouldRejectNegativeDeposit() {
        bankingService.registerAccount(account);
        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit(
                        "1234567890",
                        new BigDecimal("-500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
    }

    @Test
    void shouldRejectNullDeposit() {
        bankingService.registerAccount(account);
        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit(
                        "1234567890",
                        null));
        assertEquals(new BigDecimal("2000"), account.getBalance());
    }
    @Test
    void shouldRejectDepositWithMoreThanTwoDecimalPlaces() {
        bankingService.registerAccount(account);
        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit(
                        "1234567890",
                        new BigDecimal("500.123")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
    }




}
