package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SingleRoom implements Serializable {

    @SerializedName("room_type_id")
    private Integer roomtTypeId;

    @SerializedName("deposit_required")
    private Boolean depositRequired;

    @SerializedName("adults")
    private Integer adults;

    @SerializedName("num_rooms_available_at_this_price")
    private Integer roomsAtThisprice;

    @SerializedName("room_amenities")
    private ArrayList<String> roomAmenities;

    @SerializedName("price")
    private float price;

    @SerializedName("refundable")
    private Boolean refundable;

    @SerializedName("room_id")
    private Integer roomId;

    @SerializedName("room_name")
    private String roomName;

    @SerializedName("block_id")
    private String blockId;

    public SingleRoom(Integer roomtTypeId, Boolean depositRequired, Integer adutls, Integer roomsAtThisprice, ArrayList<String> roomAmenities, float price, Boolean refundable, Integer roomId, String roomName, String blockId) {
        this.roomtTypeId = roomtTypeId;
        this.depositRequired = depositRequired;
        this.adults = adutls;
        this.roomsAtThisprice = roomsAtThisprice;
        this.roomAmenities = roomAmenities;
        this.price = price;
        this.refundable = refundable;
        this.roomId = roomId;
        this.roomName = roomName;
        this.blockId = blockId;
    }

    public Integer getRoomtTypeId() {
        return roomtTypeId;
    }

    public Boolean getDepositRequired() {
        return depositRequired;
    }

    public Integer getAdults() {
        return adults;
    }

    public Integer getRoomsAtThisprice() {
        return roomsAtThisprice;
    }

    public ArrayList<String> getRoomAmenities() {
        return roomAmenities;
    }

    public float getPrice() {
        return price;
    }

    public Boolean getRefundable() {
        return refundable;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getBlockId() {
        return blockId;
    }
}
