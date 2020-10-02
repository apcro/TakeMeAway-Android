package io.takemeaway.takemeaway.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
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

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.CustomHotelDetailsAdapter;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.HotelDetails;
import io.takemeaway.takemeaway.datamodels.SavedItem;
import io.takemeaway.takemeaway.responsemodels.OneSavedItemResponse;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SavedItemDetailsActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private static final String TAG = SavedItemDetailsActivity.class.getSimpleName();

    private String token;

    private Backend mBackend;
    private Context mContext;
    private Activity mActivity;
    ProgressDialog progressDialog;

    ImageView cityLoader, hotelLoader, cityImage;
    private RelativeLayout mLoadfader;

    int cityId;
    int hotelId;

    CustomHotelDetailsAdapter mCustomPagerAdapter;
    ViewPager mViewPager;

    private TextView mHotelWelcomeMessage;
    private TextView mHotelImportantInformation;
    private TextView mHotelAddress;
    private TextView mHotelCheckIn;
    private TextView mHotelCheckout;

    private RelativeLayout mCheckInLayout;
    private RelativeLayout mCheckOutLayout;
    private RelativeLayout mHotelMessageHolder;
    private RelativeLayout mHotelInfoHolder;
    private TextView mRatingView, mRatingBox;
    private TextView mPerNight;
    private TextView mDescription;

    public ArrayList<String> mHotelImages;

    MapView mapView;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_saveditemdetails);

        int loaderImage = Utilities.getRandomLoaderImage();
        ImageView loaderView = (ImageView) findViewById(R.id.loaderImage);
        loaderView.setImageResource(loaderImage);
        mLoadfader = (RelativeLayout) findViewById(R.id.loadFader);

        mBackend = TakeMeAway.getBackend();
        mContext = this;
        mActivity = this;

        token = mBackend.getUserToken();

        if (token == null) {
            goHome();
            finish();
        }

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText("");
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        progressDialog = new ProgressDialog(SavedItemDetailsActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loadingdetailsdialog));
        progressDialog.show();

        cityId = getIntent().getIntExtra("cityId", 0);
        hotelId = getIntent().getIntExtra("hotelId", 0);

        TextView cityName = (TextView) findViewById(R.id.cityName);
        cityName.setText(getIntent().getStringExtra("cityName"));

        FloatingActionButton bookTravel = (FloatingActionButton) findViewById(R.id.book_travel);
        bookTravel.setOnClickListener(this);

        cityLoader = (ImageView) findViewById(R.id.cityProgress);
        hotelLoader = (ImageView) findViewById(R.id.hotelProgress);
        cityImage = (ImageView) findViewById(R.id.cityImage);

        // == Setting up the ViewPager ==
        mHotelImages = new ArrayList<>();
        mCustomPagerAdapter = new CustomHotelDetailsAdapter(mHotelImages, getApplicationContext());

        mViewPager = (ViewPager) findViewById(R.id.hotelpager);
        mViewPager.setAdapter(mCustomPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        mapView = (MapView) findViewById(R.id.mapHolder);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        bookTravel = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.book_travel);
        bookTravel.setOnClickListener(this);
        Drawable bookIcon = getDrawable(R.drawable.ic2_takeoff);
        if (bookIcon != null) {
            bookIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            bookTravel.setImageDrawable(bookIcon);
        }

        FloatingActionButton deleteItem = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.remove_item);
        deleteItem.setOnClickListener(this);
        Drawable deleteIcon = getDrawable(R.drawable.ic2_delete);
        if (deleteIcon != null) {
            deleteIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            deleteItem.setImageDrawable(deleteIcon);
        }

        getSavedItem(cityId, hotelId);

        Utilities.hideUI(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Google map ready");
        map = googleMap;
        MapsInitializer.initialize(mContext);
        mapView.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_travel:
                goBook();
                break;

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        mBackend = TakeMeAway.getBackend();
        token = mBackend.getUserToken();
        Utilities.hideUI(this);
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     *
     */
    private void getSavedItem(int cityId, int hotelId) {

        final int hotelDetailId = hotelId;
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<OneSavedItemResponse> call = apiService.loadSavedItem(token, cityId, hotelId);
        call.enqueue(new Callback<OneSavedItemResponse>() {
            @Override
            public void onResponse(Call<OneSavedItemResponse>call, Response<OneSavedItemResponse> response) {
                progressDialog.dismiss();
                int status = response.body().getStatus();
                if (status == 1) {
                    SavedItem item = response.body().getItem();

                    Picasso.with(mContext).load(item.getCityimage()).fit().centerCrop().into(cityImage);

                    AddMapMarker(new LatLng(item.getAirportLat(), item.getAirportLon()), item.getCityname(), "airport");

                    TextView hotelTextView = (TextView) findViewById(R.id.hotelName);
                    hotelTextView.setText(item.getHotelname());

                    fetchHotelDetails(hotelDetailId);

                    LatLng hotelLatLng = new LatLng(item.getHotelLat(), item.getHotelLon());
                    AddMapMarker(hotelLatLng, item.getHotelname(), "hotel");
                    MoveMap(hotelLatLng);

                    Utilities.hideUI(mActivity);
                }

            }

            @Override
            public void onFailure(Call<OneSavedItemResponse>call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });

    }


    public void AddMapMarker(LatLng latlng, String title, String type) {

        Drawable infoIcon = null;
        switch (type) {
            case "hotel":
                infoIcon = getDrawable(R.drawable.ic2_hotel);
                break;
            case "airport":
                infoIcon = getDrawable(R.drawable.ic2_land);
                break;
        }
        if (infoIcon != null) {
            infoIcon.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
        }

        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(infoIcon);

        map.addMarker(new MarkerOptions()
                .icon(markerIcon)
                .position(latlng)
                .title(title)
        );
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15f);
        map.moveCamera(cameraUpdate);
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void fetchHotelDetails(Integer hotelId) {
        final ProgressDialog progressDialog = new ProgressDialog(SavedItemDetailsActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Hotel Details");
        progressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<HotelDetails> call = apiService.getHotelDetails(token, hotelId);
        call.enqueue(new Callback<HotelDetails>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<HotelDetails>call, Response<HotelDetails> response) {
                hotelLoader.setVisibility(View.GONE);
                HotelDetails hotelDetails = response.body();
                if (hotelDetails != null) {
                    SingleHotel hotel = hotelDetails.getHotel();
                    ArrayList<String> images = hotel.getImages();

                    if (images.size() > 10) {
                        images = new ArrayList<>(images.subList(0, 10));
                    }

                    if (!images.isEmpty()) {
                        mHotelImages = images;
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        builder.setMessage("Sorry, we couldn't find any images for this hotel");
                        builder.setPositiveButton(R.string.oktext, null);
                        builder.show();
                    }

                    mRatingView = (TextView) findViewById(R.id.hotelRating);
                    mRatingBox = (TextView) findViewById(R.id.reviewScore);
                    mPerNight = (TextView) findViewById(R.id.hotelMaxprice);
                    mDescription  = (TextView) findViewById(R.id.hotelDescription);
                    mHotelWelcomeMessage = (TextView) findViewById(R.id.hotelWelcomeMessage);
                    mHotelImportantInformation = (TextView) findViewById(R.id.hotelImportantinformation);
                    mHotelAddress = (TextView) findViewById(R.id.address);
                    mHotelCheckIn = (TextView) findViewById(R.id.checkIn);
                    mHotelCheckout = (TextView) findViewById(R.id.checkOut);

                    mCheckInLayout = (RelativeLayout) findViewById(R.id.checkInLayout);
                    mCheckOutLayout = (RelativeLayout) findViewById(R.id.checkOutLayout);
                    mHotelMessageHolder = (RelativeLayout) findViewById(R.id.hotelMessageHolder);

                    CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                            .setDefaultFontPath(TakeMeAway.MAINFONT)
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    );

                    mRatingView.setText("Rating: " + hotel.getRating().toString());
                    mRatingBox.setText(hotel.getRating().toString());
                    mPerNight.setText("Per night: " + Utilities.getCurrencySymbol(hotel.getCurrencycode()) + hotel.getAvgRate().toString());
                    mDescription.setText(hotel.getHotelDescription());

                    // some of this data might not be available
                    String temporary;
                    String temporary2;
                    temporary = hotel.getHotelierWelcome();
                    if (temporary == null || temporary.equals("")) {
                        mHotelMessageHolder.setVisibility(View.GONE);
                    } else {
                        mHotelWelcomeMessage.setText(temporary);
                    }

                    temporary = hotel.getImportantInformation();
                    if (temporary != null) {
                        mHotelImportantInformation.setText(temporary);
                    } else {
                        mHotelInfoHolder = (RelativeLayout) findViewById(R.id.hotelInfoHolder);
                        mHotelInfoHolder.setVisibility(View.GONE);
                    }

                    temporary = hotel.getAddress();
                    temporary2 = hotel.getZip();
                    if (temporary != null) {
                        if (temporary2 != null) {
                            mHotelAddress.setText(temporary + ", " + temporary2);
                        } else {
                            mHotelAddress.setText(temporary);
                        }
                    } else {
                        mHotelAddress.setVisibility(View.GONE);
                    }

                    temporary = hotel.getCheckinFrom();
                    temporary2 = hotel.getCheckinTo();
                    if (temporary != null && !temporary.equals("")) {
                        if (temporary2 != null && !temporary2.equals("")) {
                            mHotelCheckIn.setText("From " + temporary + " until " + temporary2);
                        } else {
                            mHotelCheckIn.setText("From " + temporary);
                        }
                    } else {
                        mCheckInLayout.setVisibility(View.GONE);
                    }

                    temporary = hotel.getCheckoutFrom();
                    temporary2 = hotel.getCheckoutTo();
                    if (temporary != null && !temporary.equals("")) {
                        if (temporary2 != null && !temporary2.equals("")) {
                            mHotelCheckout.setText("From " + temporary + " until " + temporary2);
                        } else {
                            mHotelCheckout.setText("From " + temporary);
                        }
                    } else {
                        mCheckOutLayout.setVisibility(View.GONE);
                    }
                }
                mCustomPagerAdapter.setmHotelImages(mHotelImages);
                mCustomPagerAdapter.notifyDataSetChanged();

                mLoadfader.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(Call<HotelDetails>call, Throwable t) {
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
            }
        });
    }

    private void goBook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.savedbookingwarningheading);
        builder.setMessage(R.string.savedbookingwarning);
        builder.setNegativeButton(R.string.canceltext, null);
        builder.setPositiveButton(R.string.oktext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reallyGoBook();
            }
        });
        builder.show();

    }

    private void reallyGoBook() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("cityId", cityId);
        intent.putExtra("hotelId", hotelId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


}
