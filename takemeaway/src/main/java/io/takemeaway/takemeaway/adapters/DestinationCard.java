package io.takemeaway.takemeaway.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.mindorks.placeholderview.SwipeDirection;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeInDirectional;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutDirectional;
import com.mindorks.placeholderview.annotations.swipe.SwipeTouch;
import com.mindorks.placeholderview.annotations.swipe.SwipeView;
import com.mindorks.placeholderview.annotations.swipe.SwipingDirection;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import androidx.appcompat.app.AlertDialog;
import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.activities.HotelActivity;
import io.takemeaway.takemeaway.activities.LoginActivity;
import io.takemeaway.takemeaway.activities.RegisterActivity;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import io.takemeaway.takemeaway.ui.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
@Layout(R.layout.destination_card)
public class DestinationCard {

    private static final String TAG = DestinationCard.class.getSimpleName();

    @View(R.id.cityImage)
    ImageView cityImage;

    @View(R.id.countryName)
    TextView countryName;

    @View(R.id.cityName)
    TextView cityName;

    @View(R.id.cityDescription)
    TextView cityDescription;

    @View(R.id.destinationCardProgressView)
    ProgressBar mProgressBar;

    @View(R.id.flightCost)
    TextView mFlightCost;

    @View(R.id.newLayout)
    RelativeLayout mCardDetails;

    @View(R.id.travelWeather)
    TextView mTravelWeather;

    @View(R.id.travelWeatherHolder)
    LinearLayout mTravelWeatherHolder;

    @View(R.id.weatherIcon)
    TextView mWeatherIcon;

    @View(R.id.saveIcon)
    ImageView mSavedIcon;

    private boolean mSaved = false;

    @SwipeView
    android.view.View mSwipeView;

    private Context mContext;
    private SingleDestination mDestination;
    private Picasso mPicasso;
    private Point mCardViewHolderSize;
    public SwipeCallback mCallback;

    private Backend mBackend;

    private String token;

    public DestinationCard(Picasso picasso, Context context, SingleDestination destination, Point cardViewHolderSize, SwipeCallback callback) {
        mContext = context;
        mDestination = destination;
        mPicasso = picasso;
        mCardViewHolderSize = cardViewHolderSize;
        mCallback = callback;
    }

    @Resolve
    public void onResolved() {
        countryName.setText(mDestination.getCountryName());
        cityName.setText(mDestination.getCityName());
        cityDescription.setText(mDestination.getCityDescription());
//        String cityImageUri = TakeMeAway.DESTINATION_IMAGE_URI + mDestination.getCityImage();
        String cityImageUri = TakeMeAway.DestinationImageUri() + mDestination.getCityImage();

        mSaved = mDestination.isSaved();

        mBackend = TakeMeAway.getBackend();
        token = mBackend.getUserToken();

        String flightCost = mDestination.getFlightCost();
        if (flightCost.equals("8888")) {
            mFlightCost.setVisibility(android.view.View.GONE);
        } else {
            mFlightCost.setVisibility(android.view.View.VISIBLE);
            mFlightCost.setText(" " + Utilities.getCurrencySymbol(mDestination.getFlightCurrency()) + mDestination.getFlightCost() + "pp");
        }

        Random r = new Random();
        int i1 = r.nextInt(6) - 3;

        mSwipeView.setRotation(i1);

        mCardDetails.setVisibility(android.view.View.VISIBLE);

        mPicasso.load(cityImageUri)
                .tag(mDestination.getCityName())
                .error(R.drawable.logo)
                .resize(mCardViewHolderSize.x, mCardViewHolderSize.y)
                .onlyScaleDown()
                .centerCrop()
                .into(cityImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        mProgressBar.setVisibility(android.view.View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }});

        Integer temperature = mDestination.getTemperature();
        if (temperature != -999) {
            mTravelWeather.setText(String.valueOf(mDestination.getTemperature()) + "° " + mDestination.getWeather_description());
            setWeatherIcon();
        } else {
            mTravelWeatherHolder.setVisibility(android.view.View.GONE);
        }

        setSaveIcon();

        mSavedIcon.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                saveUnsaveItem();
            }
        });

        if (token == null) {
            mSavedIcon.setVisibility(GONE);
        }

    }

    private void setWeatherIcon() {
        String iconString = "wi_owm_" + String.valueOf(mDestination.getWeather_icon());

        int stringId = 0;
        try {
            stringId = R.string.class.getField(iconString).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        mWeatherIcon.setText(stringId);

    }

    @SwipeOutDirectional
    public void onSwipeOutDirectional(SwipeDirection direction) {
        if (direction.getDirection() == SwipeDirection.TOP.getDirection()) {
            mCallback.onSwipeUp(mDestination);
        }

        if (
            (direction.getDirection() == SwipeDirection.LEFT.getDirection())
            || (direction.getDirection() == SwipeDirection.LEFT_BOTTOM.getDirection())
            || (direction.getDirection() == SwipeDirection.LEFT_TOP.getDirection())
        ) {
            cancelImageLoad();
            mCallback.onSwipeLeft(mDestination);
        }

        if (direction.getDirection() == SwipeDirection.RIGHT_TOP.getDirection()) {
            cancelImageLoad();
            mCallback.onSwipeRight(mDestination);
        }
    }

    @SwipeCancelState
    public void onSwipeCancelState() {
        mSwipeView.setAlpha(1);
    }

    @SwipeInDirectional
    public void onSwipeInDirectional(SwipeDirection direction) {
        if (
                (direction.getDirection() == SwipeDirection.RIGHT.getDirection())
                || (direction.getDirection() == SwipeDirection.RIGHT_BOTTOM.getDirection())
                || (direction.getDirection() == SwipeDirection.RIGHT_TOP.getDirection())
                ) {
            cancelImageLoad();
            mCallback.onSwipeRight(mDestination);
        }
    }

    @SwipingDirection
    public void onSwipingDirection(SwipeDirection direction) {}

    @SwipeTouch
    public void onSwipeTouch(float xStart, float yStart, float xCurrent, float yCurrent) {

        float cardHolderDiagonalLength = (float) Math.sqrt(Math.pow(mCardViewHolderSize.x, 2) + (Math.pow(mCardViewHolderSize.y, 2)));
        float distance = (float) Math.sqrt(Math.pow(xCurrent - xStart, 2) + (Math.pow(yCurrent - yStart, 2)));
        float alpha = 1 - distance / cardHolderDiagonalLength;

        if (xCurrent < xStart) {
            mSwipeView.setAlpha(alpha);
        }
    }

    public String GetCardTag() {
        return mDestination.getCityName();
    }

    public void cancelImageLoad() {
        mPicasso.cancelTag(mDestination.getCityName());
    }

    public interface SwipeCallback {
        void onSwipeUp(SingleDestination destination);
        void onSwipeLeft(SingleDestination destination);
        void onSwipeRight(SingleDestination destination);
    }

    private void saveUnsaveItem() {

        if (mSaved) {
            removeItem();
        } else {
            saveItem();
        }
        setSaveIcon();

    }

    private void setSaveIcon() {


        if (mSaved) {
            mSavedIcon.setImageResource(R.drawable.ic_favorite_white_24dp); //            setImageDrawable(toolbarSaveIcon);
        } else {
            mSavedIcon.setImageResource(R.drawable.ic_favorite_border_white_24dp); //            setImageDrawable(toolbarSaveIcon);
        }
    }
    private void removeItem() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<BaseResponse> call = apiService.deleteSavedItem(token, mDestination.getCityId(), 0);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {

                Map<String, String> articleParams = new HashMap<>();
                articleParams.put("city_ids", mDestination.getCityId().toString());
                articleParams.put("city_name", mDestination.getCityName());
                articleParams.put("token", TakeMeAway.getBackend().getUserToken());
                FlurryAgent.logEvent("RemovedSavedCity", articleParams);
                mSaved = false;
                setSaveIcon();
            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.sorry);
                builder.setMessage("Something went wrong and we couldn't delete that item");
                builder.setNegativeButton(R.string.oktext, null);
                builder.show();
            }
        });
        setSaveIcon();
    }

    private void saveItem() {

        if (token == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Sorry");
            builder.setCancelable(false);
            builder.setMessage("You need to be registered to save a trip.");
            builder.setNeutralButton("Cancel", null);
            builder.show();
        } else {

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<BaseResponse> call = apiService.saveItem(token, mDestination.getCityId(), 0, 0, "", 0, "");
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {
                    int status = response.body().getStatus();
                    if (status == 1) {

                        Map<String, String> articleParams = new HashMap<>();
                        articleParams.put("city_id", mDestination.getCityId().toString());
                        articleParams.put("city_name", mDestination.getCityName());
                        articleParams.put("token", TakeMeAway.getBackend().getUserToken());

                        articleParams.put("people", String.valueOf(TakeMeAway.getBackend().getCurrentUser().getPeople()));
                        articleParams.put("budget", String.valueOf(TakeMeAway.getBackend().getCurrentUser().getBudget()));
                        articleParams.put("weekend", TakeMeAway.getBackend().getCurrentUser().getTravel_dates());
                        FlurryAgent.logEvent("SavedTrip", articleParams);

                        Toast.makeText(mContext, "Destination Saved", Toast.LENGTH_SHORT).show();

                        mSaved = true;
                        setSaveIcon();
                    } else {
                        Toast.makeText(mContext, "There was an error saving", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse>call, Throwable t) {
                    // Log error here since request failed
                    Log.e(TAG, t.toString());
                }
            });
        }
    }



}