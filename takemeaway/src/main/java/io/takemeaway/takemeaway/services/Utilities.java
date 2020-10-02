package io.takemeaway.takemeaway.services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.TextStyle;
import org.threeten.bp.temporal.TemporalAdjusters;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.activities.MainActivity;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class Utilities {

    private static final String TAG = Utilities.class.getSimpleName();

    public static String getCurrencySymbol(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency.getSymbol();
        } catch (Exception e) {
            return currencyCode;
        }
    }

    public static int getRandomLoaderImage() {
        int loaderImage = R.drawable.lakebackground;
        Random r = new Random();
        int rand = r.nextInt(5);    // 0 to 4 (yes, really)
        switch(rand) {
            case 0:
                loaderImage = R.drawable.splash_image_2;
                break;
            case 1:
                loaderImage = R.drawable.ilnur_kalimullin_354455;
                break;
            case 2:
                loaderImage = R.drawable.tanner_larson_310290;
                break;
            case 3:
                loaderImage = R.drawable.amsterdam_256;
                break;
            case 4:
                loaderImage = R.drawable.simon_migaj_421505_unsplash_256;
                break;
        }

        return loaderImage;
    }

    public static void sendDeviceTokenToServer() {

        // send device token to server and store against current userId
        String userToken = TakeMeAway.getBackend().getUserToken();
        String deviceToken = TakeMeAway.getBackend().getSharedPreferences("deviceToken");

        if (userToken!= null) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<BaseResponse> call = apiService.updateDeviceToken(userToken, deviceToken, "android");
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    TakeMeAway.getBackend().setSharedPreferences("deviceTokenSaved", "yes");
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    // Log error here since request failed
                }
            });
        }
    }

    public static Point getDisplaySize(WindowManager windowManager) {
        try {
            if (Build.VERSION.SDK_INT > 16) {
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
            } else {
                return new Point(0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Point(0, 0);
        }
    }

    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final Map<String, String> getTravelDates(String leaveOn, String returnOn) {
        Map <String, String> dateStrings = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDate leaveDate = today.with( TemporalAdjusters.next( DayOfWeek.FRIDAY ) );
        LocalDate returnDate = leaveDate.with( TemporalAdjusters.next( DayOfWeek.SUNDAY ) );

        switch (leaveOn) {
            case "thursday":
                leaveDate = today.with( TemporalAdjusters.next( DayOfWeek.THURSDAY ) );
                break;
            case "saturday":
                leaveDate = today.with( TemporalAdjusters.next( DayOfWeek.SATURDAY ) );
                break;
        }

        switch (returnOn) {
            case "monday":
                returnDate = leaveDate.with( TemporalAdjusters.next( DayOfWeek.MONDAY ) );
                break;
            case "tuesday":
                returnDate = leaveDate.with( TemporalAdjusters.next( DayOfWeek.TUESDAY ) );
                break;
        }

        String outputDates =
                leaveDate.getDayOfMonth() + " "
                + leaveDate.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                + " - "
                + returnDate.getDayOfMonth() + " "
                + returnDate.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());
        dateStrings.put("thisweekend", outputDates);

        leaveDate = leaveDate.plusWeeks(1);
        returnDate = returnDate.plusWeeks(1);

        outputDates =
                leaveDate.getDayOfMonth() + " "
                        + leaveDate.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                        + "-"
                        + returnDate.getDayOfMonth() + " "
                        + returnDate.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());

        dateStrings.put("nextweekend", outputDates);

        leaveDate = leaveDate.plusWeeks(1);
        returnDate = returnDate.plusWeeks(1);

        outputDates =
                leaveDate.getDayOfMonth() + " "
                        + leaveDate.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault())
                        + "-"
                        + returnDate.getDayOfMonth() + " "
                        + returnDate.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault());

        dateStrings.put("in2weeks", outputDates);

        return dateStrings;
    }

    public static String GetDateString(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }

    public static int GetNights() {
        String leaveDay = TakeMeAway.getBackend().getCurrentUser().getLeaveDay();
        String returnDay = TakeMeAway.getBackend().getCurrentUser().getReturnDay();

        int ld = 0;
        int rd = 0;
        if (leaveDay.equals("thursday")) {
            ld = 0;
        }
        if (leaveDay.equals("friday")) {
            ld = 1;
        }
        if (leaveDay.equals("saturday")) {
            ld = 2;
        }
        if (returnDay.equals("sunday")) {
            rd = 3;
        }
        if (returnDay.equals("monday")) {
            rd = 4;
        }
        if (returnDay.equals("tuesday")) {
            rd = 5;
        }
        return rd-ld;
    }

    public static int getCurrentTripPrice() {
        int people = TakeMeAway.getBackend().getCurrentUser().getPeople();
        int flightPrice = TakeMeAway.getBackend().getCurrentFlightPrice() * people;
        int nights = Utilities.GetNights();
        int hotelPrice = TakeMeAway.getBackend().getCurrentHotelPrice() * nights;
        return flightPrice + hotelPrice;
    }


    public static void hideUI(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    public static String makeBookingComURI(Integer hotelId, String startDate, Integer nights, Integer stage, String roomblock) {
        String aid = Backend.getAid();
        String uri = "https://secure.booking.com/book.html?" + aid + "&hostname=www.booking.com&hotel_id=" + hotelId + "&stage=" + stage.toString();

        UserDetails currentUser = TakeMeAway.getBackend().getCurrentUser();
        // work out the actual date from saved data
        Map<String, String> travelDateStrings = Utilities.getTravelDates(currentUser.getLeaveDay(), currentUser.getReturnDay());
        switch (currentUser.getTravel_dates()) {
            case "thisweekend":
                break;
            case "nextweekend":
                break;
            case "in2weeks":
                break;
            case "manual":
                break;
        }

        switch (stage) {
            case 0:
                break;
            case 1:
                uri = uri + "&checkin=" + "2019-09-15";
                uri = uri + "&interval=" + nights.toString();
                break;
            case 2:
                // add selected room
                uri = uri + "&checkin=" + "2019-09-15";
                uri = uri + "&interval=" + nights.toString();
                uri = uri + "&nr_rooms_" + roomblock + "=1";
                break;

        }

        return uri;

    }
}
