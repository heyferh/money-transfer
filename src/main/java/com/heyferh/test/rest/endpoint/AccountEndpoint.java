package com.heyferh.test.rest.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyferh.test.model.Account;
import com.heyferh.test.service.api.AccountService;
import com.heyferh.test.util.NegativeFundsException;
import spark.Service;

import java.util.List;

public class AccountEndpoint implements EndpointConfigurer {

    private final AccountService accountService;

    private final ObjectMapper objectMapper;

    public AccountEndpoint(AccountService accountService) {
        this.accountService = accountService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void configure(Service spark) {
        spark.post("/account", (req, res) -> {
            Account account = objectMapper.readValue(req.body(), Account.class);
            try {
                Account created = accountService.create(account);
                return objectMapper.writeValueAsString(created);
            } catch (NegativeFundsException e) {
                res.status(400);
                return "{\"message\": \"Negative balance isn't allowed. " +
                        "Found: " + e.getMoney() + "\"}";
            }
        });
        spark.get("/account", (req, res) -> {
            List<Account> accounts = accountService.findAll();
            return objectMapper.writeValueAsString(accounts);
        });
        spark.get("/account/:id", (req, res) -> {
            String id = req.params("id");
            Account account = accountService.find(Long.parseLong(id));
            return objectMapper.writeValueAsString(account);
        });
    }
}
