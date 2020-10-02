package io.takemeaway.takemeaway.datamodels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SingleRoomBlock implements Serializable {

    @SerializedName("room_description")
    private String roomDescription;

    @SerializedName("photos")
    private ArrayList<RoomPhotoBlock> photos;

    @SerializedName("number_of_rooms_left")
    private Integer numberOfRooms;

    @SerializedName("facilities")
    private ArrayList<String> facilities;

    @SerializedName("name")
    private String roomName;

    @SerializedName("max_occupancy")
    private Integer maxOccupancy;

    @SerializedName("room_id")
    private Integer roomId;

    @SerializedName("block_id")
    private String blockId;

    @SerializedName("rack_rate")
    private RoomPriceBlock rackRate;

    @SerializedName("incremental_price")
    private ArrayList<RoomPriceBlock> incrementalPrice;

    @SerializedName("min_price")
    private RoomPriceBlock minPrice;

    @SerializedName("room_type_id")
    private Integer roomTypeId;

    @SerializedName("refundable")
    private boolean refundable;

    public SingleRoomBlock(String roomDescription, ArrayList<RoomPhotoBlock> photos, Integer numberOfRooms, ArrayList<String> facilities, String roomName, Integer maxOccupancy, Integer roomId, String blockId, RoomPriceBlock rackRate, ArrayList<RoomPriceBlock> incrementalPrice, RoomPriceBlock minPrice, Integer roomTypeId, boolean refundable) {
        this.roomDescription = roomDescription;
        this.photos = photos;
        this.numberOfRooms = numberOfRooms;
        this.facilities = facilities;
        this.roomName = roomName;
        this.maxOccupancy = maxOccupancy;
        this.roomId = roomId;
        this.blockId = blockId;
        this.rackRate = rackRate;
        this.incrementalPrice = incrementalPrice;
        this.minPrice = minPrice;
        this.roomTypeId = roomTypeId;
        this.refundable = refundable;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public ArrayList<RoomPhotoBlock> getPhotos() {
        return photos;
    }

    public Integer getNumberOfRooms() {
        return numberOfRooms;
    }

    public ArrayList<String> getFacilities() {
        return facilities;
    }

    public String getRoomName() {
        return roomName;
    }

    public Integer getMaxOccupancy() {
        return maxOccupancy;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public String getBlockId() {
        return blockId;
    }

    public RoomPriceBlock getRackRate() {
        return rackRate;
    }

    public ArrayList<RoomPriceBlock> getIncrementalPrice() {
        return incrementalPrice;
    }

    public RoomPriceBlock getMinPrice() {
        return minPrice;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public boolean isRefundable() {
        return refundable;
    }
}
