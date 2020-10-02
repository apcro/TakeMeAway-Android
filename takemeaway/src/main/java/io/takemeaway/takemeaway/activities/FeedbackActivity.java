package io.takemeaway.takemeaway.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static io.takemeaway.takemeaway.R.id.toolbar;

/**
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */

public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FeedbackActivity.class.getSimpleName();

    private String token;

    private EditText feedbackText;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.activity_feedback);

        Backend mBackend = TakeMeAway.getBackend();

        mContext = this;

        token = mBackend.getUserToken();

        if (token == null) {
            goHome();
        }

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.feedbackActivityTitle);
        toolbarTitle.setTextColor(getColor(R.color.white));

        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getColor(R.color.brand_dark));

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }
        feedbackText = findViewById(R.id.feedbackText);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TakeMeAway.MAINFONT)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.sendButton) {
            sendFeedback();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void sendFeedback() {

        final ProgressDialog progressDialog = new ProgressDialog(FeedbackActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sending feedback...");
        progressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiService.sendFeedback(token, feedbackText.getText().toString()
        );
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {

                if (response.code() == 400) {
                    Gson gson = new GsonBuilder().create();
                    BaseResponse mError;
                    try {
                        mError = gson.fromJson(response.errorBody().string(), BaseResponse.class);
                        String errormessage = mError.getErrorMessage();
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        builder.setTitle("Sorry");
                        builder.setMessage("There was an error sending your feedback. \n" + errormessage);
                        builder.setPositiveButton("OK", null);
                        builder.show();

                    } catch (IOException e) {
                        // handle failure to read error
                    }
                } else {

                    int status = response.body().getStatus();

                    if (status == 0) {
                        // we have an error
                        String errormessage = response.body().getErrorMessage();
                        Toast.makeText(mContext, errormessage, Toast.LENGTH_SHORT).show();
                    } else {
                        progressDialog.dismiss();
                        goHome();
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
