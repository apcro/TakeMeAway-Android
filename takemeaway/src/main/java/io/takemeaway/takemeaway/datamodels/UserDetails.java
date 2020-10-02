package io.takemeaway.takemeaway.datamodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.ByteArrayOutputStream;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.responsemodels.UserSettings;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class UserDetails {

    public static String preferencesKey = "CurrentUser";

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("surname")
    private String surname;

    @SerializedName("mail")
    private String mail;

    @SerializedName("username")
    private String username;

    @SerializedName("created")
    private String created;

    @SerializedName("usersettings")
    private UserSettings userSettings;

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
    @SerializedName("twoWeekendStart")
    private String twoWeekendStart;
    @SerializedName("twoWeekendEnd")
    private String twoWeekendEnd;

    @SerializedName("leaveDay")
    private String leaveDay;
    @SerializedName("returnDay")
    private String returnDay;

    @SerializedName("leaveDate")
    private Long leaveDate;
    @SerializedName("returnDate")
    private Long returnDate;

    private String avatarBitmap;

    public static final float HOME_AIRPORT_LAT = 52.3105F;
    public static final float HOME_AIRPORT_LON = 4.7683F;
    private static final String DEFAULT_CURRENCY = "GBP";
    private static final String DEFAULT_LOCALE = "en-GB";

    public UserDetails(Integer id, String name, String surname, String mail, String username, String created, UserSettings userSettings, float lat, float lon, int budget, int people, int split, String home_airport, String travel_dates, String avatar, String hotelTypes, String currencyCode, String locale, String thisWeekendStart, String thisWeekendEnd, String nextWeekendStart, String nextWeekendEnd, String twoWeekendStart, String twoWeekendEnd, String leaveDay, String returnDay, String avatarBitmap, Long returnDate, Long leaveDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.username = username;
        this.created = created;
        this.userSettings = userSettings;
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
        this.twoWeekendStart = twoWeekendStart;
        this.twoWeekendEnd = twoWeekendEnd;
        this.leaveDay = leaveDay;
        this.returnDay = returnDay;
        this.avatarBitmap = avatarBitmap;
        this.leaveDate = leaveDate;
        this.returnDate = returnDate;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getMail() {
        return mail;
    }

    public String getUsername() {
        return username;
    }

    public String getCreated() {
        return created;
    }

    public float getLat() {
        if (lat == 0.0f) {
            lat = HOME_AIRPORT_LAT;
        }
        return lat;
    }

    public float getLon() {
        if (lon == 0.0f) {
            lon = HOME_AIRPORT_LON;
        }
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

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public void setSplit(int split) {
        this.split = split;
    }

    public void setHome_airport(String home_airport) {
        this.home_airport = home_airport;
    }

    public String getTravel_dates() {
        return travel_dates;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setTravel_dates(String travel_dates) {
        this.travel_dates = travel_dates;
    }

    public String getHotelTypes() {
        return hotelTypes;
    }

    public void setHotelTypes(String hotelTypes) {
        this.hotelTypes = hotelTypes;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Bitmap getAvatarBitmap() {

        if (avatarBitmap != null) {
            byte[] imageAsBytes = Base64.decode(avatarBitmap.getBytes(), Base64.URL_SAFE);
            return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        } else {
            // return the default profile icon
            return BitmapFactory.decodeResource(TakeMeAway.context().getResources(), R.drawable.ic2_profile);
        }
    }


    public void setAvatarBitmap(Bitmap profileBitmap) {

        Bitmap resizedAvatar = getResizedBitmap(profileBitmap, 120, 120);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedAvatar.compress(Bitmap.CompressFormat.PNG, 90, baos); //bm is the bitmap object
        byte[] b = baos.toByteArray();

        this.avatarBitmap = Base64.encodeToString(b, Base64.URL_SAFE);
    }

    private Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return ThumbnailUtils.extractThumbnail(bm, width, height);
    }

    public String getCurrencyCode() {
        if (currencyCode == null) {
            return DEFAULT_CURRENCY;
        } else {
            return currencyCode;
        }

    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getLocale() {
        if (locale == null) {
            return DEFAULT_LOCALE;
        } else {
            return locale;
        }
    }

    public void setLocale(String locale) {
        this.locale = locale;
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

    public String getLeaveDay() {
        if (leaveDay == null) {
            return "friday";
        }
        return leaveDay;
    }

    public String getReturnDay() {
        if (returnDay == null) {
            return "sunday";
        }
        return returnDay;
    }

    public void setLeaveDay(String leaveDay) {
        this.leaveDay = leaveDay;
    }

    public void setReturnDay(String returnDay) {
        this.returnDay = returnDay;
    }

    public String getTwoWeekendStart() {
        return twoWeekendStart;
    }

    public String getTwoWeekendEnd() {
        return twoWeekendEnd;
    }

    public Long getLeaveDate() {
        return leaveDate;
    }

    public Long getReturnDate() {
        return returnDate;
    }
}
