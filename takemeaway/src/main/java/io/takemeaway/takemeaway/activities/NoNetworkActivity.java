package io.takemeaway.takemeaway.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.novoda.merlin.MerlinsBeard;

import io.takemeaway.takemeaway.R;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class NoNetworkActivity extends AppCompatActivity implements View.OnClickListener {

    private MerlinsBeard merlinsBeard;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonetwork);

        Button goHome = (Button) findViewById(R.id.button3);
        goHome.setOnClickListener(this);

        // check network activity first
        merlinsBeard = MerlinsBeard.from(this);

        progressDialog = new ProgressDialog(NoNetworkActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking Connectivity");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button3:
                goHome();
        }
    }

    private void goHome() {
        progressDialog.show();
        if (merlinsBeard.isConnected()) {
            progressDialog.dismiss();
            Intent intent;
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    progressDialog.dismiss();
                }
            }, 600);
        }
    }
}
