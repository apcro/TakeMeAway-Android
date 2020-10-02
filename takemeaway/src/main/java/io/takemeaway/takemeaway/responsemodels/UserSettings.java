package io.takemeaway.takemeaway.responsemodels;

import com.google.gson.annotations.SerializedName;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class UserSettings {

    @SerializedName("status")
    private Integer status;

    @SerializedName("latitude")
    private float lat;

    @SerializedName("longitude")
    private float lon;

    @SerializedName("budget")
    private int budget;

    @SerializedName("people")
    private int people;

    @SerializedName("split")
    private int split;

    @SerializedName("home_airport")
    private String home_airport;

    @SerializedName("travel_dates")
    private String travel_dates;

    @SerializedName("avatar")
    private String avatar;

    @SerializedName("hotel_types")
    private String hotelTypes;

    @SerializedName("currency_code")
    private String currencyCode;

    @SerializedName("locale")
    private String locale;

    @SerializedName("thisWeekendStart")
    private String thisWeekendStart;
    @SerializedName("thisWeekendEnd")
    private String thisWeekendEnd;
    @SerializedName("nextWeekendStart")
    private String nextWeekendStart;
    @SerializedName("nextWeekendEnd")
    private String nextWeekendEnd;

    public UserSettings(Integer status, float lat, float lon, int budget, int people, int split, String home_airport, String travel_dates, String avatar, String hotelTypes, String currencyCode, String locale, String thisWeekendStart, String thisWeekendEnd, String nextWeekendStart, String nextWeekendEnd) {
        this.status = status;
        this.lat = lat;
        this.lon = lon;
        this.budget = budget;
        this.people = people;
        this.split = split;
        this.home_airport = home_airport;
        this.travel_dates = travel_dates;
        this.avatar = avatar;
        this.hotelTypes = hotelTypes;
        this.currencyCode = currencyCode;
        this.locale = locale;
        this.thisWeekendStart = thisWeekendStart;
        this.thisWeekendEnd = thisWeekendEnd;
        this.nextWeekendStart = nextWeekendStart;
        this.nextWeekendEnd = nextWeekendEnd;
    }

    public Integer getStatus() {
        return status;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public int getBudget() {
        return budget;
    }

    public int getPeople() {
        return people;
    }

    public int getSplit() {
        return split;
    }

    public String getHome_airport() {
        return home_airport;
    }

    public String getTravel_dates() {
        return travel_dates;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getHotelTypes() {
        return hotelTypes;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getLocale() {
        return locale;
    }

    public String getThisWeekendStart() {
        return thisWeekendStart;
    }

    public String getThisWeekendEnd() {
        return thisWeekendEnd;
    }

    public String getNextWeekendStart() {
        return nextWeekendStart;
    }

    public String getNextWeekendEnd() {
        return nextWeekendEnd;
    }
}
