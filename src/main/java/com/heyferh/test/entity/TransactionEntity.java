package com.heyferh.test.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "from_id")
    private long from;

    @Column(name = "to_id")
    private long to;

    @Embedded
    private MoneyEntity money;

    @PrePersist
    public void prePersist() {
        this.dateTime = LocalDateTime.now();
    }

    public TransactionEntity() {
    }

    public TransactionEntity(long from, long to, MoneyEntity money) {
        this.from = from;
        this.to = to;
        this.money = money;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
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

    public MoneyEntity getMoney() {
        return money;
    }

    public void setMoney(MoneyEntity moneyEntity) {
        this.money = moneyEntity;
    }
}
