package io.takemeaway.takemeaway.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
/**
 * TakeMeAway
 * Created by Tom Gordon
 *
 * Currently unused, can be used to display details about a given city
 *
 */
public class CityDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context mContext = getApplicationContext();

        setContentView(R.layout.activity_citydetails);

        Bundle bundle = getIntent().getExtras();
        SingleDestination mDestination = (SingleDestination) bundle.getSerializable("destination");

        String mCityName = mDestination.getCityName();

        // set up content
        TextView cityName = findViewById(R.id.cityName);
        TextView cityDescription = findViewById(R.id.cityDescription);
        cityName.setText(mDestination.getCityName());
        ImageView cityImageView = findViewById(R.id.cityImage);

        if (mDestination.getCityLongDescription() == "") {
            cityDescription.setVisibility(View.INVISIBLE);
            cityImageView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
            cityImageView.requestLayout();
        } else {
            cityDescription.setText(mDestination.getCityLongDescription());
        }

        Picasso.with(mContext).load(TakeMeAway.DestinationImageUri() + mDestination.getCityImage())
                .error(R.drawable.logo)
                .fit()
                .centerCrop()
                .into(cityImageView);

        // set up controls
        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(mCityName);
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        if (getActionBar() != null) {
            getActionBar().setHomeAsUpIndicator(upArrow);
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setDisplayShowHomeEnabled(true);
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

}
