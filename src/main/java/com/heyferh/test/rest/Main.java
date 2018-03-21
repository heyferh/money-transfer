package com.heyferh.test.rest;

import com.heyferh.test.repository.AccountRepository;
import com.heyferh.test.repository.TransactionRepository;
import com.heyferh.test.rest.endpoint.AccountEndpoint;
import com.heyferh.test.rest.endpoint.TransactionEndpoint;
import com.heyferh.test.service.api.AccountService;
import com.heyferh.test.service.api.TransactionService;
import com.heyferh.test.service.impl.AccountServiceImpl;
import com.heyferh.test.service.impl.TransactionServiceImpl;

public class Main {
    public static void main(String[] args) {

        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        AccountService accountService = new AccountServiceImpl(accountRepository, transactionRepository);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository);

        AccountEndpoint accountEndpoint = new AccountEndpoint(accountService);
        TransactionEndpoint transactionEndpoint = new TransactionEndpoint(transactionService, accountService);

        RestContext restContext = new RestContext();

        restContext.addEndpoint(accountEndpoint);
        restContext.addEndpoint(transactionEndpoint);

        restContext.init();
    }
}
