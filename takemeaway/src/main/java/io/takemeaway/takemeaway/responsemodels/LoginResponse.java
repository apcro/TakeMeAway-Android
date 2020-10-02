package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class LoginResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("token")
    private String token;

    @SerializedName("error")
    private String error;

    public LoginResponse(Integer status, String token, String error) {

        this.status = status;
        this.token = token;
        this.error = error;
    }

    public Integer getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public String getError() {
        return error;
    }
}
