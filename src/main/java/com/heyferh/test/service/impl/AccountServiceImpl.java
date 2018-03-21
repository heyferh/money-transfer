package com.heyferh.test.service.impl;

import com.heyferh.test.entity.AccountEntity;
import com.heyferh.test.entity.MoneyEntity;
import com.heyferh.test.entity.TransactionEntity;
import com.heyferh.test.model.Account;
import com.heyferh.test.model.Money;
import com.heyferh.test.repository.AccountRepository;
import com.heyferh.test.repository.TransactionRepository;
import com.heyferh.test.service.api.AccountService;
import com.heyferh.test.util.InsufficientBalanceException;
import com.heyferh.test.util.NegativeFundsException;
import com.heyferh.test.util.UnknownAccountException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.heyferh.test.util.EntityManagerHelper.beginTransaction;
import static com.heyferh.test.util.EntityManagerHelper.commitTransaction;
import static com.heyferh.test.util.EntityManagerHelper.getEntityManager;
import static com.heyferh.test.util.EntityManagerHelper.rollbackTransaction;
import static com.heyferh.test.util.MockCurrencyConverter.convert;
import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

public class AccountServiceImpl implements AccountService {

    private final static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Account find(long id) {
        AccountEntity accountEntity = accountRepository.find(id);
        return new Account(id,
                new Money(accountEntity.getMoneyEntity().getAmount(), accountEntity.getMoneyEntity().getCurrency()));
    }

    @Override
    public List<Account> findAll() {
        List<AccountEntity> entities = accountRepository.findAll();
        return entities.stream().map(accountEntity -> new Account(accountEntity.getId(),
                new Money(
                        accountEntity.getMoneyEntity().getAmount(),
                        accountEntity.getMoneyEntity().getCurrency())))
                .collect(Collectors.toList());
    }

    @Override
    public Account create(Account account) throws NegativeFundsException {
        if (account.getMoney().getAmount().compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Attempt to save an invalid account. {}", account.getMoney());
            throw new NegativeFundsException(account.getMoney());
        }
        AccountEntity newAccount = new AccountEntity(
                new MoneyEntity(
                        account.getMoney().getAmount(),
                        account.getMoney().getCurrency()));
        AccountEntity accountEntity = accountRepository.create(newAccount);
        return new Account(accountEntity.getId(),
                new Money(
                        accountEntity.getMoneyEntity().getAmount(),
                        accountEntity.getMoneyEntity().getCurrency()));
    }

    @Override
    public void transfer(long fromId, long toId, Money money) throws InsufficientBalanceException, NegativeFundsException, UnknownAccountException {
        if (money.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Transfer amount is negative. {}", money);
            throw new NegativeFundsException(money);
        }
        if (fromId == toId) {
            logger.error("Attempt to transfer within one account, accountId: {}", fromId);
            return;
        }
        beginTransaction();
        try {
            AccountEntity from;
            AccountEntity to;
            if (fromId < toId) { // ensure lock ordering to avoid deadlock
                from = getEntityManager().find(AccountEntity.class, fromId, PESSIMISTIC_WRITE);
                to = getEntityManager().find(AccountEntity.class, toId, PESSIMISTIC_WRITE);
            } else {
                to = getEntityManager().find(AccountEntity.class, toId, PESSIMISTIC_WRITE);
                from = getEntityManager().find(AccountEntity.class, fromId, PESSIMISTIC_WRITE);
            }
            if (from == null) {
                throw new UnknownAccountException(fromId);
            } else if (to == null) {
                throw new UnknownAccountException(toId);
            } else {
                doTransfer(from, to, money);
                commitTransaction();
            }
        } finally {
            if (getEntityManager().getTransaction().isActive()) {
                rollbackTransaction();
            }
        }
    }

    private void doTransfer(AccountEntity from, AccountEntity to, Money transfer) throws InsufficientBalanceException {
        checkBalance(from, transfer);

        accountRepository.withdraw(from, convert(transfer, from.getMoneyEntity().getCurrency()));
        accountRepository.deposit(to, convert(transfer, to.getMoneyEntity().getCurrency()));

        TransactionEntity transaction = new TransactionEntity(from.getId(), to.getId(),
                new MoneyEntity(transfer.getAmount(), transfer.getCurrency()));
        transactionRepository.create(transaction);
    }

    private void checkBalance(AccountEntity fromEntity, Money transferredMoney) throws InsufficientBalanceException {
        Money fromMoney = new Money(fromEntity.getMoneyEntity().getAmount(), fromEntity.getMoneyEntity().getCurrency());
        BigDecimal convertedTransferAmount = convert(transferredMoney, fromMoney.getCurrency());

        if (fromMoney.getAmount().subtract(convertedTransferAmount).compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Transaction rolled back due to InsufficientBalanceException - actual: {}, required: {}",
                    fromMoney, transferredMoney);
            rollbackTransaction();
            throw new InsufficientBalanceException(fromMoney, transferredMoney);
        }
    }
}
