package com.heyferh.test.util;

import com.heyferh.test.model.Money;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.heyferh.test.util.CurrencyCode.EUR;
import static com.heyferh.test.util.CurrencyCode.RUB;
import static com.heyferh.test.util.CurrencyCode.USD;
import static java.math.BigDecimal.ROUND_HALF_EVEN;

public class MockCurrencyConverter {

    private static Map<CurrencyCode, Map<CurrencyCode, BigDecimal>> rates;

    static {
        rates = new HashMap<>();

        Map<CurrencyCode, BigDecimal> rubleRates = new HashMap<>();
        rubleRates.put(RUB, BigDecimal.ONE);
        rubleRates.put(USD, BigDecimal.valueOf(0.01739));
        rubleRates.put(EUR, BigDecimal.valueOf(0.01414));

        Map<CurrencyCode, BigDecimal> dollarRates = new HashMap<>();
        dollarRates.put(RUB, BigDecimal.valueOf(57.5041));
        dollarRates.put(USD, BigDecimal.ONE);
        dollarRates.put(EUR, BigDecimal.valueOf(0.81338));

        Map<CurrencyCode, BigDecimal> euroRates = new HashMap<>();
        euroRates.put(RUB, BigDecimal.valueOf(70.6979));
        euroRates.put(USD, BigDecimal.valueOf(1.22944));
        euroRates.put(EUR, BigDecimal.ONE);

        rates.put(RUB, rubleRates);
        rates.put(USD, dollarRates);
        rates.put(EUR, euroRates);
    }

    public static BigDecimal convert(Money money, CurrencyCode currency) {
        BigDecimal rate = rates.get(money.getCurrency()).get(currency);
        return money.getAmount()
                .multiply(rate)
                .setScale(currency.getFractionDigits(), ROUND_HALF_EVEN);
    }
}
