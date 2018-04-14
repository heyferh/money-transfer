package com.heyferh.test.model;

public class Transfer {

    private long fromId;
    private long toId;
    private Money money;

    public Transfer() {
    }

    public Transfer(long fromId, long toId, Money money) {
        this.fromId = fromId;
        this.toId = toId;
        this.money = money;
    }

    public long getFromId() {
        return fromId;
    }

    public void setFromId(long fromId) {
        this.fromId = fromId;
    }

    public long getToId() {
        return toId;
    }

    public void setToId(long toId) {
        this.toId = toId;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
    }
}
