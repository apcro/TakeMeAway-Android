package io.takemeaway.takemeaway.ui;

import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import io.takemeaway.takemeaway.R;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
@NonReusable
@Layout(R.layout.drawer_header_logged_out)
public class DrawerHeaderLoggedOut {

    @View(R.id.profileImageView)
    ImageView profileImage;

    @View(R.id.nameTxt)
    TextView nameTxt;

    @View(R.id.emailTxt)
    TextView emailTxt;

    public DrawerHeaderLoggedOut() {}

    @Resolve
    public void onResolved() {
        if (nameTxt != null) {
            nameTxt.setText("");
            emailTxt.setText("");
            profileImage.setImageResource(R.drawable.ic_account_circle_white_24dp);
        }
    }
}
