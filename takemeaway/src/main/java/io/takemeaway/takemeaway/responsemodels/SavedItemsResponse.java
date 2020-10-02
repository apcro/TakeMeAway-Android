package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import io.takemeaway.takemeaway.datamodels.SavedItem;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SavedItemsResponse implements Serializable {

    public static String preferencesKey = "SavedItemsResponse";

    @SerializedName("status")
    private Integer status;

    @SerializedName("errorMessage")
    private String errorMessage;

    @SerializedName("data")
    private ArrayList<SavedItem> saveditems = new ArrayList<>();

    public SavedItemsResponse(Integer status, ArrayList<SavedItem> items) {
        this.status = status;
        this.saveditems = items;
    }

    public Integer getStatus() {
        return status;
    }

    public ArrayList<SavedItem> getSaveditems() {
        return saveditems;
    }

}
