package com.heyferh.test.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyferh.test.SpringConfig;
import com.heyferh.test.model.Account;
import com.heyferh.test.model.Money;
import com.heyferh.test.model.Transaction;
import com.heyferh.test.model.Transfer;
import com.heyferh.test.rest.RestContext;
import com.heyferh.test.rest.endpoint.AccountEndpoint;
import com.heyferh.test.rest.endpoint.TransactionEndpoint;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
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

import static java.math.BigDecimal.ZERO;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class TransferTest {

    private final static Logger logger = LoggerFactory.getLogger(TransferTest.class);

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

    private static RestContext restContext;
    private static ObjectMapper objectMapper;

    private static final String TRANSFER_URL = "http://localhost:4567/transfer";
    private static final String TRANSFERS_BY_ACCOUNT_URL = "http://localhost:4567/transfer/{id}";
    private static final String ACCOUNT_URL = "http://localhost:4567/account";

    @BeforeClass
    public static void setUp() throws Exception {

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);

        restContext = new RestContext();
        restContext.addEndpoint(ctx.getBean(AccountEndpoint.class));
        restContext.addEndpoint(ctx.getBean(TransactionEndpoint.class));
        restContext.init();

        objectMapper = ctx.getBean(ObjectMapper.class);

        generateAccounts();
        generateSimultaneousTransfers();
    }

    @AfterClass
    public static void tearDown() {
        restContext.stop();
    }

    @Test
    public void testInitialBalanceSumStaysConstant() throws Exception {
        BigDecimal sum = getAccounts().stream()
                .map(Account::getMoney)
                .map(Money::getAmount)
                .reduce(ZERO, BigDecimal::add);
        assertEquals(0, initialTotalBalance.compareTo(sum));
    }

    @Test
    public void testLowestAmountIsAboveZero() throws Exception {
        BigDecimal minBalance = getAccounts().stream()
                .map(Account::getMoney)
                .map(Money::getAmount)
                .min(BigDecimal::compareTo).orElse(ZERO);
        assertTrue(minBalance.compareTo(ZERO) >= 0);
    }

    @Test
    public void testAllTransfersTookPlace() throws Exception {
        for (Account account : getAccounts()) {
            long accountId = account.getId();
            BigDecimal expected = initialAmounts.get(accountId);

            String body = Unirest.get(TRANSFERS_BY_ACCOUNT_URL)
                    .routeParam("id", String.valueOf(accountId))
                    .asString().getBody();
            List<Transaction> transactions = objectMapper.readValue(body, new TypeReference<List<Transaction>>() {
            });
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

    private static void generateAccounts() throws Exception {
        totalAccounts = ThreadLocalRandom.current().nextInt(MIN_ACCOUNTS, MAX_ACCOUNTS);
        for (int i = 0; i < totalAccounts; i++) {
            double balance = ThreadLocalRandom.current().nextDouble(MIN_BALANCE, MAX_BALANCE);
            BigDecimal bd = BigDecimal.valueOf(balance);
            bd = bd.setScale(2, RoundingMode.HALF_EVEN);
            String body = Unirest.post(ACCOUNT_URL)
                    .body(objectMapper.writeValueAsString(new Account(Money.rubles(bd))))
                    .asString().getBody();
            Account account = objectMapper.readValue(body, Account.class);
            initialTotalBalance = initialTotalBalance.add(account.getMoney().getAmount());
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
                    Unirest.post(TRANSFER_URL)
                            .body(objectMapper.writeValueAsString(new Transfer(from, to, Money.rubles(transferAmount))))
                            .asString().getBody();
                } catch (JsonProcessingException | UnirestException e) {
                    logger.error("Error: ", e);
                }
            }
        };
    }

    private List<Account> getAccounts() throws UnirestException, IOException {
        String body = Unirest.get(ACCOUNT_URL)
                .asString().getBody();
        return objectMapper.readValue(body, new TypeReference<List<Account>>() {
        });
    }
}