package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class RoomPriceBlock implements Serializable {

    @SerializedName("currency")
    private String currency;

    @SerializedName("other_currency")
    private OtherCurrencyBlock otherCurrency;

    @SerializedName("price")
    private Float price;

    public RoomPriceBlock(String currency, OtherCurrencyBlock otherCurrency, Float price) {
        this.currency = currency;
        this.otherCurrency = otherCurrency;
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public OtherCurrencyBlock getOtherCurrency() {
        return otherCurrency;
    }

    public Float getPrice() {
        return price;
    }
}
