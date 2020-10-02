package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class AvatarResponse {

    @SerializedName("status")
    private Integer status;

    @SerializedName("errorMessage")
    private String errorMessage;

    @SerializedName("message")
    private String message;

    @SerializedName("avatar")
    private String avatar;

    public AvatarResponse() {}

    public AvatarResponse(Integer status, String errorMessage, String message, String avatar) {
        this.status = status;
        this.errorMessage = errorMessage;
        this.message = message;
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getAvatar() {
        return avatar;
    }
}
