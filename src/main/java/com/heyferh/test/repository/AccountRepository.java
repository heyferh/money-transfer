package com.heyferh.test.repository;

import com.heyferh.test.entity.AccountEntity;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static com.heyferh.test.util.EntityManagerHelper.beginTransaction;
import static com.heyferh.test.util.EntityManagerHelper.closeEntityManager;
import static com.heyferh.test.util.EntityManagerHelper.commitTransaction;
import static com.heyferh.test.util.EntityManagerHelper.getEntityManager;

public class AccountRepository {

    @Inject
    public AccountRepository() {
    }

    public AccountEntity create(AccountEntity accountEntity) {
        beginTransaction();
        getEntityManager().persist(accountEntity);
        commitTransaction();
        return accountEntity;
    }

    public AccountEntity find(long id) {
        AccountEntity accountEntity = getEntityManager().find(AccountEntity.class, id);
        closeEntityManager();
        return accountEntity;
    }

    public List<AccountEntity> findAll() {
        List<AccountEntity> accountEntities = getEntityManager().createQuery("select a from AccountEntity a", AccountEntity.class).getResultList();
        closeEntityManager();
        return accountEntities;
    }

    public void deposit(AccountEntity accountEntity, BigDecimal amount) {
        BigDecimal currentAmount = accountEntity.getMoneyEntity().getAmount();
        accountEntity.getMoneyEntity().setAmount(currentAmount.add(amount));
        getEntityManager().merge(accountEntity);
    }

    public void withdraw(AccountEntity accountEntity, BigDecimal amount) {
        BigDecimal currentAmount = accountEntity.getMoneyEntity().getAmount();
        accountEntity.getMoneyEntity().setAmount(currentAmount.subtract(amount));
        getEntityManager().merge(accountEntity);
    }
}
