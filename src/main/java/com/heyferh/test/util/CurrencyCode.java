package com.heyferh.test.util;

public enum CurrencyCode {
    USD(2), EUR(2), RUB(2);

    private int fractionDigits;

    CurrencyCode(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public int getFractionDigits() {
        return fractionDigits;
    }
}