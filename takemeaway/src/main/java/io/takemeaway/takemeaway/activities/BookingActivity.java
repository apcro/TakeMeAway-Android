package io.takemeaway.takemeaway.activities;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import androidx.cardview.widget.CardView;
import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.HotelDetails;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static io.takemeaway.takemeaway.R.id.toolbar;

/**
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon, Rob Reev & Junaid Rahim
 */

/**
 * This is the main Booking activity, showing details of the destination and selected hotel
 * along with the necessary redirects to SkyScanner/Booking.com
 */
public class BookingActivity extends Activity implements View.OnClickListener {

    private static final String TAG = BookingActivity.class.getSimpleName();

    private Context mContext;
    private Backend mBackend;

    private String token;

    ProgressDialog progressDialog;

    private RelativeLayout mDetailsLoader;

    private ImageView mCityImage;
    private String mHotelBookingLink;
    private String mFlightBookingLink;
    private TextView mTotalTripCost;
    private TextView mTotalTripPeople;
    private TextView mFlightDetailsText;

    private SingleDestination mDestination;

    int mCityId;
    int mHotelId;

    private String mHotelCurrency;
    private String mFlightPrice;

    private boolean mEnableEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        this.mContext = getApplicationContext();
        mBackend = TakeMeAway.getBackend();
        token = mBackend.getUserToken();

        if (token == null) {
            goHome();
            finish();
        }

        setContentView(R.layout.activity_booking);

        mDetailsLoader = findViewById(R.id.detailsLoader);
        mTotalTripCost = findViewById(R.id.totalCost);
        mTotalTripPeople = findViewById(R.id.totalPeople);
        mFlightDetailsText = findViewById(R.id.flightDetailsText);

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.bookingpageheader);
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = findViewById(toolbar);
        setActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        if (getActionBar() != null) {
            getActionBar().setHomeAsUpIndicator(upArrow);
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        progressDialog = new ProgressDialog(BookingActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Details");
        progressDialog.show();

        mCityId = getIntent().getIntExtra("cityId", 0);
        mHotelId = getIntent().getIntExtra("hotelId", 0);

        int mHotelPrice = getIntent().getIntExtra("hotelPrice", 0);
        mHotelCurrency = getIntent().getStringExtra("hotelCurrency");

        mFlightPrice = getIntent().getStringExtra("flightCost");

        mCityImage = findViewById(R.id.destinationLeader);

        CardView mBookHotelButton = findViewById(R.id.hotelDetailsHolder);
        CardView mBookFlightButton = findViewById(R.id.TripDetailsHolder);
        CardView mEmailDetailsButton = findViewById(R.id.emailDetailsHolder);

        mBookHotelButton.setOnClickListener(this);
        mBookFlightButton.setOnClickListener(this);
        mEmailDetailsButton.setOnClickListener(this);

        UserDetails currentUser = mBackend.getCurrentUser();

        mEnableEmail = true;

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        mDestination = (SingleDestination) bundle.getSerializable("destination");

        ImageView mHomeIcon = findViewById(R.id.homeIcon);
        mHomeIcon.setVisibility(View.VISIBLE);
        mHomeIcon.setOnClickListener(view -> goHome());

        loadDetails();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        Utilities.hideUI(this);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void loadDetails() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<SingleDestination> call = apiService.getCityDetails(token, mCityId);
        call.enqueue(new Callback<SingleDestination>() {
            @Override
            public void onResponse(Call<SingleDestination>call, Response<SingleDestination> response) {
                mDestination = response.body();

                // destination image
                if (mDestination.getCityImage() != null) {
                    String cityImageUri = TakeMeAway.DestinationImageUri() + mDestination.getCityImage();
                    Picasso.with(mContext).load(cityImageUri).fit().centerCrop().into(mCityImage);
                }
                TextView textView = findViewById(R.id.destinationName);
                textView.setText(mDestination.getCityName());

                mFlightBookingLink = mDestination.getFlightRefUri();

                TextView travelDates  = findViewById(R.id.travelDates);
                travelDates.setText(mDestination.getCityDescription());

                Integer temperature = mDestination.getTemperature();
                if (temperature != -999) {
                    TextView travelWeather = findViewById(R.id.travelWeather);
                    travelWeather.setText(mDestination.getTemperature() + "° " + mDestination.getWeather_description());
                    setWeatherIcon();
                } else {
                    findViewById(R.id.travelWeatherHolder).setVisibility(View.GONE);
                }

                loadHotelDetails();

            }

            @Override
            public void onFailure(Call<SingleDestination>call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void setWeatherIcon() {
        TextView weatherIcon = findViewById(R.id.weatherIcon);
        String iconString = "wi_owm_" + String.valueOf(mDestination.getWeather_icon());

        int stringId = 0;
        try {
            stringId = R.string.class.getField(iconString).getInt(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        weatherIcon.setText(stringId);

    }
    private void loadHotelDetails() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<HotelDetails> call = apiService.getHotelDetails(token, mHotelId);
        call.enqueue(new Callback<HotelDetails>() {
            @Override
            public void onResponse(Call<HotelDetails>call, Response<HotelDetails> response) {
                int status = response.body().getStatus();
                if (status == 1) {
                    SingleHotel hotel = response.body().getHotel();

                    TextView textView = findViewById(R.id.hotelName);
                    textView.setText(hotel.getName());

                    mHotelBookingLink = Utilities.makeBookingComURI(hotel.getHotelId(), mBackend.getCurrentUser().getLeaveDay(), Utilities.GetNights(), 2, mBackend.getCurrentSelectedRoom());

                    int people = TakeMeAway.getBackend().getCurrentUser().getPeople();

                    String tripCostText = "for " + String.valueOf(people);
                    if (people == 1) {
                        tripCostText += " person";
                    } else {
                        tripCostText += " people";
                    }
                    mTotalTripPeople.setText("Cost " + tripCostText);
                    mFlightDetailsText.setText("Return flights " + tripCostText);

                    mTotalTripCost.setText(Utilities.getCurrencySymbol(mBackend.getCurrentUser().getCurrencyCode()) + String.valueOf(Utilities.getCurrentTripPrice()));

                } else {
                    // error
                    Toast.makeText(mContext, "Backend error", Toast.LENGTH_SHORT).show();
                }
                mDetailsLoader.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(Call<HotelDetails>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent browserIntent;
        Bundle bundle = new Bundle();
        Map<String, String> articleParams = new HashMap<String, String>();
        switch (id) {
            case R.id.hotelDetailsHolder:
                bundle.putString("hotelLink", mHotelBookingLink);
                bundle.putString("flightLink", mFlightBookingLink);
                bundle.putInt("tab", 1);

                browserIntent = new Intent(this, BookingJourneyFragmentActivity.class);

                browserIntent.putExtra("uris", bundle);

                startActivity(browserIntent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                break;

            case R.id.TripDetailsHolder:
                bundle.putString("hotelLink", mHotelBookingLink);
                bundle.putString("flightLink", mFlightBookingLink);
                bundle.putInt("tab", 0);

                browserIntent = new Intent(this, BookingJourneyFragmentActivity.class);

                browserIntent.putExtra("uris", bundle);

                startActivity(browserIntent);
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                break;
            case R.id.emailDetailsHolder:
                if (mEnableEmail) {
                    sendEmail();
                } else {
                    Toast.makeText(mContext, "Details already emailed", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public void sendEmail() {

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Emailing Details");
        progressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<BaseResponse> call = apiService.emailTrip(token, mCityId, mHotelId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {
                int status = response.body().getStatus();
                if (status == 1) {
                    Toast.makeText(mContext, "Details emailed!", Toast.LENGTH_SHORT).show();
                    mEnableEmail = false;
                } else {
                    // error
                    Toast.makeText(mContext, "Sorry, could not email the details.", Toast.LENGTH_SHORT).show();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }


}

