package com.heyferh.test.util;

import com.heyferh.test.model.Money;

public class InsufficientBalanceException extends Exception {
    private Money actual;
    private Money required;

    public InsufficientBalanceException(Money actual, Money required) {
        this.actual = actual;
        this.required = required;
    }

    public Money getActual() {
        return actual;
    }

    public Money getRequired() {
        return required;
    }

    @Override
    public String toString() {
        return "InsufficientBalanceException{" +
                "actual=" + actual +
                ", required=" + required +
                '}';
    }
}
