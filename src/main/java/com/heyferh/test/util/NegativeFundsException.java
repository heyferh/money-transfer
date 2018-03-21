package com.heyferh.test.util;

import com.heyferh.test.model.Money;

public class NegativeFundsException extends Exception {
    private Money money;

    public NegativeFundsException(Money money) {
        this.money = money;
    }

    public Money getMoney() {
        return money;
    }

    @Override
    public String toString() {
        return "NegativeFundsException{" +
                "money=" + money +
                '}';
    }
}
