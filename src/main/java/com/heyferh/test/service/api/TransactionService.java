package com.heyferh.test.service.api;

import com.heyferh.test.model.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> findByAccountId(long id);
}
