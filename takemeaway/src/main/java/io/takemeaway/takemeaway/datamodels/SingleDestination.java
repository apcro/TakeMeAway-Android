package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SingleDestination implements Serializable {

    @SerializedName("cityId")
    private Integer cityId;

    @SerializedName("cityName")
    private String cityName;

    @SerializedName("countryName")
    private String countryName;

    @SerializedName("iata")
    private String iata;

    @SerializedName("lat")
    private float lat;

    @SerializedName("lon")
    private float lon;

    @SerializedName("googlePlaceId")
    private String googlePlaceId;

    @SerializedName("cityDescription")
    private String cityDescription;

    @SerializedName("cityLongDescription")
    private String cityLongDescription;

    @SerializedName("cityImage")
    private String cityImage;

    @SerializedName("flightcost")
    private String flightCost;

    @SerializedName("currencycode")
    private String currencyCode;

    @SerializedName("flightref")
    private String flightRefUri;

    @SerializedName("flightcurrency")
    private String flightCurrency;

    @SerializedName("temperature")
    private Integer temperature;

    @SerializedName("weather_description")
    private String weather_description;

    @SerializedName("weather_icon")
    private Integer weather_icon;

    @SerializedName("hotels")
    private ArrayList<SingleHotel> hotels;

    @SerializedName("issaved")
    private boolean issaved;

    public SingleDestination() {}

    public SingleDestination(Integer cityId, String cityName, String countryName, String iata, float lat, float lon, String googlePlaceId, String cityDescription, String cityLongDescription, String cityImage, String flightCost, String currencyCode, String flightRefUri, String flightcurrency, Integer temperature, String weather_description, Integer weather_icon, Boolean issaved) {
        this.cityId = cityId;
        this.cityName = cityName;
        this.countryName = countryName;
        this.iata = iata;
        this.lat = lat;
        this.lon = lon;
        this.googlePlaceId = googlePlaceId;
        this.cityDescription = cityDescription;
        this.cityLongDescription = cityLongDescription;
        this.cityImage = cityImage;
        this.flightCost = flightCost;
        this.currencyCode = currencyCode;
        this.flightRefUri = flightRefUri;
        this.flightCurrency = flightcurrency;
        this.temperature = temperature;
        this.weather_description = weather_description;
        this.weather_icon = weather_icon;
        this.hotels = hotels;
        this.issaved = issaved;
    }

    public Integer getImageId() {
        return cityId;
    }

    public String getCityImage() {
        return cityImage;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getIata() {
        return iata;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getGooglePlaceId() {
        return googlePlaceId;
    }

    public String getCityDescription() {
        return cityDescription;
    }

    public String getCityLongDescription() {
        return cityLongDescription;
    }

    public String getFlightCost() {
        return flightCost;
    }

    public Integer getCityId() {
        return cityId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getFlightRefUri() {
        return flightRefUri;
    }

    public String getFlightCurrency() {
        return flightCurrency;
    }

    public ArrayList<SingleHotel> getHotels() {
        return hotels;
    }

    public Integer getTemperature() {
        if (temperature == null) {
            return -999;
        }
        return temperature;
    }

    public String getWeather_description() {
        if (weather_description == null) {
            return "";
        }
        return weather_description;
    }

    public Integer getWeather_icon() {
        return weather_icon;
    }

    public boolean isSaved() {
        return issaved;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setGooglePlaceId(String googlePlaceId) {
        this.googlePlaceId = googlePlaceId;
    }

    public void setCityDescription(String cityDescription) {
        this.cityDescription = cityDescription;
    }

    public void setCityLongDescription(String cityLongDescription) {
        this.cityLongDescription = cityLongDescription;
    }

    public void setCityImage(String cityImage) {
        this.cityImage = cityImage;
    }

    public void setFlightCost(String flightCost) {
        this.flightCost = flightCost;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setFlightRefUri(String flightRefUri) {
        this.flightRefUri = flightRefUri;
    }

    public void setFlightCurrency(String flightCurrency) {
        this.flightCurrency = flightCurrency;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public void setWeather_description(String weather_description) {
        this.weather_description = weather_description;
    }

    public void setWeather_icon(Integer weather_icon) {
        this.weather_icon = weather_icon;
    }

    public void setHotels(ArrayList<SingleHotel> hotels) {
        this.hotels = hotels;
    }
}
