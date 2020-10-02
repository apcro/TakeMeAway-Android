package io.takemeaway.takemeaway.activities;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.ProfileFilterAdaptor;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleAirport;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.fragments.FragmentProfile;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */

public class ProfileFilterActivity extends AppCompatActivity {

    private static final String TAG = ProfileFilterActivity.class.getSimpleName();

    private Context mContext;

    private ViewPager mViewPager;
    private Backend mBackend;
    private String mToken;
    private UserDetails mCurrentUser;
    ProfileFilterAdaptor mProfileFilterAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilefilters);

        mBackend = TakeMeAway.getBackend();
        mContext = this;
        mCurrentUser = mBackend.getCurrentUser();

        mToken = mBackend.getUserToken();

        this.mContext = this;

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText("Settings");
        toolbarTitle.setTextColor(getColor(R.color.white));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mProfileFilterAdapter = new ProfileFilterAdaptor(getSupportFragmentManager());

        mViewPager.setAdapter(mProfileFilterAdapter);
        tabLayout.setupWithViewPager(mViewPager);

        switchToTab(getIntent().getIntExtra("tabtoshow", 0));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // hide keyboard for filters
                if (position == 1) {
                    View focus = getCurrentFocus();
                    if (focus != null) {
                        hideKeyboard(focus);
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    private void hideKeyboard(View v) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void switchToTab(int tab){
        mViewPager.setCurrentItem(tab);
    }

}