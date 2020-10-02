package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class OtherCurrencyBlock implements Serializable {

    @SerializedName("price")
    private Float roomDescription;

    @SerializedName("currency")
    private String currency;

    public OtherCurrencyBlock(Float roomDescription, String currency) {
        this.roomDescription = roomDescription;
        this.currency = currency;
    }

    public Float getRoomDescription() {
        return roomDescription;
    }

    public String getCurrency() {
        return currency;
    }
}
