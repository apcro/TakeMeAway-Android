package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class Airports implements Serializable {

    public static String preferencesKey = "Airports";

    @SerializedName("status")
    private Integer status;

    @SerializedName("data")
    private ArrayList<SingleAirport> airports = new ArrayList<>();

    public Airports(Integer status, ArrayList<SingleAirport> airports) {
        this.status = status;
        this.airports = airports;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ArrayList<SingleAirport> getAirports() {
        return airports;
    }

}
