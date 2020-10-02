package io.takemeaway.takemeaway.ui;

import android.app.ProgressDialog;
import android.content.Context;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.activities.LoginActivity;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class Utility {

    public static ProgressDialog ProgressDialog(String text, Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(text);
        progressDialog.show();
        return progressDialog;
    }
}
