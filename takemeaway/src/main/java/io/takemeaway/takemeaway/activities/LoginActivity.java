package io.takemeaway.takemeaway.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flurry.android.FlurryAgent;
import com.google.firebase.analytics.FirebaseAnalytics;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.responsemodels.LoginResponse;
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
public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private Context mContext;
    private String token;

    private Backend mBackend;
    private ProgressDialog mProgressDialog;

    private UserDetails mUserDetails;
    private Bitmap mProfileBitmap;

    private TwitterLoginButton twitterLoginButton;
    private CallbackManager callbackManager;

    private String loginType = "facebook";

    private Target mBitmapTarget;

    @BindView(R.id.name) EditText _emailText;
    @BindView(R.id.password) EditText _passwordText;
    @BindView(R.id.loginButton) Button _loginButton;
    @BindView(R.id.registerLink) TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("nwNcdaxLhqiX4b9RPVhhBnCXe", "m7B0n74CX6UHfRGZjIYBlkIWM7GAxYnPRbgijCqgxzWzYxWGC8"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        setContentView(R.layout.activity_login);

        Log.d("MAIN", "LoginActivity");

        mContext = getApplicationContext();

        ButterKnife.bind(this);

        mBackend = TakeMeAway.getBackend();

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.loginactivityheading);
        toolbarTitle.setTextColor(getColor(R.color.white));

        token = mBackend.getUserToken();

        Button mLogin = findViewById(R.id.loginButton);
        TextView mRegister = findViewById(R.id.registerLink);

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);

        Toolbar mToolbar = findViewById(toolbar);
        setActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getActionBar() != null) {
                getActionBar().setHomeAsUpIndicator(upArrow);

                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginType = "email";
                String username = _emailText.getText().toString();
                String password = _passwordText.getText().toString();
                userLogin(username, password,"");
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

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
    public void onClick(View v) {
    }

    private void userLogin(String username, String password, String type) {

        if (!validate() && type == "") {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        mProgressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
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
                    _loginButton.setEnabled(true);

                } else {

                    loadUser();

                }
            }

            @Override
            public void onFailure(Call<LoginResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                Toast.makeText(mContext, "Sorry, there was a problem", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                _loginButton.setEnabled(true);
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

                        Map<String, String> articleParams = new HashMap<String, String>();
                        articleParams.put("token", TakeMeAway.getBackend().getUserToken());
                        FlurryAgent.logEvent("login", articleParams);

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

        mBitmapTarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "Loaded bitmap");
                mUserDetails.setAvatarBitmap(bitmap);
                mBackend.saveCurrentUser(mUserDetails);
                goHome();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "Failed to load bitmap");
                mProfileBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic2_profile);
                mUserDetails.setAvatarBitmap(mProfileBitmap);
                mBackend.saveCurrentUser(mUserDetails);
                goHome();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG, "Preparing to load bitmap");
            }

        };

        mPicasso
                .build()
                .load(avatarUri)
                .error(R.drawable.ic_account_circle_white_24dp)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .centerCrop()
                .resize(pxFromDp(120), pxFromDp(120))

                .into(mBitmapTarget);
    }

    public int dpFromPx(float px) {
        return (int) (px / mContext.getResources().getDisplayMetrics().density);
    }

    private int pxFromDp(float dp) {
        return (int) (dp * mContext.getResources().getDisplayMetrics().density);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        if (loginType == mBackend.USER_FACEBOOK) {
            return valid;
        }

        if (loginType == mBackend.USER_TWITTER) {
            return valid;
        }

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 3) {
            _passwordText.setError("at least 3 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void goHome() {
        mProgressDialog.dismiss();
        Intent intent;
        int cityId = getIntent().getIntExtra("cityId", 0);
        if (cityId != 0) {
            intent = new Intent(this, BookingActivity.class);
            intent.putExtra("cityId", cityId);
            intent.putExtra("hotelId", getIntent().getIntExtra("hotelId", 0));
            intent.putExtra("hotelPrice", getIntent().getIntExtra("hotelPrice", 0));
            intent.putExtra("hotelCurrency", getIntent().getStringExtra("hotelCurrency"));
            intent.putExtra("flightCost", getIntent().getStringExtra("flightCost"));
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
