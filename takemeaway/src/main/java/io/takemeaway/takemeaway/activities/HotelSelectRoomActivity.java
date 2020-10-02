package io.takemeaway.takemeaway.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.HotelDetails;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */

public class HotelSelectRoomActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = HotelSelectRoomActivity.class.getSimpleName();

    private String token;

    private Toolbar mToolbar;

    TakeMeAway takeMeAway = (TakeMeAway) getApplication();
    private Backend mBackend;

    private com.github.clans.fab.FloatingActionButton bookTravel;

    private Context mContext;

    private int hotelId;
    private int cityId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_hoteldetails);

        mContext = this;

        mBackend = takeMeAway.getBackend();

        token = mBackend.getUserToken();

        if (token == null) {
            goHome();
            finish();
        }

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText("Select your room");
        toolbarTitle.setTextColor(getColor(R.color.white));

        mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView hotelName = (TextView) findViewById(R.id.hotelName);
        hotelName.setText(getIntent().getStringExtra("hotelName"));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    private void fetchRoomDetails(Integer hotelId) {
        final ProgressDialog progressDialog = new ProgressDialog(HotelSelectRoomActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading Room Details");
        progressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        token = mBackend.getUserToken();
        Log.d(TAG, "Token: " + token);

        Call<HotelDetails> call = apiService.getRoomDetails(token, hotelId);
        call.enqueue(new Callback<HotelDetails>() {
            @Override
            public void onResponse(Call<HotelDetails>call, Response<HotelDetails> response) {

            }

            @Override
            public void onFailure(Call<HotelDetails>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
            }
        });
    }

    private void goBook() {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("cityId", cityId);
        intent.putExtra("hotelId", hotelId);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

    /**
     *
     */
    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
