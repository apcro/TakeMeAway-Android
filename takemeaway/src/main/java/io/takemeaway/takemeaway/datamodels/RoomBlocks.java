package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class RoomBlocks implements Serializable {

    @SerializedName("data")
    private List<SingleHotel> hotels = new ArrayList<>();

}
