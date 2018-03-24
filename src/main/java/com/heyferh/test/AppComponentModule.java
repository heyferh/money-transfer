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

import javax.inject.Singleton;

@Module
public class AppComponentModule {

    @Provides
    @Singleton
    public AccountRepository provideAccountRepository() {
        return new AccountRepository();
    }

    @Provides
    @Singleton
    public TransactionRepository provideTransactionRepository() {
        return new TransactionRepository();
    }

    @Provides
    @Singleton
    public AccountService provideAccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        return new AccountServiceImpl(accountRepository, transactionRepository);
    }

    @Provides
    @Singleton
    public TransactionService provideTransactionService(TransactionRepository transactionRepository) {
        return new TransactionServiceImpl(transactionRepository);
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
