package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.takemeaway.takemeaway.datamodels.SingleDestination;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class DestinationsResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private List<SingleDestination> destinations = new ArrayList<>();

    @SerializedName("errorMessage")
    private String errorMessage;

    public DestinationsResponse(Integer status, List<SingleDestination> destinations, String errorMessage) {
        this.status = status;
        this.destinations = destinations;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SingleDestination> getDestinations() {
        return destinations;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
