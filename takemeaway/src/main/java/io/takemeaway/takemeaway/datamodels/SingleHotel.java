package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SingleHotel implements Serializable {

    @SerializedName("hotel_id")
    private Integer hotelId;

    @SerializedName("url")
    private String url;

    @SerializedName("name")
    private String name;

    @SerializedName("hotel_lat")
    private float hotelLat;
    @SerializedName("hotel_lon")
    private float hotelLon;

    @SerializedName("countrycode")
    private String countryCode;

    @SerializedName("minrate")
    private Integer minRate;

    @SerializedName("avgrate")
    private Integer avgRate;

    @SerializedName("maxrate")
    private Integer maxRate;

    @SerializedName("currencycode")
    private String currencycode;

    @SerializedName("photo")
    private String hotelPhoto;

    @SerializedName("description")
    private String hotelDescription;

    @SerializedName("review_score")
    private Float rating;

    @SerializedName("checkin_from")
    private String checkinFrom;

    @SerializedName("checkin_to")
    private String checkinTo;

    @SerializedName("checkout_from")
    private String checkoutFrom;

    @SerializedName("checkout_to")
    private String checkoutTo;

    @SerializedName("hotelier_welcome")
    private String hotelierWelcome;

    @SerializedName("important_information")
    private String importantInformation;

    @SerializedName("address")
    private String address;

    @SerializedName("zip")
    private String zip;

    @SerializedName("images")
    private ArrayList<String> images;

    @SerializedName("bookinglink")
    private String bookinglink;

    @SerializedName("rooms")
    private ArrayList<SingleRoom> rooms;

    @SerializedName("amenities1")
    private String amenities1;

    @SerializedName("amenities2")
    private String amenities2;

    // new, extended data
    @SerializedName("blockdata")
    private ArrayList<SingleRoomBlock> roomBlocks;

    private boolean savedHotel;

    public SingleHotel() {
    }

    public SingleHotel(Integer hotelId, String url, String name, float hotelLat, float hotelLon, String countryCode, Integer minRate, Integer avgRate, Integer maxRate, String currencycode, String hotelPhoto, String hotelDescription, Float rating, String checkinFrom, String checkinTo, String checkoutFrom, String checkoutTo, String hotelierWelcome, String importantInformation, String address, String zip, ArrayList<String> images, String bookinglink, ArrayList<SingleRoom> rooms, String amenities1, String amenities2, boolean savedHotel, ArrayList<SingleRoomBlock> roomBlocks) {
        this.hotelId = hotelId;
        this.url = url;
        this.name = name;
        this.hotelLat = hotelLat;
        this.hotelLon = hotelLon;
        this.countryCode = countryCode;
        this.minRate = minRate;
        this.avgRate = avgRate;
        this.maxRate = maxRate;
        this.currencycode = currencycode;
        this.hotelPhoto = hotelPhoto;
        this.hotelDescription = hotelDescription;
        this.rating = rating;
        this.checkinFrom = checkinFrom;
        this.checkinTo = checkinTo;
        this.checkoutFrom = checkoutFrom;
        this.checkoutTo = checkoutTo;
        this.hotelierWelcome = hotelierWelcome;
        this.importantInformation = importantInformation;
        this.address = address;
        this.zip = zip;
        this.images = images;
        this.bookinglink = bookinglink;
        this.rooms = rooms;
        this.amenities1 = amenities1;
        this.amenities2 = amenities2;
        this.savedHotel = savedHotel;
        this.roomBlocks = roomBlocks;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public Integer getHotelId() {
        return hotelId;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public float getHotelLat() {
        return hotelLat;
    }

    public float getHotelLon() {
        return hotelLon;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public Integer getMinRate() {
        return minRate;
    }

    public Integer getMaxRate() {
        return maxRate != null ? maxRate:0;
    }

    public String getHotelPhoto() {
        return hotelPhoto;
    }

    public String getHotelDescription() {
        return hotelDescription;
    }

    public String getCurrencycode() {
        return currencycode;
    }

    public Float getRating() {
        return rating;
    }

    public String getCheckinFrom() {
        return checkinFrom;
    }

    public String getCheckinTo() {
        return checkinTo;
    }

    public String getCheckoutFrom() {
        return checkoutFrom;
    }

    public String getCheckoutTo() {
        return checkoutTo;
    }

    public String getHotelierWelcome() {
        return hotelierWelcome;
    }

    public String getImportantInformation() {
        return importantInformation;
    }

    public String getAddress() {
        return address;
    }

    public String getZip() {
        return zip;
    }

    public String getBookinglink() {
        return bookinglink;
    }

    public ArrayList<SingleRoom> getRooms() {
        return rooms;
    }

    public boolean isSavedHotel() {
        return savedHotel;
    }

    public void setSavedHotel(boolean savedHotel) {
        this.savedHotel = savedHotel;
    }

    public String getAmenities1() {
        return amenities1;
    }

    public String getAmenities2() {
        return amenities2;
    }

    public Integer getAvgRate() {
        if (this.avgRate == null) {
            if (this.minRate > 0) {
                this.avgRate = (this.minRate + this.maxRate) / 2;
            } else {
                this.avgRate = this.maxRate;
            }
        }
        return this.avgRate;
    }

    public ArrayList<SingleRoomBlock> getRoomBlocks() {
        return roomBlocks;
    }
}
