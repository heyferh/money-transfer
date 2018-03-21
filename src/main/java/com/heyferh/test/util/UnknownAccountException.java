package com.heyferh.test.util;

public class UnknownAccountException extends Exception {
    private long id;

    public UnknownAccountException(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "UnknownAccountException{" +
                "id=" + id +
                '}';
    }
}
