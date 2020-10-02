package io.takemeaway.takemeaway.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.util.Random;

import io.takemeaway.takemeaway.services.Backend;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */

public class TakeMeAway extends Application {

    public static final String preferencesFileName = "TakeMeAwayPreferences";

    public static final String MAINFONT = "fonts/Lato-Semibold.ttf";

    public static final String DESTINATION_IMAGE_URI = "https://cdn1.takemeaway.io/images/destinations/";
    public static final String AVATAR_URI = "https://cdn1.takemeaway.io/images/user/avatars";

    private static Backend mBackend;

    @Override
    public void onCreate() {
        super.onCreate();
        new FlurryAgent.Builder()
                .withLogEnabled(true)
                .withCaptureUncaughtExceptions(true)
                .withLogLevel(Log.DEBUG)
                .build(this, "TTN7BG86F595S6BM6HSC");

        AndroidThreeTen.init(this);

        mBackend = new Backend(this);

    }

    public TakeMeAway() {
        super();
    }

    public static Backend getBackend() {
        return mBackend;
    }

    public static Context context() {
        return mBackend.getApplication().getApplicationContext();
    }

    public static String DestinationImageUri() {
        Random r = new Random();
        int i1 = r.nextInt(4 - 1) + 1;
        return "https://cdn"+String.valueOf(i1)+".takemeaway.io/images/destinations/";
    }

}
