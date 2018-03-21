package com.heyferh.test.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "accounts")
public class AccountEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private MoneyEntity moneyEntity;

    public AccountEntity() {
    }

    public AccountEntity(MoneyEntity moneyEntity) {
        this.moneyEntity = moneyEntity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MoneyEntity getMoneyEntity() {
        return moneyEntity;
    }

    public void setMoneyEntity(MoneyEntity moneyEntity) {
        this.moneyEntity = moneyEntity;
    }
}
