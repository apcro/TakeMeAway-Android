package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class Hotels {

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private List<SingleHotel> hotels = new ArrayList<>();

    public Hotels(Integer status, List<SingleHotel> hotels) {
        this.status = status;
        this.hotels = hotels;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SingleHotel> getHotels() {
        return hotels;
    }

    public void setHotels(List<SingleHotel> hotels) {
        this.hotels = hotels;
    }

}
