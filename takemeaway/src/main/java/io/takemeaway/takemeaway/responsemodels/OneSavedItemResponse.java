package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

import io.takemeaway.takemeaway.datamodels.SavedItem;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class OneSavedItemResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("errorMessage")
    private String errorMessage;

    @SerializedName("data")
    private SavedItem item;

    public OneSavedItemResponse(Integer status, String errorMessage, SavedItem item) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.item = item;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public SavedItem getItem() {
        return item;
    }
}