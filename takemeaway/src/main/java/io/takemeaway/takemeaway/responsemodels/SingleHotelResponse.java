package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

import io.takemeaway.takemeaway.datamodels.SingleHotel;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SingleHotelResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("errorMessage")
    private String errorMessage;

    @SerializedName("data")
    private SingleHotel hotel;

    public SingleHotelResponse(Integer status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    public SingleHotel getData() {
        return hotel;
    }
}
