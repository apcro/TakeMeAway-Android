package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class BaseResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("errorMessage")
    private String errorMessage;

    public BaseResponse() {}

    public BaseResponse(Integer status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }


}
