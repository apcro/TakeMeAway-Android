package io.takemeaway.takemeaway.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flurry.android.FlurryAgent;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.responsemodels.LoginResponse;
import io.takemeaway.takemeaway.responsemodels.RegisterResponse;
import io.takemeaway.takemeaway.responsemodels.UserResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private EditText emailField;
    private EditText passwordField;
    private EditText nameField;
    private String token;

    private Context mContext;
    private Backend mBackend;
    private ProgressDialog mProgressDialog;

    private UserDetails mUserDetails;
    private Bitmap mProfileBitmap;

    private String loginType = "facebook";

    private TwitterLoginButton twitterLoginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MAIN", "RegisterActivity");
        super.onCreate(savedInstanceState);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("nwNcdaxLhqiX4b9RPVhhBnCXe", "m7B0n74CX6UHfRGZjIYBlkIWM7GAxYnPRbgijCqgxzWzYxWGC8"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_register);

        mContext = getApplicationContext();

        mBackend = TakeMeAway.getBackend();

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.registeractivityheadingtext);
        toolbarTitle.setTextColor(getColor(R.color.brand_dark));

        Button mRegister = (Button) findViewById(R.id.registerButton);
        mRegister.setOnClickListener(this);

        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);

        final Drawable upArrow = getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        nameField = (EditText) findViewById(R.id.name);
        emailField = (EditText) findViewById(R.id.email);
        passwordField = (EditText) findViewById(R.id.password);

        mProgressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        twitterLoginButton = findViewById(R.id.twitter_login_button);

        twitterLoginButton.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitter");
                loginType = "twitter";
                String twitterUsername = result.data.getUserName();
                String userid = String.valueOf(result.data.getUserId());

                // call login with type=twitter
                userLogin(userid, twitterUsername, "twitter");

            }

            @Override
            public void failure(TwitterException exception) {
                Log.d(TAG, "twitter fail");
            }
        });

        callbackManager = CallbackManager.Factory.create();
        LoginButton fb_loginButton = findViewById(R.id.fb_login_button);
        fb_loginButton.setReadPermissions("email");
        fb_loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook login");
                userLogin(loginResult.getAccessToken().getToken(), loginResult.getAccessToken().getUserId(), "facebook");

                Map<String, String> articleParams = new HashMap<String, String>();
                articleParams.put("signUpMethod", "byFacebook");
                articleParams.put("token", TakeMeAway.getBackend().getUserToken());
                FlurryAgent.logEvent("NewSignup", articleParams);
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook login cancelled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook login error");
            }
        });

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onSupportNavigateUp() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.registerButton: {
                if (validate()) {
                    userRegister(String.valueOf(emailField.getText()), String.valueOf(passwordField.getText()), String.valueOf(nameField.getText()));
                }
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("enter a valid email address");
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (password.isEmpty() || password.length() < 3) {
            passwordField.setError("at least 3 alphanumeric characters");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    private void userRegister(String emailaddress, String password, String name) {

        mProgressDialog.setMessage("Registering...");
        mProgressDialog.show();

        String mLocale = Locale.getDefault().toString();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<RegisterResponse> call = apiService.register(emailaddress, password, name, mLocale);
        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse>call, Response<RegisterResponse> response) {

                int status = response.body().getStatus();

                if (status == 0) {
                    String errormessage = response.body().getError();

                    mProgressDialog.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage(errormessage);
                    builder.setPositiveButton("OK", null);
                    builder.show();

                } else {

                    String token = response.body().getToken();
                    Log.d(TAG, "Register Token: " + token);

                    mBackend.saveUserToken(token);

                    loadUser(token);

                }
            }

            @Override
            public void onFailure(Call<RegisterResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private void GoToHotels(int cityId, int hotelId, String cityName) {
        Intent intent = new Intent(this, HotelActivity.class);
        intent.putExtra("cityId", cityId);
        intent.putExtra("hotelId", hotelId);
        intent.putExtra("cityName", cityName);
        startActivity(intent);
    }

    private void loadUser(@Nullable String token) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<UserResponse> call = apiService.userLoad(token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse>call, Response<UserResponse> response) {
                if (response.code() != 200) {
                    Log.d(TAG, "Error: " + response.errorBody().toString());
                    mProgressDialog.dismiss();
                } else {
                    int status = response.body().getStatus();

                    if (status == 1) {
                        UserDetails userDetails = response.body().getUserdata();
                        mBackend.saveCurrentUser(userDetails);

                        mProgressDialog.dismiss();

                        int cityId = getIntent().getIntExtra("cityId", 0);
                        int hotelId = getIntent().getIntExtra("hotelId", 0);
                        String cityName = getIntent().getStringExtra("cityName");

                        if (cityId != 0) {
                            // we've come from the hotels page
                            GoToHotels(cityId, hotelId, cityName);
                        }

                        Map<String, String> articleParams = new HashMap<String, String>();
                        articleParams.put("signUpMethod", "byEmail");
                        articleParams.put("token", TakeMeAway.getBackend().getUserToken());
                        FlurryAgent.logEvent("NewSignup", articleParams);

                        goHomeOrProfile();

                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Sorry");
                        builder.setMessage("We registered you, but couldn't log you in");
                        builder.setPositiveButton("OK", null);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                goLogin();
                            }
                        });
                        builder.show();

                        Log.d(TAG, "Error: status was 0");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

    }

    private void goHomeOrProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.word_welcome);
        builder.setMessage(R.string.on_register_notice);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.word_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goProfile();
            }
        });
        builder.setNegativeButton(R.string.word_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goHome();
            }
        });
        builder.show();
    }

    private void goProfile() {

        Intent intent = new Intent(this, ProfileFilterActivity.class);
        intent.putExtra("tabtoshow", 0);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void goLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (loginType == "facebook") {

            mBackend.setUserType(mBackend.USER_FACEBOOK);
            // facebook callback
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (loginType == "twitter") {
            mBackend.setUserType(mBackend.USER_TWITTER);
            // Pass the activity result to the login button.
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void userLogin(String username, String password, String type) {

        mProgressDialog.setMessage("Logging you in ...");
        mProgressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<LoginResponse> call = apiService.login(username, password, type);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse>call, Response<LoginResponse> response) {
                String token = response.body().getToken();

                mBackend.saveUserToken(token);

                int status = response.body().getStatus();

                if (status == 0) {
                    String errormessage = response.body().getError();
                    Toast.makeText(mContext, errormessage, Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                } else {

                    loadUser();

                }
            }

            @Override
            public void onFailure(Call<LoginResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private void loadUser() {
        if (token == null) {
            token = mBackend.getUserToken();
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<UserResponse> call = apiService.userLoad(token);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse>call, Response<UserResponse> response) {

                if (response.code() != 200) {
                    Log.d(TAG, "Error: " + response.errorBody().toString());
                } else {
                    int status = response.body().getStatus();

                    if (status == 1) {
                        mUserDetails = response.body().getUserdata();

                        // do we have a new devicetoken to save?
                        String saveDeviceToken = mBackend.getSharedPreferences("deviceTokenSaved");
                        if (saveDeviceToken != null) {
                            if (saveDeviceToken.equals("no")) {
                                Utilities.sendDeviceTokenToServer();
                            }
                        }

                        getAvatarBitmap();

                    } else {
                        Log.d(TAG, "Error: status was 0");
                        goHome();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });

    }

    private void getAvatarBitmap() {
        // update with local data
        String avatarUri = TakeMeAway.AVATAR_URI + mUserDetails.getAvatar();
        mUserDetails.setAvatar(avatarUri);

        Picasso.Builder mPicasso = new Picasso.Builder(mContext);
        mPicasso.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                Log.d(TAG, "Image load failed");
                Log.d(TAG, "URI: " + uri);
                exception.printStackTrace();
            }
        });

        Log.d(TAG, "Avatar URI: " + avatarUri);

        mPicasso.build()
                .load(avatarUri)
                .error(R.drawable.ic_account_circle_white_24dp)
                .centerCrop()
                .resize(pxFromDp(120), pxFromDp(120))

                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mUserDetails.setAvatarBitmap(bitmap);
                        mBackend.saveCurrentUser(mUserDetails);
                        goHome();
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        mProfileBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic2_profile);
                        mUserDetails.setAvatarBitmap(mProfileBitmap);
                        mBackend.saveCurrentUser(mUserDetails);
                        goHome();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                });
    }

    private int pxFromDp(float dp) {
        return (int) (dp * mContext.getResources().getDisplayMetrics().density);
    }

}
