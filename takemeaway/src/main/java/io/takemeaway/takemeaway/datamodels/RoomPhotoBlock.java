package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class RoomPhotoBlock implements Serializable {

    @SerializedName("url_original")
    private String urlOriginal;

    @SerializedName("url_square60")
    private String urlSquare60;

    @SerializedName("photo_id")
    private String photoId;

    @SerializedName("url_max300")
    private String urlMax300;

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public String getUrlSquare60() {
        return urlSquare60;
    }

    public String getPhotoId() {
        return photoId;
    }

    public String getUrlMax300() {
        return urlMax300;
    }
}
