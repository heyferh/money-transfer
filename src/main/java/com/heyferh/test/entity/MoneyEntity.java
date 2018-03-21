package com.heyferh.test.entity;

import com.heyferh.test.util.CurrencyCode;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class MoneyEntity {

    @Column(name = "amount", precision = 19, scale = 4)
    private BigDecimal amount;
    @Column(name = "currency")
    private CurrencyCode currency;

    public MoneyEntity() {
    }

    public MoneyEntity(BigDecimal amount, CurrencyCode currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CurrencyCode getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyCode currency) {
        this.currency = currency;
    }
}
