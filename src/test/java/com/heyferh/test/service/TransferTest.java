package com.heyferh.test.service;

import com.heyferh.test.model.Account;
import com.heyferh.test.model.Money;
import com.heyferh.test.model.Transaction;
import com.heyferh.test.repository.AccountRepository;
import com.heyferh.test.repository.TransactionRepository;
import com.heyferh.test.service.api.AccountService;
import com.heyferh.test.service.api.TransactionService;
import com.heyferh.test.service.impl.AccountServiceImpl;
import com.heyferh.test.service.impl.TransactionServiceImpl;
import com.heyferh.test.util.InsufficientBalanceException;
import com.heyferh.test.util.NegativeFundsException;
import com.heyferh.test.util.UnknownAccountException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import static com.heyferh.test.util.CurrencyCode.RUB;
import static com.heyferh.test.util.MockCurrencyConverter.convert;
import static java.math.BigDecimal.ZERO;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TransferTest {

    private final static Logger logger = LoggerFactory.getLogger(TransferTest.class);

    private final static AccountRepository accountRepository = new AccountRepository();
    private final static TransactionRepository transactionRepository = new TransactionRepository();

    private final static TransactionService transactionService = new TransactionServiceImpl(transactionRepository);
    private final static AccountService accountService = new AccountServiceImpl(accountRepository, transactionRepository);

    private static final int MIN_ACCOUNTS = 50;
    private static final int MAX_ACCOUNTS = 100;

    private static final int MIN_BALANCE = 200;
    private static final int MAX_BALANCE = 500;

    private static final int MIN_THREADS = 5;
    private static final int MAX_THREADS = 10;

    private static final int MIN_TRANSFERS_PER_THREAD = 300;
    private static final int MAX_TRANSFERS_PER_THREAD = 500;

    private static final int MAX_TRANSFER_AMOUNT = 50;

    private static int totalAccounts;
    private static BigDecimal initialTotalBalance = ZERO;
    private static Map<Long, BigDecimal> initialAmounts = new HashMap<>();

    @BeforeClass
    public static void setUp() throws Exception {
        generateAccounts();
        generateSimultaneousTransfers();
    }

    @Test
    public void testInitialBalanceSumStaysConstant() {
        List<Account> accounts = accountService.findAll();

        BigDecimal sum = accounts.stream()
                .map(Account::getMoney)
                .map(Money::getAmount)
                .reduce(ZERO, BigDecimal::add);
        logger.info("Total sum is {}", sum);

        assertEquals(0, initialTotalBalance.compareTo(sum));
    }

    @Test
    public void testLowestAmountIsAboveZero() {
        List<Account> accounts = accountService.findAll();

        BigDecimal minBalance = accounts.stream()
                .map(Account::getMoney)
                .map(Money::getAmount)
                .min(BigDecimal::compareTo).orElse(ZERO);
        logger.info("Min balance is {}", minBalance);

        assertTrue(minBalance.compareTo(ZERO) >= 0);
    }

    @Test
    public void testAllTransfersTookPlace() {
        List<Account> accounts = accountService.findAll();

        for (Account account : accounts) {
            long accountId = account.getId();
            BigDecimal expected = initialAmounts.get(accountId);

            List<Transaction> transactions = transactionService.findByAccountId(accountId);
            for (Transaction transaction : transactions) {
                BigDecimal transferAmount = transaction.getMoney().getAmount();
                if (transaction.getFrom() == accountId) {
                    expected = expected.subtract(transferAmount);
                }
                if (transaction.getTo() == accountId) {
                    expected = expected.add(transferAmount);
                }
            }
            assertEquals(0, expected.subtract(account.getMoney().getAmount()).compareTo(ZERO));
        }
    }

    @Test
    public void testTransferAmount() throws Exception {
        BigDecimal fromAmount = new BigDecimal("12.34");
        BigDecimal toAmount = new BigDecimal("56.78");
        long fromId = accountService.create(new Account(Money.rubles(fromAmount))).getId();
        long toId = accountService.create(new Account(Money.rubles(toAmount))).getId();

        BigDecimal transferAmount = new BigDecimal("11.11");
        accountService.transfer(fromId, toId, Money.rubles(transferAmount));

        Account from = accountService.find(fromId);
        Account to = accountService.find(toId);

        Assert.assertEquals(0, fromAmount.subtract(transferAmount).compareTo(from.getMoney().getAmount()));
        Assert.assertEquals(0, toAmount.add(transferAmount).compareTo(to.getMoney().getAmount()));
    }

    @Test
    public void testCurrencyConversion() throws Exception {
        BigDecimal fromAmount = new BigDecimal("12000.34");
        BigDecimal toAmount = new BigDecimal("56123.78");
        long fromId = accountService.create(new Account(Money.rubles(fromAmount))).getId();
        long toId = accountService.create(new Account(Money.rubles(toAmount))).getId();

        Money transferMoney = Money.euros(new BigDecimal("4.15"));
        accountService.transfer(fromId, toId, transferMoney);

        Account from = accountService.find(fromId);
        Account to = accountService.find(toId);

        Assert.assertEquals(0, fromAmount.subtract(convert(transferMoney, RUB)).compareTo(from.getMoney().getAmount()));
        Assert.assertEquals(0, toAmount.add(convert(transferMoney, RUB)).compareTo(to.getMoney().getAmount()));

    }

    @Test(expected = InsufficientBalanceException.class)
    public void testInsufficientBalance() throws Exception {
        BigDecimal fromAmount = new BigDecimal("12.34");
        BigDecimal toAmount = new BigDecimal("56.78");
        long fromId = accountService.create(new Account(Money.rubles(fromAmount))).getId();
        long toId = accountService.create(new Account(Money.rubles(toAmount))).getId();

        BigDecimal transferAmount = new BigDecimal("9000.00");
        accountService.transfer(fromId, toId, Money.rubles(transferAmount));
    }

    private static void generateAccounts() throws Exception {
        totalAccounts = ThreadLocalRandom.current().nextInt(MIN_ACCOUNTS, MAX_ACCOUNTS);
        for (int i = 0; i < totalAccounts; i++) {
            double balance = ThreadLocalRandom.current().nextDouble(MIN_BALANCE, MAX_BALANCE);
            BigDecimal bd = BigDecimal.valueOf(balance);
            bd = bd.setScale(2, RoundingMode.HALF_EVEN);
            Account account = accountService.create(new Account(Money.rubles(bd)));

            initialTotalBalance = initialTotalBalance.add(bd);
            initialAmounts.put(account.getId(), account.getMoney().getAmount());
        }
    }

    private static void generateSimultaneousTransfers() throws Exception {
        int numOfThreads = ThreadLocalRandom.current().nextInt(MIN_THREADS, MAX_THREADS);
        ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads);

        Runnable transferRoutine = generateTransferRoutine();
        List<Future<?>> results = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++) {
            results.add(threadPool.submit(transferRoutine));
        }

        for (Future<?> result : results) {
            result.get();
        }
    }

    private static Runnable generateTransferRoutine() {
        return () -> {
            int transfers = ThreadLocalRandom.current().nextInt(MIN_TRANSFERS_PER_THREAD, MAX_TRANSFERS_PER_THREAD);
            for (int i = 0; i < transfers; i++) {
                long from = ThreadLocalRandom.current().nextLong(1, totalAccounts);
                long to = ThreadLocalRandom.current().nextLong(1, totalAccounts);
                BigDecimal transferAmount = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(MAX_TRANSFER_AMOUNT));
                transferAmount = transferAmount.setScale(2, RoundingMode.HALF_EVEN);
                try {
                    accountService.transfer(from, to, Money.rubles(transferAmount));
                } catch (InsufficientBalanceException | UnknownAccountException | NegativeFundsException ignore) {
                    // valid cases
                }
            }
        };
    }
}