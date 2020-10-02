package io.takemeaway.takemeaway.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toolbar;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class HotelReviewsActivity extends Activity {

    private static final String TAG = HotelReviewsActivity.class.getSimpleName();

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context mContext = getApplicationContext();

        setContentView(R.layout.reviews_webview);

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(getString(R.string.reviews_webview_toolbar));
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = findViewById(toolbar);
        setActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getActionBar().setHomeAsUpIndicator(upArrow);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        SingleHotel hotel = (SingleHotel) bundle.getSerializable("hotel");

        String hoteluri = hotel.getUrl();
        String[] separated = hoteluri.split("/");
        String stub = separated[(separated.length-1)];
        stub = stub.replace(".html", "");

        Log.d(TAG, "Stub: " + stub);

        String uri = "https://www.booking.com/reviewlist.html?" +
                "tmpl=reviewlistpopup;" +
                "pagename="+stub+";" +
                "hrwt=1;" +
                "cc1="+hotel.getCountryCode()+";" +
                "target_aid=1362236;" +
                "aid=1362236;" +
                "target_label=TakeMeAway;" +
                "target_lang=en;";

        WebView webView = findViewById(R.id.webView2);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(uri);

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
