package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SingleAirport implements Serializable {

    @SerializedName("status")
    private int status;

    @SerializedName("iata")
    private String iata;

    @SerializedName("name")
    private String name;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("lat")
    private float lat;

    @SerializedName("lon")
    private float lon;

    @SerializedName("errorMessage")
    private String errorMessage;


    public SingleAirport() {
    }

    public SingleAirport(String iata, String name, String city) {
        this.iata = iata;
        this.name = name;
        this.city = city;
    }

    public SingleAirport(int status, String iata, String name, String city, String country, float lat, float lon, String errorMessage) {
        this.status = status;
        this.iata = iata;
        this.name = name;
        this.city = city;
        this.country = country;
        this.lat = lat;
        this.lon = lon;
        this.errorMessage = errorMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getIata() {
        return iata;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
