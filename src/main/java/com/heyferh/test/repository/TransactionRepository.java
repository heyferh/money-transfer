package com.heyferh.test.repository;

import com.heyferh.test.entity.TransactionEntity;

import java.util.List;

import static com.heyferh.test.util.EntityManagerHelper.closeEntityManager;
import static com.heyferh.test.util.EntityManagerHelper.getEntityManager;

public class TransactionRepository {

    public void create(TransactionEntity transactionEntity) {
        getEntityManager().persist(transactionEntity);
    }

    public List<TransactionEntity> findByAccountId(long id) {
        List<TransactionEntity> transactionEntities = getEntityManager()
                .createQuery("select t from TransactionEntity t where t.from = :id or t.to = :id", TransactionEntity.class)
                .setParameter("id", id)
                .getResultList();
        closeEntityManager();
        return transactionEntities;
    }
}
