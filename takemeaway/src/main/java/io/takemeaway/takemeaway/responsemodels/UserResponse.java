package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.takemeaway.takemeaway.datamodels.CurrentUser;
import io.takemeaway.takemeaway.datamodels.UserDetails;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class UserResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("userdata")
    private UserDetails userdata;

    public UserResponse(Integer status, List<CurrentUser> user) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public UserDetails getUserdata() {
        return userdata;
    }
}
