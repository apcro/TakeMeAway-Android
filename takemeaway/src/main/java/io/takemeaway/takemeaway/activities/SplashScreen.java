package io.takemeaway.takemeaway.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;
import android.widget.TextView;

import com.mindorks.placeholderview.Utils;
import com.novoda.merlin.MerlinsBeard;
import com.twitter.sdk.android.core.models.User;

import java.util.List;

import io.takemeaway.takemeaway.BuildConfig;
import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.DestinationCard;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.responsemodels.DestinationsResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SplashScreen extends Activity {

    private static final String TAG = SplashScreen.class.getSimpleName();
    private AlertDialog mAlertDialog;
    private Context mContext;
    private Backend mBackend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mContext = this;

        Utilities.hideUI(this);

        mBackend = TakeMeAway.getBackend();
        String token  = mBackend.getUserToken();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        mAlertDialog = builder.create();

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void startMainActivity() {

        MerlinsBeard merlinsBeard = MerlinsBeard.from(this);
        Intent intent;
        if (merlinsBeard.isConnected()) {
            intent = new Intent(this, WelcomeActivity.class);
        } else {
            intent = new Intent(this, NoNetworkActivity.class);
        }
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
