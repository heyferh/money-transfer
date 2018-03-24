package com.heyferh.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heyferh.test.repository.AccountRepository;
import com.heyferh.test.repository.TransactionRepository;
import com.heyferh.test.service.api.AccountService;
import com.heyferh.test.service.api.TransactionService;
import com.heyferh.test.service.impl.AccountServiceImpl;
import com.heyferh.test.service.impl.TransactionServiceImpl;
import dagger.Module;
import dagger.Provides;

@Module
public class AppComponentModule {

    @Provides
    public AccountService accountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        return new AccountServiceImpl(accountRepository, transactionRepository);
    }

    @Provides
    public TransactionService transactionService(TransactionRepository transactionRepository) {
        return new TransactionServiceImpl(transactionRepository);
    }

    @Provides
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
