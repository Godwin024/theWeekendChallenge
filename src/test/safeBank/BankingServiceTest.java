package safeBank;

import dev.swizzyTheGreat.exceptions.InsufficientFundsException;
import dev.swizzyTheGreat.exceptions.InvalidAccountException;
import dev.swizzyTheGreat.exceptions.InvalidAmountException;
import dev.swizzyTheGreat.exceptions.WithdrawalLimitExceededException;
import dev.swizzyTheGreat.data.models.Account;
import dev.swizzyTheGreat.data.models.TransactionType;
import dev.swizzyTheGreat.services.BankingService;
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

    @Test
    void shouldWithdrawMoneyFromAccountTest(){
        bankingService.registerAccount(account);
        bankingService.withdraw("1234567890", new BigDecimal("500"));
        assertEquals(new BigDecimal("1500"), account.getBalance());
        assertEquals(1,  account.getTransactions().size());
        assertEquals(TransactionType.WITHDRAWAL, account.getTransactions().get(0).transactionType());
    }

    @Test
    void shouldRejectZeroWithdrawal() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAmountException.class, () -> bankingService.withdraw("1234567890", BigDecimal.ZERO));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }


    @Test
    void shouldRejectNegativeWithdrawal() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAmountException.class, () -> bankingService.withdraw("1234567890", new BigDecimal("-500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldRejectNullWithdrawal() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAmountException.class, () -> bankingService.withdraw("1234567890", null));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldRejectWithdrawalWithMoreThanTwoDecimalPlaces() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAmountException.class, () -> bankingService.withdraw("1234567890", new BigDecimal("500.123")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldRejectWithdrawalAboveLimit() {
        bankingService.registerAccount(account);
        assertThrows(WithdrawalLimitExceededException.class, () -> bankingService.withdraw("1234567890", new BigDecimal("100001")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldRejectWithdrawalWhenBalanceIsInsufficient() {
        bankingService.registerAccount(account);
        assertThrows(InsufficientFundsException.class, () -> bankingService.withdraw("1234567890", new BigDecimal("2500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldAllowWithdrawalOfExactBalance() {
        bankingService.registerAccount(account);
        bankingService.withdraw("1234567890", new BigDecimal("2000"));
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(1, account.getTransactions().size());
        assertEquals(TransactionType.WITHDRAWAL, account.getTransactions().get(0).transactionType());
    }

    @Test
    void shouldAllowWithdrawalAtExactLimit() throws InvalidAccountException {
        Account highBalanceAccount = new Account("Godwin", new BigDecimal("150000"), "9876543210");
        bankingService.registerAccount(highBalanceAccount);
        bankingService.withdraw("9876543210", new BigDecimal("100000"));
        assertEquals(new BigDecimal("50000"), highBalanceAccount.getBalance());
        assertEquals(1, highBalanceAccount.getTransactions().size());
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        bankingService.transfer("1234567890", "9876543210", new BigDecimal("500"));
        assertEquals(new BigDecimal("1500"), account.getBalance());
        assertEquals(new BigDecimal("1500"), recipient.getBalance());
        assertEquals(1, account.getTransactions().size());
        assertEquals(1, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectTransferOfZeroAmount() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        assertThrows(InvalidAmountException.class, () -> bankingService.transfer("1234567890", "9876543210", BigDecimal.ZERO));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, account.getTransactions().size());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectNegativeTransferAmount() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        assertThrows(InvalidAmountException.class, () -> bankingService.transfer("1234567890", "9876543210", new BigDecimal("-500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, account.getTransactions().size());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectTransferWhenSenderHasInsufficientFunds() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        assertThrows(InsufficientFundsException.class, () -> bankingService.transfer("1234567890", "9876543210", new BigDecimal("2500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, account.getTransactions().size());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectNullTransferAmount() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        assertThrows(InvalidAmountException.class, () -> bankingService.transfer("1234567890", "9876543210", null));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, account.getTransactions().size());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectTransferWithMoreThanTwoDecimalPlaces() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        assertThrows(InvalidAmountException.class, () -> bankingService.transfer("1234567890", "9876543210", new BigDecimal("500.123")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, account.getTransactions().size());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectTransferAboveWithdrawalLimit() throws InvalidAccountException {
        Account sender = new Account("Godwin", new BigDecimal("150000"), "1234567890");
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(sender);
        bankingService.registerAccount(recipient);
        assertThrows(WithdrawalLimitExceededException.class, () -> bankingService.transfer("1234567890", "9876543210", new BigDecimal("120000")));
        assertEquals(new BigDecimal("150000"), sender.getBalance());
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, sender.getTransactions().size());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectTransferWhenRecipientDoesNotExist() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAccountException.class, () -> bankingService.transfer("1234567890", "9999999999", new BigDecimal("500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldRejectTransferWhenSenderDoesNotExist() {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(recipient);
        assertThrows(InvalidAccountException.class, () -> bankingService.transfer("9999999999", "9876543210", new BigDecimal("500")));
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldRejectTransferToSameAccount() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAccountException.class, () -> bankingService.transfer("1234567890", "1234567890", new BigDecimal("500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldAllowTransferAtExactWithdrawalLimit() throws InvalidAccountException {
        Account sender = new Account("Godwin", new BigDecimal("150000"), "1234567890");
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(sender);
        bankingService.registerAccount(recipient);
        bankingService.transfer("1234567890", "9876543210", new BigDecimal("100000"));
        assertEquals(new BigDecimal("50000"), sender.getBalance());
        assertEquals(new BigDecimal("101000"), recipient.getBalance());
        assertEquals(1, sender.getTransactions().size());
        assertEquals(1, recipient.getTransactions().size());
    }

    @Test
    void shouldRecordCorrectTransactionTypesForTransfer() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        bankingService.transfer("1234567890", "9876543210", new BigDecimal("500"));
        assertEquals(TransactionType.WITHDRAWAL, account.getTransactions().get(0).transactionType());
        assertEquals(TransactionType.DEPOSIT, recipient.getTransactions().get(0).transactionType());
        assertEquals(new BigDecimal("500"), account.getTransactions().get(0).amount());
        assertEquals(new BigDecimal("500"), recipient.getTransactions().get(0).amount());
    }

    @Test
    void shouldRollbackTransferWhenRecipientDepositFails() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210")
        {
            @Override
            public void deposit(BigDecimal amount) {
                throw new InvalidAmountException("Fake deposit failure");
            }
        };

        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        assertThrows(InvalidAmountException.class, () -> bankingService.transfer("1234567890", "9876543210", new BigDecimal("500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(new BigDecimal("1000"), recipient.getBalance());
        assertEquals(0, account.getTransactions().size());
        assertEquals(0, recipient.getTransactions().size());
    }

    @Test
    void shouldNotRollbackSuccessfulTransfer() throws InvalidAccountException {
        Account recipient = new Account("John", new BigDecimal("1000"), "9876543210");
        bankingService.registerAccount(account);
        bankingService.registerAccount(recipient);
        bankingService.transfer("1234567890", "9876543210", new BigDecimal("500"));
        assertEquals(new BigDecimal("1500"), account.getBalance());
        assertEquals(new BigDecimal("1500"), recipient.getBalance());
        assertEquals(1, account.getTransactions().size());
        assertEquals(1, recipient.getTransactions().size());
        assertEquals(TransactionType.WITHDRAWAL, account.getTransactions().get(0).transactionType());
        assertEquals(TransactionType.DEPOSIT, recipient.getTransactions().get(0).transactionType());
    }

    @Test
    void shouldRejectTransferWithNullSenderAccountNumber() {
        assertThrows(InvalidAccountException.class, () -> bankingService.transfer(null, "9876543210", new BigDecimal("500")));
    }

    @Test
    void shouldRejectTransferWithNullRecipientAccountNumber() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAccountException.class, () -> bankingService.transfer("1234567890", null, new BigDecimal("500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldRejectTransferWithBlankSenderAccountNumber() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAccountException.class, () -> bankingService.transfer("", "9876543210", new BigDecimal("500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldRejectTransferWithSpacesAsSenderAccountNumber() {
        bankingService.registerAccount(account);
        assertThrows(InvalidAccountException.class, () -> bankingService.transfer("   ", "9876543210", new BigDecimal("500")));
        assertEquals(new BigDecimal("2000"), account.getBalance());
        assertEquals(0, account.getTransactions().size());
    }

    @Test
    void shouldFindAccountWhenAccountNumberContainsSurroundingSpaces() {
        bankingService.registerAccount(account);
        Account foundAccount = bankingService.findAccount(" 1234567890 ");
        assertNotNull(foundAccount);
        assertEquals(account, foundAccount);
    }
}
