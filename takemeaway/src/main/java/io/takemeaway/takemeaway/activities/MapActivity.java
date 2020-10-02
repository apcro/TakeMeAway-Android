package io.takemeaway.takemeaway.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class MapActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    private static final String TAG = MapActivity.class.getSimpleName();

    private String token;

    private Backend mBackend;

    private Context mContext;

    private int mCityId;
    private String mCityName;
    private String mFlightCost;
    private LatLng mCityLatLon;
    private LatLng mHotelLatLon;

    private ImageView mSaveIcon;
    private ImageView mShareIcon;

    private ProgressDialog mProgressDialog;

    private SingleDestination mDestination;
    private SingleHotel mHotel;

    MapView mapView;
    GoogleMap mGoogleMap;
    private boolean mLoadedNewHotels = false;
    private String mUserAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        overridePendingTransition(R.anim.anim_zoom_in, 0);
        setContentView(R.layout.activity_map);

        Bundle bundle = getIntent().getExtras();
        mDestination = (SingleDestination) bundle.getSerializable("destination");

        if (mDestination == null) {
            // something went wrong
            ShowFailure();
        }

        mHotel = (SingleHotel) bundle.getSerializable("hotel");

        mContext = this;

        mBackend = TakeMeAway.getBackend();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        mCityName = mDestination.getCityName();

        mCityLatLon = new LatLng(mDestination.getLat(), mDestination.getLon());
        mHotelLatLon = new LatLng(mHotel.getHotelLat(), mHotel.getHotelLon());

        token = mBackend.getUserToken();

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText(mHotel.getName());
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
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
        mapView = (MapView) findViewById(R.id.mapHolder);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

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

        // hotel
        Drawable infoIcon = getDrawable(R.drawable.ic2_hotel);
        if (infoIcon != null) {
            infoIcon.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
        }
        BitmapDescriptor markerIcon = getMarkerIconFromDrawable(infoIcon);
        AddMapMarker(mHotelLatLon, mHotel.getName(), markerIcon);

        // airport
        infoIcon = getDrawable(R.drawable.ic2_takeoff);
        if (infoIcon != null) {
            infoIcon.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
        }
        markerIcon = getMarkerIconFromDrawable(infoIcon);
        AddMapMarker(mCityLatLon, mHotel.getName(), markerIcon);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mHotelLatLon);
        builder.include(mCityLatLon);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.15); // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mGoogleMap.moveCamera(cu);

    }

    @Override
    public void onResume(){
        super.onResume();
        Utilities.hideUI(this);
    }

    public void AddMapMarker(LatLng latlng, String title, BitmapDescriptor markerIcon) {

        try {
            mGoogleMap.addMarker(new MarkerOptions()
                    .icon(markerIcon)
                    .position(latlng)
                    .title(title)
            );
        } catch (Exception e) {
            Log.d(TAG, "Google map failed to instantiate properly");
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
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15f);
        try {
            mGoogleMap.animateCamera(cameraUpdate);
        } catch (Exception e) {
            Log.d(TAG, "Googlemap wasn't instantiated");
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        overridePendingTransition(0, R.anim.anim_zoom_out);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    private void RemoveMapMarkers() {
        mGoogleMap.clear();
    }

    private void ShowFailure() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.sorry);
        builder.setMessage("There was an error");
        builder.setPositiveButton(R.string.oktext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        builder.show();
    }

}
