package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class HotelDetails implements Serializable {

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private SingleHotel hotel = new SingleHotel();

    public HotelDetails(Integer status, SingleHotel hotel) {
        this.status = status;
        this.hotel = hotel;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public SingleHotel getHotel() {
        return hotel;
    }

}
