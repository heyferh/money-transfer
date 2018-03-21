package com.heyferh.test.model;

public class Account {

    private long id;
    private Money money;

    public Account() {
    }

    public Account(long id, Money money) {
        this.id = id;
        this.money = money;
    }

    public Account(Money money) {
        this.money = money;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }
}
