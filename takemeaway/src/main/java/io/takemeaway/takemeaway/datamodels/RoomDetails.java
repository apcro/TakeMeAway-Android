package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class RoomDetails implements Serializable {

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private ArrayList<SingleRoom> rooms;

    public RoomDetails(Integer status, ArrayList<SingleRoom> rooms) {
        this.status = status;
        this.rooms = rooms;
    }

    public Integer getStatus() {
        return status;
    }

    public ArrayList<SingleRoom> getRooms() {
        return rooms;
    }
}
