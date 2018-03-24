package com.heyferh.test.rest.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyferh.test.model.Transaction;
import com.heyferh.test.model.Transfer;
import com.heyferh.test.service.api.AccountService;
import com.heyferh.test.service.api.TransactionService;
import com.heyferh.test.util.InsufficientBalanceException;
import com.heyferh.test.util.NegativeFundsException;
import com.heyferh.test.util.UnknownAccountException;
import spark.Service;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TransactionEndpoint implements EndpointConfigurer {

    private final TransactionService transactionService;
    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    @Inject
    public TransactionEndpoint(TransactionService transactionService, AccountService accountService, ObjectMapper objectMapper) {
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void configure(Service spark) {
        spark.post("/transfer", (req, res) -> {
            Transfer transfer = objectMapper.readValue(req.body(), Transfer.class);
            try {
                accountService.transfer(transfer.getFromId(), transfer.getToId(), transfer.getMoney());
                return objectMapper.writeValueAsString(transfer);
            } catch (InsufficientBalanceException e) {
                res.status(400);
                return "{\"message\": \"Insufficient balance. " +
                        "Found: " + e.getActual() + " Required: " + e.getRequired() + "\"}";
            } catch (UnknownAccountException e) {
                res.status(400);
                return "{\"message\": \"Unknown account. Not found: " + e.getId() + "\"}";
            } catch (NegativeFundsException e) {
                res.status(400);
                return "{\"message\": \"Negative amount. Found: " + e.getMoney() + "\"}";
            }
        });
        spark.get("/transfer/:id", (req, res) -> {
            String id = req.params("id");
            List<Transaction> transactions = transactionService.findByAccountId(Long.parseLong(id));
            return objectMapper.writeValueAsString(transactions);
        });
    }
}
