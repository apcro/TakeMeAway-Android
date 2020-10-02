package io.takemeaway.takemeaway.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.CustomHotelDetailsAdapter;
import io.takemeaway.takemeaway.adapters.RoomBlockPagerAdapter;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.HotelDetails;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.datamodels.SingleRoom;
import io.takemeaway.takemeaway.datamodels.SingleRoomBlock;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import io.takemeaway.takemeaway.ui.Utility;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.view.View.GONE;
import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class HotelDetailActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static final String TAG = HotelDetailActivity.class.getSimpleName();

    CustomHotelDetailsAdapter mCustomPagerAdapter;
    ViewPager mViewPager;

    public ArrayList<String> mHotelImages;

    private String token;

    private Backend mBackend;

    private Context mContext;

    private int hotelId;

    private TextView mRatingView;
    private TextView mPerNight;
    private TextView mDescription;

    MapView mapView;
    GoogleMap mGoogleMap;
    private TextView mHotelWelcomeMessage;
    private TextView mHotelImportantInformation;
    private TextView mHotelAddress;
    private TextView mHotelCheckIn;
    private TextView mHotelCheckout;
    private TextView mReviewScore;
    private RelativeLayout mLoadFader;

    private ProgressDialog mProgressDialog;

    private RelativeLayout mCheckInLayout;
    private RelativeLayout mCheckOutLayout;
    private RelativeLayout mHotelMessageHolder;
    private RelativeLayout mHotelInfoHolder;

    private LinearLayout mRoomholder;

    private String mFlightPrice;
    private String mHotelPrice;
    private String mCityName;
    private int mCityId;


    private ImageView mSaveIcon;
    private com.github.clans.fab.FloatingActionButton mNewFavouriteIcon;
    private ImageView mShareIcon;

    private SingleHotel mHotel;
    private SingleDestination mDestination;

    private TextView mPriceNotice;

    private com.github.clans.fab.FloatingActionButton mBookTravel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        setContentView(R.layout.activity_hoteldetails);

        int loaderImage = Utilities.getRandomLoaderImage();
        ImageView loaderView = findViewById(R.id.loaderImage);
        loaderView.setImageResource(loaderImage);

        mContext = this;

        mBackend = TakeMeAway.getBackend();

        token = mBackend.getUserToken();

        if (token == null) {
            goHome();
            finish();
        }

        mProgressDialog = new ProgressDialog(HotelDetailActivity.this, R.style.AppTheme_Dark_Dialog);

        mSaveIcon = findViewById(R.id.saveIcon);
        mSaveIcon.setVisibility(View.GONE);

        mNewFavouriteIcon = findViewById(R.id.new_favourite);
        mNewFavouriteIcon.setOnClickListener(this);


        mShareIcon = findViewById(R.id.shareIcon);
        Drawable toolbarShareIcon = getDrawable(R.drawable.ic_share_white_24dp);
        if (toolbarShareIcon != null) {
            toolbarShareIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mShareIcon.setImageDrawable(toolbarShareIcon);
        }
        mShareIcon.setOnClickListener(this);

        mapView = findViewById(R.id.mapHolder);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        hotelId = getIntent().getIntExtra("hotelId", 1);
        mCityId = getIntent().getIntExtra("cityId", 0);
        mCityName = getIntent().getStringExtra("cityName");
        mFlightPrice = getIntent().getStringExtra("flightCost");

        mHotelPrice = getIntent().getStringExtra("hotelPrice");
        String mCurrency = getIntent().getStringExtra("hotelCurrency");

        // == Setting up the ViewPager ==
        mHotelImages = new ArrayList<>();
        mCustomPagerAdapter = new CustomHotelDetailsAdapter(mHotelImages, getApplicationContext());

        mViewPager = findViewById(R.id.hotelpager);
        mViewPager.setAdapter(mCustomPagerAdapter);

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.hoteldetailstitlebartext);
        toolbarTitle.setTextColor(getColor(R.color.white));

        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        Toolbar mToolbar = findViewById(toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
        TextView hotelName = findViewById(R.id.hotelName);
        hotelName.setText(getIntent().getStringExtra("hotelName"));

        fetchHotelDetails(hotelId);

        mBookTravel = findViewById(R.id.book_travel);
        mBookTravel.setOnClickListener(this);


        final Drawable bookIcon = getDrawable(R.drawable.ic2_takeoff);
        if (bookIcon != null) {
            bookIcon.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
            mBookTravel.setImageDrawable(bookIcon);
        }
        mBookTravel.setVisibility(View.VISIBLE);    // hide until a room is selected

        mRatingView = findViewById(R.id.hotelRating);
        mPerNight = findViewById(R.id.hotelMaxprice);
        mDescription  = findViewById(R.id.hotelDescription);
        mHotelWelcomeMessage = findViewById(R.id.hotelWelcomeMessage);
        mHotelImportantInformation = findViewById(R.id.hotelImportantinformation);
        mHotelAddress = findViewById(R.id.address);
        mHotelCheckIn = findViewById(R.id.checkIn);
        mHotelCheckout = findViewById(R.id.checkOut);
        mReviewScore = findViewById(R.id.reviewScore);

        mCheckInLayout = findViewById(R.id.checkInLayout);
        mCheckOutLayout = findViewById(R.id.checkOutLayout);
        mHotelMessageHolder = findViewById(R.id.hotelMessageHolder);

        mLoadFader = findViewById(R.id.loadFader);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Bundle bundle = getIntent().getExtras();
        mDestination = (SingleDestination) bundle.getSerializable("destination");

        mPriceNotice = findViewById(R.id.priceDetail);
        mPriceNotice.setTextColor(getColor(R.color.white));
        mPriceNotice.setVisibility(View.VISIBLE);
        updateBookButton();

        final ObjectAnimator pulseButton = ObjectAnimator.ofPropertyValuesHolder(mBookTravel,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        pulseButton.setDuration(500);
        pulseButton.setRepeatCount(1);
        pulseButton.setRepeatMode(ValueAnimator.REVERSE);
        pulseButton.start();
        pulseButton.addListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pulseButton.start();
                    }
                }, 1500);
            }
        });

        Button reviewsButton = findViewById(R.id.hotelReviews);
        reviewsButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Utilities.hideUI(this);
        com.github.clans.fab.FloatingActionButton bookTravel = findViewById(R.id.book_travel);
        final Drawable bookIcon = getDrawable(R.drawable.ic2_takeoff);
        if (bookIcon != null) {
            bookIcon.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
            bookTravel.setImageDrawable(bookIcon);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {

        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent();
        intent.putExtra("hotelId", mHotel.getHotelId());
        intent.putExtra("hotelState", mHotel.isSavedHotel());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_travel:
                goBook();
                break;
            case R.id.shareIcon:
                shareSavedItem();
                break;
            case R.id.new_favourite:
            case R.id.saveIcon:
                saveUnsaveItem();
                break;
            case R.id.hotelReviews:
                showReviews();
                break;
        }
    }

    private void saveUnsaveItem() {

        boolean saved = setSaveIcon();

        if (saved) {
            removeItem();
            mHotel.setSavedHotel(false);
        } else {
            saveItem();
            mHotel.setSavedHotel(true);
        }
        setSaveIcon();

    }

    private void removeItem() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.removesaveditemmessage));
        mProgressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<BaseResponse> call = apiService.deleteSavedItem(token, mDestination.getCityId(), mHotel.getHotelId());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                mProgressDialog.dismiss();

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

        final ProgressDialog progressDialog = Utility.ProgressDialog("Saving...", HotelDetailActivity.this);

        final int hotelId = mHotel.getHotelId();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiService.saveItem(token, mCityId, hotelId, mHotel.getAvgRate(), mDestination.getFlightCost(), 0, "");

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {
                int status = response.body().getStatus();
                if (status == 1) {
                    progressDialog.dismiss();

                    Toast.makeText(mContext, "Saved", Toast.LENGTH_SHORT).show();
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

    private boolean setSaveIcon() {

        boolean saved = mHotel.isSavedHotel();
        if (saved) {
            mShareIcon.setVisibility(View.VISIBLE);
            mNewFavouriteIcon.setImageResource(R.drawable.ic_favorite_white_24dp);
        } else {
            mShareIcon.setVisibility(View.GONE);
            mNewFavouriteIcon.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }

        return saved;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapsInitializer.initialize(mContext);
        mapView.onResume();

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                showMap();
            }
        });
    }

    private void fetchHotelDetails(Integer hotelId) {
        final ProgressDialog progressDialog = new ProgressDialog(HotelDetailActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Hotel Details");

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        token = mBackend.getUserToken();
        Log.d(TAG, "Token: " + token);

        Call<HotelDetails> call = apiService.getHotelDetails(token, hotelId);
        call.enqueue(new Callback<HotelDetails>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<HotelDetails>call, Response<HotelDetails> response) {

                HotelDetails hotelDetails = response.body();
                if (hotelDetails != null) {
                    mHotel = hotelDetails.getHotel();
                    ArrayList<String> images = mHotel.getImages();

                    if (images.size() > 10) {
                        images = new ArrayList<>(images.subList(0, 10));
                    }

                    if (!images.isEmpty()) {
                        mHotelImages = images;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        builder.setMessage("Sorry, we couldn't find any images for this hotel");
                        builder.setPositiveButton("OK", null);
                        builder.show();
                    }

                    mRatingView.setText("Rating: " + mHotel.getRating().toString());

                    mPerNight.setText("Hotel cost pp: " + Utilities.getCurrencySymbol(TakeMeAway.getBackend().getCurrentUser().getCurrencyCode()) + mHotelPrice);

                    mDescription.setText(mHotel.getHotelDescription());

                    // some of this data might not be available
                    String temporary;
                    String temporary2;
                    temporary = mHotel.getHotelierWelcome();
                    if (temporary == null || temporary.equals("")) {
                        mHotelMessageHolder.setVisibility(View.GONE);
                    } else {
                        mHotelWelcomeMessage.setText(temporary);
                    }

                    temporary = mHotel.getImportantInformation();
                    if (temporary != null) {
                        mHotelImportantInformation.setText(temporary);
                    } else {
                        mHotelInfoHolder = findViewById(R.id.hotelInfoHolder);
                        mHotelInfoHolder.setVisibility(View.GONE);
                    }

                    temporary = mHotel.getAddress();
                    temporary2 = mHotel.getZip();
                    if (temporary != null) {
                        if (temporary2 != null) {
                            mHotelAddress.setText(temporary + ", " + temporary2);
                        } else {
                            mHotelAddress.setText(temporary);
                        }
                    } else {
                        mHotelAddress.setVisibility(View.GONE);
                    }

                    temporary = mHotel.getCheckinFrom();
                    temporary2 = mHotel.getCheckinTo();
                    if (temporary != null && !temporary.equals("")) {
                        if (temporary2 != null && !temporary2.equals("")) {
                            mHotelCheckIn.setText("From " + temporary + " until " + temporary2);
                        } else {
                            mHotelCheckIn.setText("From " + temporary);
                        }
                    } else {
                        mCheckInLayout.setVisibility(View.GONE);
                    }

                    temporary = mHotel.getCheckoutFrom();
                    temporary2 = mHotel.getCheckoutTo();
                    if (temporary != null && !temporary.equals("")) {
                        if (temporary2 != null && !temporary2.equals("")) {
                            mHotelCheckout.setText("From " + temporary + " until " + temporary2);
                        } else {
                            mHotelCheckout.setText("From " + temporary);
                        }
                    } else {
                        mCheckOutLayout.setVisibility(View.GONE);
                    }

                    mReviewScore.setText(mHotel.getRating().toString());

                    LatLng latlng = new LatLng(mHotel.getHotelLat(), mHotel.getHotelLon());

                    MoveMap(latlng);
                    AddMapMarker(latlng, mHotel.getName());

                    ((TextView) findViewById(R.id.amenitiesContent1)).setText(Html.fromHtml(mHotel.getAmenities1()));
                    ((TextView) findViewById(R.id.amenitiesContent2)).setText(Html.fromHtml(mHotel.getAmenities2()));

                } else {
                    // test mode
                    String uri = getIntent().getStringExtra("testImage");
                    mHotelImages = new ArrayList<>();
                    mHotelImages.add(uri);
                    mHotelImages.add(uri);
                    mHotelImages.add(uri);
                    mHotelImages.add(uri);
                }
                mCustomPagerAdapter.setmHotelImages(mHotelImages);
                mCustomPagerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<HotelDetails>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private void clearSelections() {

        for(int index = 0; index < mRoomholder.getChildCount(); ++index) {
            View room = mRoomholder.getChildAt(index);
            room.setBackgroundColor(getColor(R.color.white));
            ImageView selected = room.findViewById(R.id.roomSelected);
            selected.setVisibility(GONE);
        }

    }

    private void goBook() {

        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("cityId", mDestination.getCityId());
        intent.putExtra("hotelId", hotelId);
        intent.putExtra("hotelPrice", Integer.parseInt(mHotelPrice));
        intent.putExtra("flightCost", mFlightPrice);

        Bundle bundle = new Bundle();
        bundle.putSerializable("destination", mDestination);
        intent.putExtras(bundle);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void goHome() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void AddMapMarker(LatLng latlng, String title) {
        Drawable infoIcon = getDrawable(R.drawable.ic2_hotel);
        if (infoIcon != null) {
            infoIcon.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
            try {
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(infoIcon);

                mGoogleMap.addMarker(new MarkerOptions()
                        .icon(markerIcon)
                        .position(latlng)
                        .title(title)
                );
            } catch (Exception e) {
                Log.d(TAG, "Google map failed to instantiate properly");
            }
        }
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void MoveMap(LatLng latlng) {
        try {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15f);
            mGoogleMap.moveCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d("hoteldetail", "no google maps");
        }
    }

    private void showMap() {

        Intent intent = new Intent(mContext, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("destination", mDestination);
        intent.putExtras(bundle);

        bundle = new Bundle();
        bundle.putSerializable("hotel", mHotel);
        intent.putExtras(bundle);

        startActivity(intent);

    }

    private void showReviews() {

        Intent intent = new Intent(mContext, HotelReviewsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("hotel", mHotel);
        intent.putExtras(bundle);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    private void shareSavedItem() {
        String shareHash = Utilities.md5(String.valueOf(String.valueOf(mCityId) + ":" + hotelId));

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check this out on TakeMeAway! We could be there next weekend! https://takemeaway.io/tma/" + shareHash;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TakeMeAway");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void updateBookButton() {
        String cost = Utilities.getCurrencySymbol(mBackend.getCurrentUser().getCurrencyCode()) + String.valueOf(Utilities.getCurrentTripPrice());
        mPriceNotice.setText(cost);
        doBounceTextAnimation(mPriceNotice);
    }

}
