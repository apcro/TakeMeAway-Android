package io.takemeaway.takemeaway.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.squareup.picasso.Picasso;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.services.Backend;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
@NonReusable
@Layout(R.layout.drawer_header)
public class DrawerHeader {

    private static final String TAG = "DrawerHeaderClass";

    String username;
    String firstName;
    String lastName;
    String email;

    @View(R.id.profileImageView)
    ImageView profileImage;

    @View(R.id.nameTxt)
    TextView nameTxt;

    @View(R.id.emailTxt)
    TextView emailTxt;

    Backend mBackend;

    public DrawerHeader() {

        this.mBackend = TakeMeAway.getBackend();
    }

    @SuppressLint("SetTextI18n")
    @Resolve
    public void onResolved() {
        Log.d(TAG, "Creating Drawer Header");

        UserDetails currentuser = mBackend.getCurrentUser();

        username = currentuser.getUsername();
        firstName = currentuser.getName();
        lastName = currentuser.getSurname();
        email = currentuser.getMail();

        nameTxt.setText(firstName + ' ' + lastName);
        emailTxt.setText(email);

        profileImage.setImageBitmap(mBackend.getCurrentUser().getAvatarBitmap());
    }
}
