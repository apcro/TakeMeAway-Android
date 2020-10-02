package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class HotelAvailabilities {

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private List<SingleHotelAvailability> hotels = new ArrayList<>();

    public HotelAvailabilities(Integer status, List<SingleHotelAvailability> hotels) {
        this.status = status;
        this.hotels = hotels;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SingleHotelAvailability> getHotels() {
        return hotels;
    }

    public void setHotels(List<SingleHotelAvailability> hotels) {
        this.hotels = hotels;
    }

}
