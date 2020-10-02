package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import io.takemeaway.takemeaway.datamodels.SingleDestination;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class CityResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("city")
    private SingleDestination destination;

    @SerializedName("errorMessage")
    private String errorMessage;

    public CityResponse(Integer status, SingleDestination destination, String errorMessage) {
        this.status = status;
        this.destination = destination;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public SingleDestination getDestination() {
        return destination;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
