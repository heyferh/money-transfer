package com.heyferh.test.model;

import java.time.LocalDateTime;

public class Transaction {

    private long from;
    private long to;
    private LocalDateTime date;
    private Money money;

    public Transaction(LocalDateTime date, long from, long to, Money money) {
        this.date = date;
        this.from = from;
        this.to = to;
        this.money = money;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
