package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SingleHotelAvailability {

    @SerializedName("hotel_id")
    private Integer hotelId;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private float price;

    @SerializedName("hotel_currency_code")
    private String currencyCode;

    @SerializedName("rooms")
    private List<SingleRoom> rooms;

    public SingleHotelAvailability(Integer hotelId, String name, float price, String currencyCode, List<SingleRoom> rooms) {
        this.hotelId = hotelId;
        this.name = name;
        this.price = price;
        this.currencyCode = currencyCode;
        this.rooms = rooms;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public List<SingleRoom> getRooms() {
        return rooms;
    }
}
