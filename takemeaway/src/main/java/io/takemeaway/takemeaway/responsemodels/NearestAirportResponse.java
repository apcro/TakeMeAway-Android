package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.takemeaway.takemeaway.datamodels.SingleAirport;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class NearestAirportResponse implements Serializable {

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private SingleAirport airport = new SingleAirport();

    public NearestAirportResponse(Integer status, SingleAirport airport) {
        this.status = status;
        this.airport = airport;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public SingleAirport getAirport() {
        return airport;
    }

}
