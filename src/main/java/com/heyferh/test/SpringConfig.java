package com.heyferh.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heyferh.test.repository.AccountRepository;
import com.heyferh.test.repository.TransactionRepository;
import com.heyferh.test.rest.endpoint.AccountEndpoint;
import com.heyferh.test.rest.endpoint.TransactionEndpoint;
import com.heyferh.test.service.api.AccountService;
import com.heyferh.test.service.api.TransactionService;
import com.heyferh.test.service.impl.AccountServiceImpl;
import com.heyferh.test.service.impl.TransactionServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public TransactionRepository transactionRepository() {
        return new TransactionRepository();
    }

    @Bean
    public AccountRepository accountRepository() {
        return new AccountRepository();
    }

    @Bean
    public AccountService accountService() {
        return new AccountServiceImpl();
    }

    @Bean
    public TransactionService transactionService() {
        return new TransactionServiceImpl();
    }

    @Bean
    public AccountEndpoint accountEndpoint() {
        return new AccountEndpoint();
    }

    @Bean
    public TransactionEndpoint transactionEndpoint() {
        return new TransactionEndpoint();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
