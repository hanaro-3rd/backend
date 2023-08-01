package com.example.travelhana.Domain;

public enum Currency {
    JPY("JPY", 100), // 일본 엔 환율
    EUR("EUR",1), // 유럽 유로 환율
    USD("USD", 1); // 미국 달러 환율


    private final String code;
    private final int baseCurrency;

    Currency(String code, int baseCurrency) {
        this.code = code;
        this.baseCurrency = baseCurrency;
    }

    public String getCode() {
        return code;
    }

    public int getBaseCurrency() {
        return baseCurrency;
    }

    public static Currency getByCode(String code) {
        for (Currency currency : values()) {
            if (currency.getCode().equals(code)) {
                return currency;
            }
        }
        return null;
    }
}
