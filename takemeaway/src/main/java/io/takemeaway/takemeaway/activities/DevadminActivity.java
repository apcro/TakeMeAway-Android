package io.takemeaway.takemeaway.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toolbar;

import io.takemeaway.takemeaway.R;

import static io.takemeaway.takemeaway.R.id.toolbar;

/**
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
/**
 * Created by cro on 17/05/15.
 */
public class DevadminActivity extends Activity {

    private static final String TAG = DevadminActivity.class.getSimpleName();

    private Context mContext;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mContext = getApplicationContext();

        setContentView(R.layout.documents_terms);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText("Dev Admin");
        toolbarTitle.setTextColor(getColor(R.color.white));

        mToolbar = (Toolbar) findViewById(toolbar);
        setActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getActionBar().setHomeAsUpIndicator(upArrow);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onNavigateUp() {
        goHome();
        return true;
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
