package com.heyferh.test.model;

import com.heyferh.test.util.CurrencyCode;

import java.math.BigDecimal;

import static java.math.BigDecimal.valueOf;


public class Money {

    private BigDecimal amount;
    private CurrencyCode currency;

    public Money() {
    }

    public Money(long amount, CurrencyCode currency) {
        this.currency = currency;
        this.amount = valueOf(amount);
    }

    public Money(BigDecimal amount, CurrencyCode currency) {
        this.currency = currency;
        this.amount = amount;
    }

    public static Money rubles(BigDecimal amount) {
        return new Money(amount, CurrencyCode.RUB);
    }

    public static Money dollars(BigDecimal amount) {
        return new Money(amount, CurrencyCode.USD);
    }

    public static Money euros(BigDecimal amount) {
        return new Money(amount, CurrencyCode.EUR);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return currency + " " + amount;
    }
}
