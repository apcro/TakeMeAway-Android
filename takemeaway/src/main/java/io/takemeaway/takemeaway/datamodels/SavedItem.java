package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import io.takemeaway.takemeaway.application.TakeMeAway;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SavedItem implements Serializable {

    @SerializedName("cityId")
    private int cityId;

    @SerializedName("cityname")
    private String cityname;

    @SerializedName("country")
    private String country;

    @SerializedName("cityimage")
    private String cityimage;

    @SerializedName("airportLat")
    private float airportLat;

    @SerializedName("airportLon")
    private float airportLon;

    @SerializedName("hotelname")
    private String hotelname;

    @SerializedName("hotelImage")
    private String hotelimage;

    @SerializedName("hotelid")
    private int hotelid;

    @SerializedName("hotelLat")
    private float hotelLat;

    @SerializedName("hotelLon")
    private float hotelLon;

    @SerializedName("hotelprice")
    private float hotelprice;

    @SerializedName("flightprice")
    private float flightprice;

    @SerializedName("startdate")
    private int startdate;

    @SerializedName("enddate")
    private int enddate;

    @SerializedName("people")
    private float people;

    @SerializedName("savedon")
    private int savedon;

    @SerializedName("rating")
    private float rating;

    public SavedItem(int cityId, String cityname, String country, String cityimage, float airportLat, float airportLon, String hotelname, String hotelimage, int hotelid, float hotelLat, float hotelLon, float hotelprice, float flightprice, int startdate, int enddate, float people, int savedon, float rating) {
        this.cityId = cityId;
        this.cityname = cityname;
        this.country = country;
        this.cityimage = cityimage;
        this.airportLat = airportLat;
        this.airportLon = airportLon;
        this.hotelname = hotelname;
        this.hotelimage = hotelimage;
        this.hotelid = hotelid;
        this.hotelLat = hotelLat;
        this.hotelLon = hotelLon;
        this.hotelprice = hotelprice;
        this.flightprice = flightprice;
        this.startdate = startdate;
        this.enddate = enddate;
        this.people = people;
        this.savedon = savedon;
        this.rating = rating;
    }

    public SavedItem() {
    }

    public float getHotelLat() {
        return hotelLat;
    }

    public float getHotelLon() {
        return hotelLon;
    }

    public float getAirportLat() {
        return airportLat;
    }

    public float getAirportLon() {
        return airportLon;
    }

    public int getCityId() {
        return cityId;
    }

    public String getCityname() {
        return cityname;
    }

    public String getCountry() {
        return country;
    }

    public String getCityimage() {
        return TakeMeAway.DESTINATION_IMAGE_URI + cityimage;
    }

    public String getHotelname() {
        return hotelname;
    }

    public String getHotelimage() {
        return hotelimage;
    }

    public int getHotelid() {
        return hotelid;
    }

    public int getHotelprice() {

        return (int)hotelprice;
    }

    public int getFlightprice() {
        return (int)flightprice;
    }

    public int getStartdate() {
        return startdate;
    }

    public int getEnddate() {
        return enddate;
    }

    public float getPeople() {
        return people;
    }

    public int getSavedon() {
        return savedon;
    }

    public float getRating() {
        return rating;
    }
}
