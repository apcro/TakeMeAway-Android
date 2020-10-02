package io.takemeaway.takemeaway.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Currency;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.Airports;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.SingleHotel;

import io.takemeaway.takemeaway.datamodels.UserDetails;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
/*
 * Backend
 *
 * Provides core backend services for the TakeMeAway application
 */
public class Backend {

    private static final String USER_TOKEN_PREFERENCES_KEY = "userToken";
    private static final String DEVICE_TOKEN_PREFERENCES_KEY = "deviceToken";
    private static final String HAS_SEEN_TUTORIAL_PREFERENCES_KEY = "hasSeenTutorial";

    private static final String HASH_ALGORITHM = "HmacSHA256";

    // these values come from TakeMeAway, and are unique to you
    private static String privateKey = "@string/privateKey";
    private static String publicKey = "@string/publicKey";
    private static String salt = "@string/salt";

    private TakeMeAway application;

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String USER_NORMAL = "normal";
    public final String USER_TWITTER = "twitter";
    public final String USER_FACEBOOK = "facebook";

    private String userType = USER_NORMAL;

    private SingleDestination selectedDestination;
    private SingleHotel selectedHotel;
    private int currentFlightPrice;
    private int currentHotelPrice;
    private String currentSelectedRoom;


    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
        saveCurrentUser(getCurrentUser());
    }

    public Backend(TakeMeAway application) {
        this.application = application;
    }

    public TakeMeAway getApplication() {
        return application;
    }

    public String getSharedPreferences(String key) {
        SharedPreferences preferences = getApplication().getSharedPreferences(TakeMeAway.preferencesFileName, Context.MODE_PRIVATE);
        if (preferences.contains(key)) {
            return preferences.getString(key, null);
        }
        return null;
    }

    public void setSharedPreferences(String key, String value) {
        SharedPreferences preferences = getApplication().getSharedPreferences(TakeMeAway.preferencesFileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void clearAllSharedPreferences() {
        SharedPreferences preferences = getApplication().getSharedPreferences(TakeMeAway.preferencesFileName, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();

        // Preserve hasSeenTutorial, bound to device
        String hasSeenTutorial = "no";
        if (preferences.contains(HAS_SEEN_TUTORIAL_PREFERENCES_KEY)) {
            hasSeenTutorial = preferences.getString(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, null);
        }
        editor.putString(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, hasSeenTutorial);

        editor.apply();
    }

    public void Logout() {

        if (userType == USER_FACEBOOK) {
            LoginManager.getInstance().logOut();
        }

        if (userType.equals(USER_FACEBOOK)) {
            LoginManager.getInstance().logOut();
        }

        clearAllSharedPreferences();

    }

    public String getUserToken() {
        return getSharedPreferences(USER_TOKEN_PREFERENCES_KEY);
    }

    public void saveUserToken(String userToken) {
        if (userToken == "") {
            setSharedPreferences(USER_TOKEN_PREFERENCES_KEY, null);
        } else {
            setSharedPreferences(USER_TOKEN_PREFERENCES_KEY, userToken);
        }
    }

    public String getDeviceToken() {
        return getSharedPreferences(DEVICE_TOKEN_PREFERENCES_KEY);
    }

    public void saveDeviceToken(String deviceToken) {
        setSharedPreferences(DEVICE_TOKEN_PREFERENCES_KEY, deviceToken);
    }

    public boolean hasSeenTutorial() {
        String hasSeenTutorial = getSharedPreferences(HAS_SEEN_TUTORIAL_PREFERENCES_KEY);
        return hasSeenTutorial != null && hasSeenTutorial.equals("yes");
    }

    public void setTutorialSeen() {
        setSharedPreferences(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, "yes");
    }

    public void setTutorialToSee() {
        setSharedPreferences(HAS_SEEN_TUTORIAL_PREFERENCES_KEY, "no");
    }

    public UserDetails getCurrentUser() {
        Gson gson = new Gson();
        String json = getSharedPreferences(UserDetails.preferencesKey);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, UserDetails.class);
    }

    public int getUserId() {
        UserDetails currentUser = getCurrentUser();
        if (currentUser == null) {
            return 0;
        }
        return currentUser.getId();
    }

    public boolean isLoggedIn() {
        return getUserToken() != null && getCurrentUser() != null;
    }

    private String getTimestamp() {
        long timestamp = new Date().getTime();
        return String.valueOf(timestamp);
    }

    public String getAuthorisationHeaderValue() {
        String timestamp = getTimestamp();
        String hashSource = timestamp + publicKey + salt;
        String hash = null;
        try {
            hash = hashMac(hashSource, privateKey);
        } catch (SignatureException e) {
            Log.e("SignatureException", e.getMessage());
        }

        if (hash == null) {
            hash = "";
        }

        return "KeyAuth publicKey=" + publicKey + " hash=" + hash + " ts=" + timestamp;
    }

    static String hashMac(String text, String secretKey)
            throws SignatureException {
        try {
            Key sk = new SecretKeySpec(secretKey.getBytes(), HASH_ALGORITHM);
            Mac mac = Mac.getInstance(sk.getAlgorithm());
            mac.init(sk);
            final byte[] hmac = mac.doFinal(text.getBytes());
            return toHexString(hmac);
        } catch (NoSuchAlgorithmException e1) {
            // throw an exception or pick a different encryption method
            throw new SignatureException(
                    "error building signature, no such algorithm in device " + HASH_ALGORITHM);
        } catch (InvalidKeyException e) {
            throw new SignatureException("error building signature, invalid key " + HASH_ALGORITHM);
        }
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        Formatter formatter = new Formatter(sb);
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return sb.toString();
    }

    public void saveCurrentUser(UserDetails currentUser) {
        Gson gson = new Gson();
        String json = gson.toJson(currentUser);
        setSharedPreferences(UserDetails.preferencesKey, json);
    }

    public void saveAirportsList(Airports airports) {
        Gson gson = new Gson();
        String json = gson.toJson(airports);
        setSharedPreferences(Airports.preferencesKey, json);
    }

    public Airports getAirportsList() {
        Gson gson = new Gson();
        String json = getSharedPreferences(Airports.preferencesKey);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, Airports.class);
    }

    public void saveCurrentDestination(SingleDestination currentDestination) {
        Gson gson = new Gson();
        String json = gson.toJson(currentDestination);
        setSharedPreferences("currentDestination", json);
    }

    public SingleDestination getCurrentDestination() {
        Gson gson = new Gson();
        String json = getSharedPreferences("currentDestination");
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, SingleDestination.class);
    }

    static String getPrivateKey() {
        return privateKey;
    }

    static String getPublicKey() {
        return publicKey;
    }

    static String getSalt() {
        return salt;
    }

    static String getBaseUrl() {
        String baseUrl = "https://api.takemeaway.io/api/1.0/";
        return baseUrl;
    }

    public String CurrencySymbol() {
        Currency currency = Currency.getInstance(Locale.getDefault());
        return currency.getSymbol();
    }

    public boolean isFirstTimeLaunch() {
        SharedPreferences preferences = getApplication().getSharedPreferences(TakeMeAway.preferencesFileName, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        SharedPreferences preferences = getApplication().getSharedPreferences(TakeMeAway.preferencesFileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        preferencesEditor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        preferencesEditor.apply();
    }

    public SingleDestination getSelectedDestination() {
        return selectedDestination;
    }

    public void setSelectedDestination(SingleDestination selectedDestination) {
        this.selectedDestination = selectedDestination;
    }

    public SingleHotel getSelectedHotel() {
        return selectedHotel;
    }

    public void setSelectedHotel(SingleHotel selectedHotel) {
        this.selectedHotel = selectedHotel;
        this.currentHotelPrice = selectedHotel.getAvgRate();
        Log.d("backend-set hotelprice", String.valueOf(selectedHotel.getAvgRate()));
    }

    public int getCurrentFlightPrice() {
        return currentFlightPrice;
    }

    public void setCurrentFlightPrice(int currentFlightPrice) {
        this.currentFlightPrice = currentFlightPrice;
    }

    public int getCurrentHotelPrice() {
        return currentHotelPrice;
    }

    public void setCurrentHotelPrice(int currentHotelPrice) {
        this.currentHotelPrice = currentHotelPrice;
    }

    public static String getAid() {
        // aid is provided by Booking.com and is your unique affiliate ID
        String aid = "@string/aid";
        return aid;
    }

    public String getCurrentSelectedRoom() {
        return currentSelectedRoom;
    }

    public void setCurrentSelectedRoom(String currentSelectedRoom) {
        this.currentSelectedRoom = currentSelectedRoom;
    }
}