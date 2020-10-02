package io.takemeaway.takemeaway.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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

public class PrivacyActivity extends Activity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context mContext = getApplicationContext();

        setContentView(R.layout.documents_terms);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.privacypolictyheadingtext);
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
        setActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getActionBar() != null) {
                getActionBar().setHomeAsUpIndicator(upArrow);
                getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        WebView webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.takemeaway.io/m.privacy");

    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }

}
