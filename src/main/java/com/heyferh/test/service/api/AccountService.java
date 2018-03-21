package com.heyferh.test.service.api;

import com.heyferh.test.model.Account;
import com.heyferh.test.model.Money;
import com.heyferh.test.util.InsufficientBalanceException;
import com.heyferh.test.util.NegativeFundsException;
import com.heyferh.test.util.UnknownAccountException;

import java.util.List;

public interface AccountService {

    Account find(long id) throws UnknownAccountException;

    List<Account> findAll();

    Account create(Account account) throws NegativeFundsException;

    void transfer(long fromId, long toId, Money money) throws InsufficientBalanceException, NegativeFundsException, UnknownAccountException;

}
