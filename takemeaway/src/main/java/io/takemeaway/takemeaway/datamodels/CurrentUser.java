package io.takemeaway.takemeaway.datamodels;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class CurrentUser {

    public static String preferencesKey = "CurrentUser";

    // profile
    private static String userId;
    private static String firstName;
    private static String lastName;
    private static String avatar;

    private static String placeId;
    private static String placeName;

    private static String postcode;
    private static double latitude;
    private static double longitude;

    private static Bitmap avatarBitmap;

    // constructs a CurrentUser object from getProfile JSON
    public CurrentUser(JSONObject getProfileJson) throws JSONException {
        userId = (String) getProfileJson.get("userId");
        firstName = (String) getProfileJson.get("firstname");
        lastName = (String) getProfileJson.get("lastname");
        avatar = (String) getProfileJson.get("avatar");

        placeId = (String) getProfileJson.get("placeId");
        placeName = (String) getProfileJson.get("placeName");

        JSONObject filters = (JSONObject) getProfileJson.get("filters");

        postcode = (String) getProfileJson.get("postcode");
        latitude = Double.parseDouble(getProfileJson.get("latitude").toString());
        longitude = Double.parseDouble(getProfileJson.get("longitude").toString());

    }


}
