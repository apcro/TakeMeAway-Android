package io.takemeaway.takemeaway.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import io.takemeaway.takemeaway.R;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
@Layout(R.layout.drawer_item)
public class DrawerMenuItem {

    public static final int DRAWER_MENU_ITEM_PROFILE = 1;
    public static final int DRAWER_MENU_ITEM_ADMIN = 2;
    public static final int DRAWER_MENU_ITEM_REGISTER = 3;
    public static final int DRAWER_MENU_ITEM_SAVED = 4;
    public static final int DRAWER_MENU_ITEM_HELP = 5;
    public static final int DRAWER_MENU_ITEM_SETTINGS = 6;
    public static final int DRAWER_MENU_ITEM_TERMS = 7;
    public static final int DRAWER_MENU_ITEM_LOGOUT = 8;
    public static final int DRAWER_MENU_ITEM_LOGIN = 9;
    public static final int DRAWER_MENU_ITEM_FEEDBACK = 10;
    public static final int DRAWER_MENU_ITEM_PRIVACY = 11;

    private int mMenuPosition;
    private Context mContext;
    private DrawerCallBack mCallBack;

    @View(R.id.itemNameTxt)
    TextView itemNameTxt;

    @View(R.id.itemIcon)
    ImageView itemIcon;

    public DrawerMenuItem(Context context) {
        mContext = context;
    }

    public DrawerMenuItem(Context context, int menuPosition) {
        mContext = context;
        mMenuPosition = menuPosition;

    }

    public void SetMenuPosition(int position) {
        mMenuPosition = position;
    }

    @Resolve
    public void onResolved() {

        Drawable menuIcon;

        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                itemNameTxt.setText(R.string.profile);
                menuIcon = mContext.getDrawable(R.drawable.ic2_profile);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                itemNameTxt.setText(R.string.filters);
                menuIcon = mContext.getDrawable(R.drawable.ic2_filters);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                break;
            case DRAWER_MENU_ITEM_TERMS:
                menuIcon = mContext.getDrawable(R.drawable.ic2_about);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                itemNameTxt.setText(R.string.terms);
                break;
            case DRAWER_MENU_ITEM_PRIVACY:
                menuIcon = mContext.getDrawable(R.drawable.ic2_about);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                itemNameTxt.setText(R.string.privacy);
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                menuIcon = mContext.getDrawable(R.drawable.ic2_logout);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                itemNameTxt.setText(R.string.logout);
                break;

            case DRAWER_MENU_ITEM_LOGIN:
                menuIcon = mContext.getDrawable(R.drawable.ic2_login);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                itemNameTxt.setText(R.string.login);
                break;
            case DRAWER_MENU_ITEM_REGISTER:
                itemNameTxt.setText(R.string.registertext);
                menuIcon = mContext.getDrawable(R.drawable.ic2_register);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                break;
            case DRAWER_MENU_ITEM_SAVED:
                itemNameTxt.setText(R.string.saveditemstext);
                menuIcon = mContext.getDrawable(R.drawable.ic2_save);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                break;
            case DRAWER_MENU_ITEM_ADMIN:
                itemNameTxt.setText(R.string.adminmenutext);
                menuIcon = mContext.getDrawable(R.drawable.ic2_faq);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                break;
            case DRAWER_MENU_ITEM_HELP:
                itemNameTxt.setText(R.string.faqtext);
                menuIcon = mContext.getDrawable(R.drawable.ic2_about);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                break;
            case DRAWER_MENU_ITEM_FEEDBACK:
                itemNameTxt.setText(R.string.feedbacktext);
                menuIcon = mContext.getDrawable(R.drawable.ic2_feedback);
                menuIcon.setColorFilter(mContext.getColor(R.color.brand_dark), PorterDuff.Mode.SRC_ATOP);
                itemIcon.setImageDrawable(menuIcon);
                break;
        }
    }

    @Click(R.id.mainView)
    public void onMenuItemClick() {
        switch (mMenuPosition){
            case DRAWER_MENU_ITEM_PROFILE:
                if(mCallBack != null) mCallBack.onProfileMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SETTINGS:
                if(mCallBack != null) mCallBack.onSettingsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_TERMS:
                if(mCallBack != null) mCallBack.onTermsMenuSelected();
                break;
            case DRAWER_MENU_ITEM_LOGOUT:
                if(mCallBack != null) mCallBack.onLogoutMenuSelected();
                break;
            case DRAWER_MENU_ITEM_LOGIN:
                if(mCallBack != null) mCallBack.onLoginMenuSelected();
                break;
            case DRAWER_MENU_ITEM_REGISTER:
                if(mCallBack != null) mCallBack.onRegisterMenuSelected();
                break;
            case DRAWER_MENU_ITEM_ADMIN:
                if(mCallBack != null) mCallBack.onAdminMenuSelected();
                break;
            case DRAWER_MENU_ITEM_SAVED:
                if(mCallBack != null) mCallBack.onSavedItemMenuSelected();
                break;
            case DRAWER_MENU_ITEM_HELP:
                if(mCallBack != null) mCallBack.onHelpItemMenuSelected();
                break;
            case DRAWER_MENU_ITEM_FEEDBACK:
                if(mCallBack != null) mCallBack.onFeedbackItemMenuSelected();
                break;
            case DRAWER_MENU_ITEM_PRIVACY:
                if(mCallBack != null) mCallBack.onPrivacyItemMenuSelected();
                break;
        }
    }

    public void setDrawerCallBack(DrawerCallBack callBack) {
        mCallBack = callBack;
    }

    public interface DrawerCallBack{
        void onProfileMenuSelected();
        void onSettingsMenuSelected();
        void onTermsMenuSelected();
        void onLogoutMenuSelected();
        void onLoginMenuSelected();
        void onRegisterMenuSelected();
        void onAdminMenuSelected();
        void onSavedItemMenuSelected();
        void onHelpItemMenuSelected();
        void onFeedbackItemMenuSelected();
        void onPrivacyItemMenuSelected();
    }
}