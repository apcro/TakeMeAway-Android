package io.takemeaway.takemeaway.activities;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.github.clans.fab.FloatingActionMenu;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.HotelPagerAdapter;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.HotelAvailabilities;
import io.takemeaway.takemeaway.datamodels.Hotels;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.datamodels.SingleHotelAvailability;
import io.takemeaway.takemeaway.datamodels.UserDetails;
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
public class HotelActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static final String TAG = HotelActivity.class.getSimpleName();

    HotelPagerAdapter mHotelPagerAdapter;
    ViewPager mViewPager;

    public ArrayList<SingleHotel> mHotels;

    private Integer mInitialHotelLoadCount = 6;

    private String token;

    private Backend mBackend;

    private Context mContext;

    private int mCityId;
    private String mCityName;
    private String mFlightCost;
    private LatLng mCityLatLon;

    private ImageView mSaveIcon;
    private com.github.clans.fab.FloatingActionButton mNewFavouriteIcon;
    private ImageView mShareIcon;

    private RelativeLayout mLoadfader;

    private ProgressDialog mProgressDialog;

    private SingleDestination mDestination;

    MapView mapView;
    GoogleMap mGoogleMap;
    private boolean mLoadedNewHotels = false;
    private String mUserAction;

    private TextView mPriceNotice;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        setContentView(R.layout.activity_hotels);

        int loaderImage = getIntent().getIntExtra("imageId", Utilities.getRandomLoaderImage());
        ImageView loaderView = findViewById(R.id.loaderImage);
        loaderView.setImageResource(loaderImage);

        mCityId = getIntent().getIntExtra("cityId", 0);
        if (mCityId == 0) {
            ShowFailure();
        }

        Bundle bundle = getIntent().getExtras();
        mDestination = (SingleDestination) bundle.getSerializable("destination");

        if (mDestination == null) {
            // something went wrong
            ShowFailure();
        }

        mUserAction = "book";

        mContext = this;

        mBackend = TakeMeAway.getBackend();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mCityName = getIntent().getStringExtra("cityName");
        if (mCityName == "") {
            mCityName = mDestination.getCityName();
        }
        mFlightCost = getIntent().getStringExtra("flightCost");

        mCityLatLon = new LatLng(getIntent().getFloatExtra("lat", UserDetails.HOME_AIRPORT_LAT), getIntent().getFloatExtra("lon", UserDetails.HOME_AIRPORT_LON));

        // @TODO check the intent for an existing hotelId, and if there is one, add that to the beginning of the list
        int extraHotelId = getIntent().getIntExtra("hotelId", 0);

        token = mBackend.getUserToken();

        mProgressDialog = new ProgressDialog(HotelActivity.this, R.style.AppTheme_Dark_Dialog);

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(String.format(getString(R.string.hotesltitlebartext), mCityName));
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = findViewById(toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundResource(R.drawable.dark50);

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
        mapView = findViewById(R.id.mapHolder);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        FloatingActionMenu hotelFab = findViewById(R.id.hotel_fab_menu);
        final com.github.clans.fab.FloatingActionButton moreInfo = findViewById(R.id.hotel_information);
        com.github.clans.fab.FloatingActionButton bookTravel = findViewById(R.id.book_travel);

        mNewFavouriteIcon = findViewById(R.id.new_favourite);

        hotelFab.setClosedOnTouchOutside(true);

        moreInfo.setOnClickListener(this);

        bookTravel.setOnClickListener(this);

        mNewFavouriteIcon.setOnClickListener(this);

        Drawable bookIcon = getDrawable(R.drawable.ic2_takeoff);
        if (bookIcon != null) {
            bookIcon.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
            bookTravel.setImageDrawable(bookIcon);
        }

        Drawable moreIcon = getDrawable(R.drawable.ic2_more);
        if (moreIcon != null) {
            moreIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        Drawable infoIcon = getDrawable(R.drawable.ic2_hotel);
        if (infoIcon != null) {
            infoIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            moreInfo.setImageDrawable(infoIcon);
        }

        Drawable newFavIcon = getDrawable(R.drawable.ic_favorite_border_white_24dp);
        if (newFavIcon != null) {
            newFavIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mNewFavouriteIcon.setImageDrawable(newFavIcon);
        }

        mLoadfader = findViewById(R.id.loadFader);

        if (token == null) {
            mInitialHotelLoadCount = 3;
        }

        mSaveIcon = findViewById(R.id.saveIcon);
        mSaveIcon.setVisibility(GONE);


        mShareIcon = findViewById(R.id.shareIcon);
        Drawable toolbarShareIcon = getDrawable(R.drawable.ic_share_white_24dp);
        if (toolbarShareIcon != null) {
            toolbarShareIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mShareIcon.setImageDrawable(toolbarShareIcon);
        }
        mShareIcon.setOnClickListener(this);

        mHotels = mDestination.getHotels();

        if (mHotels == null) {
            mHotels = new ArrayList<>();
            fetchHotels(mCityId, mInitialHotelLoadCount, extraHotelId, mFlightCost);
            mLoadedNewHotels = true;
        } else {
            mLoadedNewHotels = false;

            mInitialHotelLoadCount = mHotels.size();
            mLoadfader.animate().setDuration(100).alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mProgressDialog.dismiss();
                }
            });

            mBackend.setCurrentHotelPrice(mHotels.get(0).getAvgRate());

            mPriceNotice = findViewById(R.id.priceDetail);
            mPriceNotice.setTextColor(getColor(R.color.white));
            mPriceNotice.setVisibility(View.VISIBLE);
            updateBookButton();
        }

        mHotelPagerAdapter = new HotelPagerAdapter(mHotels, getApplicationContext());

        mViewPager = findViewById(R.id.hotelpager);
        mViewPager.setAdapter(mHotelPagerAdapter);
        mViewPager.setOffscreenPageLimit(mInitialHotelLoadCount);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffset == 0.0f) {
                    SingleHotel thisHotel = mHotels.get(position);
                    LatLng latlng = new LatLng(thisHotel.getHotelLat(), thisHotel.getHotelLon());
                    MoveMap(latlng);
                    setSaveIcon(position);
                    Log.d(TAG, "Position: " + position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                SingleHotel thisHotel = mHotels.get(position);
                Log.d(TAG, String.valueOf(thisHotel));
                mBackend.setSelectedHotel(thisHotel);
                mBackend.setCurrentHotelPrice(thisHotel.getAvgRate());
                updateBookButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        TabLayout tabLayout = findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mViewPager, true);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            float oldX = 0, newX = 0, sens = 5;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        newX = event.getX();
                        if (Math.abs(oldX - newX) < sens) {
                            int hotel = mViewPager.getCurrentItem();
                            showHotelInformation(mHotels.get(hotel));
                            return true;
                        }
                        oldX = 0;
                        newX = 0;
                        break;
                }

                return false;
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        Utilities.hideUI(this);
    }

    private void updateBookButton() {
        if (token != null) {
            String cost = Utilities.getCurrencySymbol(mBackend.getCurrentUser().getCurrencyCode()) + String.valueOf(Utilities.getCurrentTripPrice());
            mPriceNotice.setText(cost);
            doBounceTextAnimation(mPriceNotice);
        } else {
            mPriceNotice.setVisibility(GONE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MapsInitializer.initialize(mContext);
        mapView.onResume();

        if (!mLoadedNewHotels) {
            updateMapMarkers();
            LatLng latlng = new LatLng(mHotels.get(0).getHotelLat(), mHotels.get(0).getHotelLon());
            MoveMap(latlng);
        }
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
            mGoogleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(TAG, "Googlemap wasn't instantiated");
        }

    }

    @Override
    public boolean onSupportNavigateUp() {

        Map<String, String> articleParams = new HashMap<>();
        articleParams.put("from", "hotels page");
        articleParams.put("to", "destination cards");
        articleParams.put("token", TakeMeAway.getBackend().getUserToken());
        FlurryAgent.logEvent("Transition", articleParams);

        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        int hotel = mViewPager.getCurrentItem();
        switch (v.getId()) {
            case R.id.hotel_information:
                mUserAction = "moreInfo";
                showHotelInformation(mHotels.get(hotel));
                break;
            case R.id.new_favourite:
            case R.id.saveIcon:
                mUserAction = "saveIcon";
                saveUnsaveItem();
                break;
            case R.id.save_travel:
                mUserAction = "saveTravel";
                saveItem();
                break;
            case R.id.book_travel:
                mUserAction = "book";
                goBook(mHotels.get(hotel));
                break;
            case R.id.shareIcon:
                shareSavedItem();
                break;
        }
    }

    private void shareSavedItem() {
        int position = mViewPager.getCurrentItem();
        SingleHotel hotel = mHotels.get(position);
        String shareHash = Utilities.md5(String.valueOf(String.valueOf(mCityId) + ":" + hotel.getHotelId()));

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check this out on TakeMeAway! We could be there next weekend! https://takemeaway.io/tma/" + shareHash;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TakeMeAway");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private boolean setSaveIcon(int position) {
        Drawable toolbarSaveIcon;

        SingleHotel hotel = mHotels.get(position);
        boolean saved = hotel.isSavedHotel();
        if (saved) {
            toolbarSaveIcon = getDrawable(R.drawable.ic_favorite_white_24dp);
            mShareIcon.setVisibility(View.VISIBLE);
            mNewFavouriteIcon.setImageResource(R.drawable.ic_favorite_white_24dp); //            setImageDrawable(toolbarSaveIcon);
        } else {
            toolbarSaveIcon = getDrawable(R.drawable.ic_favorite_border_white_24dp);
            mShareIcon.setVisibility(View.INVISIBLE);
            mNewFavouriteIcon.setImageResource(R.drawable.ic_favorite_border_white_24dp); //            setImageDrawable(toolbarSaveIcon);
        }

        if (toolbarSaveIcon != null) {
            toolbarSaveIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mSaveIcon.setImageDrawable(toolbarSaveIcon);
            mSaveIcon.setVisibility(GONE);
        }


        return saved;
    }

    private void fetchHotels(int cityId, int count, int extraHotelId, String flightCost) {

        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Loading Hotels...");
        mProgressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<Hotels> call = apiService.findHotel(token, cityId, count, extraHotelId, flightCost);
        call.enqueue(new Callback<Hotels>() {
            @Override
            public void onResponse(Call<Hotels>call, Response<Hotels> response) {

                List<SingleHotel> hotels = response.body().getHotels();

                if (hotels != null) {

                    final int size = hotels.size();

                    // if the list has got too big, shrink it. We never want more than 9
                    if (mHotels.size() >= 9) {
                        mHotels = new ArrayList<>(mHotels.subList(3, 9));
                        RemoveMapMarkers();
                    }

                    ArrayList<Integer> hotelIds = new ArrayList<>();
                    for (int i = 0; i < size; i++) {
                        SingleHotel hotel = hotels.get(i);
                        mHotels.add(hotel);

                        hotelIds.add(hotel.getHotelId());
                        LatLng latlng;
                        if (hotel.getHotelLat() == 0.0f && hotel.getHotelLon() == 0.0f) {
                            latlng = mCityLatLon;
                        } else {
                            latlng = new LatLng(hotel.getHotelLat(), hotel.getHotelLon());
                        }

                        if (i == 0) {
                            MoveMap(latlng);
                        }
                        AddMapMarker(latlng, hotel.getName());

                    }

                    mHotelPagerAdapter.notifyDataSetChanged();
                    mLoadfader.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                        }
                    });

                    checkHotelsAvailability(hotelIds);
                    setSaveIcon(0);

                    mBackend.setCurrentHotelPrice(mHotels.get(0).getAvgRate());
                    mPriceNotice = findViewById(R.id.priceDetail);
                    mPriceNotice.setTextColor(getColor(R.color.white));
                    mPriceNotice.setVisibility(View.VISIBLE);
                    updateBookButton();

                } else {
                    mProgressDialog.dismiss();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage("Sorry, we couldn't find any hotels this time");
                    builder.setPositiveButton("OK", null);
                    builder.setCancelable(false);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            onBackPressed();
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onFailure(Call<Hotels>call, Throwable t) {
                Log.e(TAG, t.toString());

                mProgressDialog.dismiss();

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setMessage("Sorry, we couldn't find any hotels");
                builder.setPositiveButton("OK", null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        goHome();
                    }
                });
                builder.setCancelable(false);
                builder.show();

            }
        });
    }

    private void updateMapMarkers() {
        int size = mHotels.size();
        for (int i = 0; i < size; i++) {
            SingleHotel hotel = mHotels.get(i);

            LatLng latlng;
            if (hotel.getHotelLat() == 0.0f && hotel.getHotelLon() == 0.0f) {
                latlng = mCityLatLon;
            } else {
                latlng = new LatLng(hotel.getHotelLat(), hotel.getHotelLon());
            }

            if (i == 0) {
                MoveMap(latlng);
            }
            AddMapMarker(latlng, hotel.getName());

        }
    }
    private void showHotelInformation(SingleHotel hotel) {
        if (token == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Sorry");
            builder.setCancelable(false);
            builder.setMessage("You need to be registered to see hotel information.");
            builder.setNeutralButton("Cancel", null);
            builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goRegister();
                }
            });
            builder.setNegativeButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goLogin();
                }
            });
            builder.show();
        } else {
            Intent intent = new Intent(mContext, HotelDetailActivity.class);
            intent.putExtra("hotelId", hotel.getHotelId());
            intent.putExtra("testImage", hotel.getHotelPhoto());
            intent.putExtra("hotelName", hotel.getName());
            intent.putExtra("cityId", mCityId);
            intent.putExtra("cityName", mCityName);
            intent.putExtra("flightCost", mFlightCost);

            intent.putExtra("hotelPrice", String.valueOf(Math.round(hotel.getRooms().get(0).getPrice())));

            Bundle bundle = new Bundle();
            bundle.putSerializable("destination", mDestination);
            intent.putExtras(bundle);

            bundle = new Bundle();
            bundle.putSerializable("hotel", hotel);
            intent.putExtras(bundle);

            startActivityForResult(intent, 1);
            overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                int returnedHotelId = data.getIntExtra("hotelId", 0);
                boolean returnedHotelState = data.getBooleanExtra("hotelState", false);
                if (returnedHotelId != 0) {
                    // set the local saved state here, which is actually this position
                    int position = mViewPager.getCurrentItem();
                    SingleHotel hotel = mHotels.get(position);
                    hotel.setSavedHotel(returnedHotelState);
                    mHotels.set(position, hotel);
                    setSaveIcon(position);
                }
            }
        }
    }

    private void RemoveMapMarkers() {
        mGoogleMap.clear();
    }

    private void saveUnsaveItem() {

        int position = mViewPager.getCurrentItem();
        SingleHotel hotel = mHotels.get(position);

        boolean saved = setSaveIcon(position);

        if (saved) {
            removeItem();
            hotel.setSavedHotel(false);
        } else {
            saveItem();
            hotel.setSavedHotel(true);
        }
        mHotels.set(position, hotel);
        setSaveIcon(mViewPager.getCurrentItem());

    }

    private void removeItem() {
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getString(R.string.removesaveditemmessage));
        mProgressDialog.show();

        final int position = mViewPager.getCurrentItem();
        int hotelId = mHotels.get(position).getHotelId();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<BaseResponse> call = apiService.deleteSavedItem(token, mCityId, hotelId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {
                mProgressDialog.dismiss();

                Map<String, String> articleParams = new HashMap<>();
                articleParams.put("city_hotel_ids", mCityId + ":" + mHotels.get(position).getHotelId());
                articleParams.put("city_name", mCityName);
                articleParams.put("token", TakeMeAway.getBackend().getUserToken());
                FlurryAgent.logEvent("RemovedSavedItem", articleParams);
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
        setSaveIcon(position);
    }

    private void saveItem() {

        if (token == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Sorry");
            builder.setCancelable(false);
            builder.setMessage("You need to be registered to save a trip.");
            builder.setNeutralButton("Cancel", null);
            builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goRegister();
                }
            });

            builder.setNegativeButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goLogin();
                }
            });
            builder.show();
        } else {

            final ProgressDialog progressDialog = Utility.ProgressDialog("Saving...", HotelActivity.this);

            final SingleHotel hotel = mHotels.get(mViewPager.getCurrentItem());
            final int hotelId = hotel.getHotelId();

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<BaseResponse> call = apiService.saveItem(token, mCityId, hotelId, hotel.getAvgRate(), mDestination.getFlightCost(), 0, "");
            call.enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {
                    int status = response.body().getStatus();
                    if (status == 1) {
                        progressDialog.dismiss();

                        Map<String, String> articleParams = new HashMap<>();
                        articleParams.put("city_hotel_ids", mCityId + ":" + hotelId);
                        articleParams.put("city_name", mCityName);
                        articleParams.put("hotel_price", hotel.getAvgRate().toString());
                        articleParams.put("flight_price", mDestination.getFlightCost());
                        articleParams.put("token", TakeMeAway.getBackend().getUserToken());

                        articleParams.put("people", String.valueOf(TakeMeAway.getBackend().getCurrentUser().getPeople()));
                        articleParams.put("budget", String.valueOf(TakeMeAway.getBackend().getCurrentUser().getBudget()));
                        articleParams.put("weekend", TakeMeAway.getBackend().getCurrentUser().getTravel_dates());
                        FlurryAgent.logEvent("SavedTrip", articleParams);

                        Toast.makeText(mContext, "Hotel & Destination Saved", Toast.LENGTH_SHORT).show();
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

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.anim_slide_out_left, R.anim.anim_slide_in_right);
    }

    private void goRegister() {

        Map<String, String> articleParams = new HashMap<>();
        articleParams.put("from", "hotels page");
        articleParams.put("to", "register page");
        articleParams.put("token", TakeMeAway.getBackend().getUserToken());
        FlurryAgent.logEvent("Transition", articleParams);

        Intent intent = new Intent(this, RegisterActivity.class);

        // pass city/hotel data so we can come back here
        intent.putExtra("action", "register");
        intent.putExtra("cityId", mCityId);
        SingleHotel hotel = mHotels.get(mViewPager.getCurrentItem());
        intent.putExtra("hotelId", hotel.getHotelId());
        intent.putExtra("cityName", mCityName);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void goLogin() {


        Map<String, String> articleParams = new HashMap<>();
        articleParams.put("from", "hotels page");
        articleParams.put("to", "login page");
        articleParams.put("token", TakeMeAway.getBackend().getUserToken());
        FlurryAgent.logEvent("Transition", articleParams);

        Intent intent = new Intent(this, LoginActivity.class);

        // pass city/hotel data so we can go to booking
        SingleHotel hotel = mHotels.get(mViewPager.getCurrentItem());
        intent.putExtra("action", mUserAction);
        intent.putExtra("cityId", mCityId);
        intent.putExtra("hotelId", hotel.getHotelId());
        if (hotel.getRooms() != null) {
            intent.putExtra("hotelPrice", Math.round(hotel.getRooms().get(0).getPrice()));
        } else {
            intent.putExtra("hotelPrice", hotel.getAvgRate());
        }
        intent.putExtra("hotelCurrency", hotel.getCurrencycode());
        intent.putExtra("flightCost", mFlightCost);

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void goBook(SingleHotel hotel) {
        if (token == null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Sorry");
            builder.setMessage("You need to be registered to book a trip.");
            builder.setCancelable(false);
            builder.setNeutralButton(R.string.canceltext, null);
            builder.setNegativeButton(R.string.login, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goLogin();
                }
            });
            builder.setPositiveButton(R.string.registertext, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goRegister();
                }
            });
            builder.show();
        } else {

            Map<String, String> articleParams = new HashMap<>();
            articleParams.put("from", "hotels page");
            articleParams.put("to", "booking page");
            articleParams.put("token", TakeMeAway.getBackend().getUserToken());
            FlurryAgent.logEvent("Transition", articleParams);

            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("action", mUserAction);
            intent.putExtra("cityId", mCityId);
            intent.putExtra("hotelId", hotel.getHotelId());
            intent.putExtra("hotelPrice", Math.round(hotel.getRooms().get(0).getPrice()));
            intent.putExtra("hotelCurrency", hotel.getCurrencycode());
            intent.putExtra("flightCost", mFlightCost);

            Bundle bundle = new Bundle();
            bundle.putSerializable("destination", mDestination);
            intent.putExtras(bundle);

            bundle = new Bundle();
            bundle.putSerializable("hotel", hotel);
            intent.putExtras(bundle);

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void ShowFailure() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(HotelActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry);
        builder.setMessage("There was an error");
        builder.setPositiveButton(R.string.oktext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goHome();
            }
        });
        builder.show();
    }

    private void checkHotelsAvailability(final ArrayList<Integer> hotelIds) {

        Log.d(TAG, "check hotel availability");

        for (int i = 0; i < hotelIds.size(); i++) {
            RelativeLayout thisView = mViewPager.findViewWithTag(hotelIds.get(i));
            ProgressBar avail = thisView.findViewById(R.id.availabilityChecker);
            TextView noRooms = thisView.findViewById(R.id.noRoomsLeft);

            noRooms.setVisibility(View.GONE);

            if (token == null) {
                avail.setVisibility(View.GONE);
            } else {
                avail.setVisibility(View.VISIBLE);
            }
            TextView atThis = thisView.findViewById(R.id.roomsAvailable);
            atThis.setVisibility(View.GONE);

        }

        if (token == null) return;

        String hotel_ids = android.text.TextUtils.join(",", hotelIds);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        token = mBackend.getUserToken();

        Call<HotelAvailabilities> call = apiService.checkHotelAvailability(token, hotel_ids);
        call.enqueue(new Callback<HotelAvailabilities>() {
            @Override
            public void onResponse(Call<HotelAvailabilities>call, Response<HotelAvailabilities> response) {

                if (response.body() != null) {
                    int status = response.body().getStatus();
                    if (status == 1) {


                        for (int i = 0; i < hotelIds.size(); i++) {
                            RelativeLayout thisView = mViewPager.findViewWithTag(hotelIds.get(i));
                            ProgressBar avail = thisView.findViewById(R.id.availabilityChecker);
                            TextView noRooms = thisView.findViewById(R.id.noRoomsLeft);
                            noRooms.setVisibility(View.VISIBLE);
                            TextView atThis = thisView.findViewById(R.id.roomsAvailable);
                            atThis.setVisibility(View.GONE);
                            avail.setVisibility(GONE);
                        }


                        List<SingleHotelAvailability> availability = response.body().getHotels();
                        for (int i = 0; i < availability.size(); i++) {
                            SingleHotelAvailability thisHotel = availability.get(i);
                            int thisHotelId = thisHotel.getHotelId();

                            RelativeLayout thisView = mViewPager.findViewWithTag(thisHotelId);

                            if (thisView != null) {
                                ProgressBar avail = thisView.findViewById(R.id.availabilityChecker);
                                avail.setVisibility(View.GONE);
                                int rooms = thisHotel.getRooms().get(0).getRoomsAtThisprice();
                                TextView atThis = thisView.findViewById(R.id.roomsAvailable);

                                String roomsText;
                                if (rooms == 1) {
                                    roomsText = "Only 1 room left!";
                                } else {
                                    roomsText = String.valueOf(rooms) + " rooms available";
                                }

                                atThis.setText(roomsText);
                                atThis.setTextColor(getColor(R.color.white));
                                atThis.setVisibility(View.VISIBLE);

                                TextView perNight = thisView.findViewById(R.id.hotelMaxprice);
                                String textPerNight = String.format(
                                        getString(R.string.hotelCostBookingScreen),
                                        Utilities.getCurrencySymbol(thisHotel.getCurrencyCode()),
                                        String.valueOf(Math.round(thisHotel.getRooms().get(0).getPrice())
                                        ));
                                perNight.setText(textPerNight);

                                TextView noRooms = thisView.findViewById(R.id.noRoomsLeft);
                                noRooms.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        for (int i = 0; i < hotelIds.size(); i++) {
                            RelativeLayout thisView = mViewPager.findViewWithTag(hotelIds.get(i));
                            ProgressBar avail = thisView.findViewById(R.id.availabilityChecker);
                            TextView noRooms = thisView.findViewById(R.id.noRoomsLeft);
                            noRooms.setVisibility(View.VISIBLE);
                            avail.setVisibility(View.GONE);

                        }
                    }
                } else {
                    // @TODO deal with an empty body on a filters call
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Sorry");
                    builder.setMessage("Something went wrong. We've made a note of it.");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }

            }

            @Override
            public void onFailure(Call<HotelAvailabilities>call, Throwable t) {
                Log.e(TAG, t.toString());


            }
        });

    }

}

