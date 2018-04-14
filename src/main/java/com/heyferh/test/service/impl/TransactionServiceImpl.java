package com.heyferh.test.service.impl;

import com.heyferh.test.entity.TransactionEntity;
import com.heyferh.test.model.Money;
import com.heyferh.test.model.Transaction;
import com.heyferh.test.repository.TransactionRepository;
import com.heyferh.test.service.api.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> findByAccountId(long id) {
        List<TransactionEntity> transactionEntities = transactionRepository.findByAccountId(id);
        return transactionEntities.stream().map(transactionEntity ->
                new Transaction(transactionEntity.getDateTime(), transactionEntity.getFrom(), transactionEntity.getTo(),
                        new Money(
                                transactionEntity.getMoney().getAmount(),
                                transactionEntity.getMoney().getCurrency())))
                .collect(Collectors.toList());
    }
}
