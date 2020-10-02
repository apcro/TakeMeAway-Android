package io.takemeaway.takemeaway.activities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toolbar;

import io.takemeaway.takemeaway.R;

import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
/**
 * TakeMeAway
 * Created by Tom Gordon
 */
public class ExternalBookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context mContext = getApplicationContext();

        setContentView(R.layout.documents_terms);

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.bookingactivitytitle);
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
            getActionBar().setDisplayShowHomeEnabled(true);
        }

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);

        String mUrl = getIntent().getStringExtra("uri");

        if (mUrl.equals("")) {
        } else {
            webView.loadUrl(mUrl);
        }

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

}
