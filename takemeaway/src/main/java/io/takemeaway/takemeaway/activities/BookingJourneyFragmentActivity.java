package io.takemeaway.takemeaway.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.BookingPagesAdapter;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */

/**
 *
 * Holds the two fragments used to show Skyscanner and booking.com websites
 *
 */
public class BookingJourneyFragmentActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager mViewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context mContext = this;

        Utilities.hideUI(this);

        Backend mBackend = TakeMeAway.getBackend();
        String token = mBackend.getUserToken();

        if (token == null) {
            goHome();
        }

        setContentView(R.layout.activity_bookingjourney_holder);

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.bookingfragmenttitle);
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));


        if (getSupportActionBar() != null) {
            final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
            if (upArrow != null) {
                upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mViewPager = findViewById(R.id.view_pager);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("uris");

        BookingPagesAdapter mBookingPagesAdapter = new BookingPagesAdapter(getSupportFragmentManager(), bundle);

        mViewPager.setAdapter(mBookingPagesAdapter);

        TabLayout tabLayout =findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        switchToTab(bundle.getInt("tab"));

    }

    public void switchToTab(int tab) {
        mViewPager.setCurrentItem(tab);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onClick(View view) {

    }
}

