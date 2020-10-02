package io.takemeaway.takemeaway.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;
import com.flurry.android.FlurryAgent;
import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipeDirectionalView;
import com.mindorks.placeholderview.Utils;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.listeners.ItemRemovedListener;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.takemeaway.takemeaway.ui.DrawerHeader;
import io.takemeaway.takemeaway.ui.DrawerHeaderLoggedOut;
import io.takemeaway.takemeaway.ui.DrawerMenuItem;
import io.takemeaway.takemeaway.ui.DrawerSeperator;
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
public class MainActivity
        extends AppCompatActivity
        implements
        DestinationCard.SwipeCallback,
        View.OnClickListener, DrawerMenuItem.DrawerCallBack
{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int FEEDBACK_THRESHOLD = 3;

    private Context mContext;

    private static Integer initialDestinationsToLoad = 2;
    private static Integer newDestinationsToLoad = 4;

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ProgressBar mInitialLoader;

    private Backend mBackend;

    private String token;

    private int mLoaderImage;
    private ImageView mLoaderView, mLoadingIconSpinner;
    private RelativeLayout mLoadFader, mNewLoader;

    protected Merlin merlin;

    private SwipeDirectionalView mSwipeView;
    private Point mWindowSize;
    private int mCardPadding = 25;

    private boolean mGoHotelsEnabled = true;
    private com.github.clans.fab.FloatingActionButton mFabUndo;
    private com.github.clans.fab.FloatingActionButton mFabFilters;
    private com.github.clans.fab.FloatingActionButton mSavedItems;

    private Picasso mPicasso;

    private AlertDialog mAlertDialog;

    private ProgressDialog mProgressDialog;

    private boolean mPreload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);


        setContentView(R.layout.activity_main);

        Utilities.hideUI(this);
        mBackend = TakeMeAway.getBackend();

        mContext = this;
        mPicasso = new Picasso.Builder(this).build();

        mPreload = true;

        mLoaderView = findViewById(R.id.loaderView);
        mLoadFader = findViewById(R.id.loadFader);
        mLoadFader.setVisibility(View.GONE);

        mNewLoader = findViewById(R.id.loadAnimation);
        mNewLoader.setVisibility(View.GONE);

        mSwipeView = findViewById(R.id.swipeStack);

        mLoadingIconSpinner = findViewById(R.id.loadingIconSpinner);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(mLoadingIconSpinner,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));

        animator.setInterpolator(new EasingInterpolator(Ease.BOUNCE_OUT));
        animator.setStartDelay(500);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

        mWindowSize = Utilities.getDisplaySize(getWindowManager());

        int mSwipeThreshold = 10;   // 20
        int mAnimationDuration = 200; //300
        mSwipeView.getBuilder()
                .setDisplayViewCount(initialDestinationsToLoad)
                .setIsUndoEnabled(true)
                .setSwipeVerticalThreshold(Utils.dpToPx(mSwipeThreshold))
                .setSwipeHorizontalThreshold(Utils.dpToPx(mSwipeThreshold))
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(mWindowSize.x - Utils.dpToPx(mCardPadding))
                        .setViewHeight(mWindowSize.y - Math.round((Utils.dpToPx(mCardPadding) * 1.5f)))
                        .setViewGravity(Gravity.TOP)
                        .setSwipeAnimTime(mAnimationDuration)
                        .setPaddingTop(65)
                        .setMarginLeft(mCardPadding * 2)
                        .setSwipeRotationAngle(15)
                        .setRelativeScale(0.01f));

        mSwipeView.addItemRemoveListener(new ItemRemovedListener() {

            @Override
            public void onItemRemoved(int count) {
                Log.d(TAG, "swipeview visible count: " + count);
                if (count < 2) {
                    fetchDestinations(newDestinationsToLoad);
                }
            }
        });

        com.github.clans.fab.FloatingActionButton mFab = findViewById(R.id.fabAdd);
        mFab.setOnClickListener(this);
        fetchDestinations(newDestinationsToLoad);

        mFabFilters = findViewById(R.id.filtersButton);
        mFabFilters.setOnClickListener(this);
        mFabFilters.setVisibility(View.VISIBLE);
        Drawable filterButtonIcon = getDrawable(R.drawable.icons8_slider_32);
        if (filterButtonIcon != null) {
            filterButtonIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mFabFilters.setImageDrawable(filterButtonIcon);
        }

        mSavedItems = findViewById(R.id.saveditemsButton);
        mSavedItems.setOnClickListener(this);
        mSavedItems.setVisibility(View.VISIBLE);
        Drawable savedItemsIcon = getDrawable(R.drawable.ic_favorite_white_24dp);
        if (savedItemsIcon != null) {
            savedItemsIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mSavedItems.setImageDrawable(savedItemsIcon);
        }

        mFabUndo = findViewById(R.id.fabUndo);
        mFabUndo.setOnClickListener(this);
        mFabUndo.setVisibility(View.GONE);
        Drawable undoIcon = getDrawable(R.drawable.ic_undo_white_24dp);
        if (undoIcon != null) {
            undoIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mFabUndo.setImageDrawable(undoIcon);
        }

        token = mBackend.getUserToken();

        mInitialLoader = findViewById(R.id.initialLoader);
        mInitialLoader.setVisibility(View.GONE);

        mProgressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);

        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);

        if (token != "") {
            mProgressDialog.setMessage(getString(R.string.loadingDestinationsWithHotels));
        } else {
            mProgressDialog.setMessage(getString(R.string.loadingDestinations));
        }

        mNewLoader.setVisibility(View.VISIBLE);

        mDrawer = findViewById(R.id.drawerLayout);
        mDrawerView = findViewById(R.id.drawerView);
        mToolbar = findViewById(R.id.toolbar);

        TextView toolbarTitle = findViewById(R.id.toolbarText);
        toolbarTitle.setText("");
        toolbarTitle.setTextColor(getColor(R.color.brand_dark));

        setupDrawer();

        merlin = new Merlin.Builder()
                .withConnectableCallbacks()
                .withDisconnectableCallbacks()
                .withBindableCallbacks()
                .build(mContext);

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                fetchDestinations(initialDestinationsToLoad);
            }

        });

        merlin.registerDisconnectable(new Disconnectable() {
            @Override
            public void onDisconnect() {

                Intent intent = new Intent(mContext, NoNetworkActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        merlin.bind();

        if (mBackend.getSharedPreferences("firstrun") == "yes") {
            mBackend.setSharedPreferences("firstrun", "no");
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Semibold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        mAlertDialog = builder.create();

        // update runcount and show feedback dialog if we need to
        String disableRunCountPreference = TakeMeAway.getBackend().getSharedPreferences("runcountdisable");
        if (disableRunCountPreference != "yes") {

            String runCountPreference = TakeMeAway.getBackend().getSharedPreferences("runcount");
            if (runCountPreference == null) {
                runCountPreference = "0";
            }
            int runCount = Integer.parseInt(runCountPreference);
            runCount++;

            if (runCount%FEEDBACK_THRESHOLD == 0) {
                getFeedback();
            }
        }
    }

    private void getFeedback() {
        // show feedback popup here

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setCancelable(false);
        builder.setTitle("Hi there");
        builder.setMessage("We'd love to hear from you! Please let us know how we're doing");
        builder.setPositiveButton("OK!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(mContext, FeedbackActivity.class);
                overridePendingTransition(0, 0);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAlertDialog.dismiss();
            }
        });
        builder.setNeutralButton("Don't bug me again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mAlertDialog.dismiss();
                TakeMeAway.getBackend().setSharedPreferences("runcountdisable", "yes");
            }
        });

        mAlertDialog = builder.create();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    mAlertDialog.show();
                }
            }
        });

    }

    @Override
    public void onSwipeUp(SingleDestination destination) {

    }

    @Override
    public void onSwipeLeft(SingleDestination destination) {
        if (mPreload) {
            fetchDestinations(initialDestinationsToLoad);
            mPreload = false;
        } else {
            fetchDestinations(newDestinationsToLoad);
        }

        mFabUndo.setVisibility(View.VISIBLE);
        checkDestinationViewCount();

        // log the swipe left
        logSwipeEvent("left", destination);

    }

    @Override
    public void onSwipeRight(final SingleDestination destination) {

        // log the swipe right
        logSwipeEvent("right", destination);

        // save it in the current session
        mBackend.setSelectedDestination(destination);
        mBackend.setCurrentFlightPrice(Integer.parseInt(destination.getFlightCost()));

        mLoadFader.setVisibility(View.VISIBLE);
        mLoadFader.setAlpha(0);
        mLoaderImage = Utilities.getRandomLoaderImage();
        mLoaderView.setImageResource(mLoaderImage);
        mLoadFader.animate().setDuration(200).alpha(1).withEndAction(new Runnable() {
            @Override
            public void run() {
                goHotel(destination);
            }
        }).start();

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Resolve
    private void setupDrawer() {
        if (token != null && token != "") {
            setLoggedInState();
        } else {
            setLoggedOutState();
        }

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        mDrawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Resolve
    private void setLoggedOutState() {
        mDrawerView.removeAllViews();
        DrawerMenuItem mLoginItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_LOGIN);
        mLoginItem.setDrawerCallBack(this);

        DrawerMenuItem mRegisterItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_REGISTER);
        mRegisterItem.setDrawerCallBack(this);
        DrawerMenuItem mPrivacyItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PRIVACY);
        mPrivacyItem.setDrawerCallBack(this);

        DrawerMenuItem mTermsItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_TERMS);
        mTermsItem.setDrawerCallBack(this);
        DrawerMenuItem mHelpItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_HELP);
        mHelpItem.setDrawerCallBack(this);

        mDrawerView
                .addView(new DrawerHeaderLoggedOut())
                .addView(mRegisterItem)
                .addView(mLoginItem)
                .addView(new DrawerSeperator())
                .addView(mTermsItem)
                .addView(mPrivacyItem)
                .addView(mHelpItem)
        ;

        ImageView toolbarProfilePic = findViewById(R.id.toolbarProfilePic);
        toolbarProfilePic.setVisibility(View.GONE);

        mDrawerView.refresh();

        mFabFilters.setVisibility(View.GONE);
        mSavedItems.setVisibility(View.GONE);
    }

    @Resolve
    private void setLoggedInState() {

        UserDetails currentUser = mBackend.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "Errror, currentuser wasn't stored properly");
            mBackend.Logout();
            mDrawer.closeDrawers();
            setLoggedOutState();
        } else {

            mDrawerView.removeAllViews();

            DrawerMenuItem mLogoutItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_LOGOUT);
            mLogoutItem.setDrawerCallBack(this);
            DrawerMenuItem mSettingsItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SETTINGS);
            mSettingsItem.setDrawerCallBack(this);
            DrawerMenuItem mProfileItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PROFILE);
            mProfileItem.setDrawerCallBack(this);
            DrawerMenuItem mTermsItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_TERMS);
            mTermsItem.setDrawerCallBack(this);

            DrawerMenuItem mPrivacyItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_PRIVACY);
            mPrivacyItem.setDrawerCallBack(this);

            DrawerMenuItem mSavedItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_SAVED);
            mSavedItem.setDrawerCallBack(this);
            DrawerMenuItem mHelpItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_HELP);
            mHelpItem.setDrawerCallBack(this);
            DrawerMenuItem mFeedbackItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_FEEDBACK);
            mFeedbackItem.setDrawerCallBack(this);

            DrawerHeader drawerHeader = new DrawerHeader();

            mDrawerView
                    .addView(drawerHeader)
                    .addView(mProfileItem)
                    .addView(mSettingsItem)
                    .addView(mSavedItem)
                    .addView(new DrawerSeperator())
                    .addView(mTermsItem)
                    .addView(mPrivacyItem)
                    .addView(mHelpItem)
                    .addView(mFeedbackItem)
                    .addView(new DrawerSeperator())
                    .addView(mLogoutItem);

            ImageView toolbarProfilePic = findViewById(R.id.toolbarProfilePic);
            String avatar = currentUser.getAvatar();
            Bitmap avatarBitmap = currentUser.getAvatarBitmap();
            Log.d(TAG, "Avatar: " + avatar);
            if (avatar != null && avatarBitmap != null) {
                Log.d(TAG, "we have loaded the bitmap from sharedpreferences");
                toolbarProfilePic.setImageBitmap(avatarBitmap);
                toolbarProfilePic.setVisibility(View.VISIBLE);
                toolbarProfilePic.setAlpha(0f);
                toolbarProfilePic.animate().alpha(1f).setDuration(150).start();
            }

            toolbarProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onProfileMenuSelected();
                }
            });

            mDrawerView.refresh();
        }
    }

    private void fetchDestinations(Integer count) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        int destinations = mSwipeView.getChildCount();
        if (destinations <= 1) {
            count = (initialDestinationsToLoad - destinations);
        }

        mLoadingIconSpinner.setVisibility(View.VISIBLE);

        Call<DestinationsResponse> call = apiService.findDestination(token, count);
        call.enqueue(new Callback<DestinationsResponse>() {
            @Override
            public void onResponse(Call<DestinationsResponse>call, Response<DestinationsResponse> response) {

                mLoadingIconSpinner.setVisibility(View.GONE);
                if (mAlertDialog.isShowing()) {
                    mAlertDialog.dismiss();
                }

                if (response.body() != null) {

                    int status = response.body().getStatus();
                    if (status == 1) {
                        List<SingleDestination> destinations = response.body().getDestinations();

                        if (destinations != null) {
                            Point cardViewHolderSize = new Point(mWindowSize.x - Utils.dpToPx(mCardPadding), mWindowSize.y - Utils.dpToPx(mCardPadding));

                            final int size = destinations.size();
                            for (int i = 0; i < size; i++) {
                                SingleDestination destination = destinations.get(i);
                                mSwipeView.addView(new DestinationCard(mPicasso, mContext, destination, cardViewHolderSize, MainActivity.this));
                            }

                            mNewLoader.setVisibility(View.GONE);
                        }
                    } else if (status == 0) {
//                      error handling
                        if (!mAlertDialog.isShowing()) {

                            List<SingleDestination> destinations = response.body().getDestinations();
                            int views = mSwipeView.getChildCount();
                            if (views == 0 && destinations.size() == 0) {
                                String errorMessage = response.body().getErrorMessage();
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                                builder.setCancelable(false);
                                builder.setTitle(R.string.sorry);
                                builder.setMessage(errorMessage);
                                builder.setPositiveButton("OK", null);
                                mAlertDialog = builder.create();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            mAlertDialog.show();
                                        }
                                    }
                                });

                            } else if (destinations.size() == 0) {
                                String errorMessage = response.body().getErrorMessage();
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                                builder.setCancelable(false);
                                builder.setTitle(R.string.sorry);
                                builder.setMessage("We couldn't find any more destinations. Try changing your budget or search filters.");
                                builder.setPositiveButton("Change Budget", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        goFilters();
                                    }
                                });

                                mAlertDialog = builder.create();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            mAlertDialog.show();
                                        }
                                    }
                                });
                            } else {

                                Point cardViewHolderSize = new Point(mWindowSize.x - Utils.dpToPx(mCardPadding), mWindowSize.y - Utils.dpToPx(mCardPadding));

                                final int size = destinations.size();
                                for (int i = 0; i < size; i++) {
                                    SingleDestination destination = destinations.get(i);
                                    mSwipeView.addView(new DestinationCard(mPicasso, mContext, destination, cardViewHolderSize, MainActivity.this));
                                }

                                mNewLoader.setVisibility(View.GONE);
                            }
                        }
                    } else if (status == 2) {
                        int views = mSwipeView.getChildCount();
                        if (views == 0) {
                            if (!mAlertDialog.isShowing()) {
                                String errorMessage = response.body().getErrorMessage();
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                                builder.setCancelable(false);
                                builder.setTitle(R.string.sorry);
                                builder.setMessage(errorMessage);
                                builder.setPositiveButton(R.string.changeBudgetText, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        goFilters();
                                    }
                                });
                                mAlertDialog = builder.create();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            mAlertDialog.show();
                                        }
                                    }
                                });
                            }

                        } else {
                            if (!mAlertDialog.isShowing()) {
                                String errorMessage = response.body().getErrorMessage();
                                AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                                builder.setCancelable(true);
                                builder.setTitle(R.string.sorry);
                                builder.setMessage(R.string.noDestinationsText);
                                builder.setPositiveButton(R.string.changeBudgetText, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        goFilters();
                                    }
                                });
                                builder.setNegativeButton(R.string.canceltext, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                mAlertDialog = builder.create();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {
                                            mAlertDialog.show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                } else {
                    if (!mAlertDialog.isShowing()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                        builder.setMessage(R.string.noDestinationErrorText);
                        builder.setPositiveButton(R.string.oktext, null);
                        mAlertDialog = builder.create();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isFinishing()) {
                                    mAlertDialog.show();
                                }
                            }
                        });
                    }
                }
                checkDestinationViewCount();
            }

            @Override
            public void onFailure(Call<DestinationsResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                checkDestinationViewCount();
                mLoadingIconSpinner.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.load_cities:
                com.github.clans.fab.FloatingActionButton moreCities = findViewById(R.id.load_cities);
                moreCities.setVisibility(View.INVISIBLE);
                fetchDestinations(initialDestinationsToLoad);
                break;
            case R.id.fabUndo:
                mSwipeView.undoLastSwipe();
                mFabUndo.setVisibility(View.GONE);
                break;
            case R.id.filtersButton:
                goFilters();
                break;
            case R.id.saveditemsButton:
                onSavedItemMenuSelected();
                break;
        }

    }

    private void checkDestinationViewCount() {

        int destinations = mSwipeView.getChildCount();

        Log.d(TAG, "Destinations: " + String.valueOf(destinations));

        if (destinations == 0) {    // we have at least 1 'undo'
            mInitialLoader.setVisibility(View.GONE);
            // show an error
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.sorry);
            String errorMessage;
            if (token != null) {
                errorMessage = getString(R.string.noDestinationsLoggedIn);
                builder.setMessage(errorMessage);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.filters, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goFilters();
                    }
                });
            } else {
                errorMessage = getString(R.string.noDestinationsLoggedOut);
                builder.setMessage(errorMessage);
                builder.setPositiveButton(R.string.registertext, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        goRegister();
                    }
                });
                builder.setNegativeButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onLoginMenuSelected();
                    }
                });
            }
            if (!mAlertDialog.isShowing()) {
                mAlertDialog = builder.create();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isFinishing()) {
                            mAlertDialog.show();
                        }
                    }
                });
            }
        } else {
            if (destinations == 1) {
                // load another destination now
                fetchDestinations(newDestinationsToLoad);
            }
        }
    }

    private void goHotel(SingleDestination destination) {

        if (mGoHotelsEnabled) {
            mGoHotelsEnabled = false;

            Intent intent = new Intent(mContext, HotelActivity.class);
            int cityId = destination.getImageId();
            String cityName = destination.getCityName();
            intent.putExtra("cityId", cityId);
            intent.putExtra("cityName", cityName);
            intent.putExtra("hotelId", 0);
            intent.putExtra("imageId", mLoaderImage);
            intent.putExtra("lat", destination.getLat());
            intent.putExtra("lon", destination.getLon());
            intent.putExtra("flightCost", destination.getFlightCost());

            Bundle bundle = new Bundle();
            bundle.putSerializable("destination", destination);
            intent.putExtras(bundle);

//            overridePendingTransition(0, 0);
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            startActivity(intent);
        }
    }

    private void goFilters() {

        mAlertDialog.dismiss();
        mNewLoader.setVisibility(View.GONE);

        Intent intent = new Intent(this, ProfileFilterActivity.class);
        intent.putExtra("tabtoshow", 1);
        startActivity(intent);
    }

    private void goRegister() {

        mAlertDialog.dismiss();
        mNewLoader.setVisibility(View.GONE);

        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utilities.hideUI(this);
        mGoHotelsEnabled = true;
        merlin.bind();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mLoadFader.setVisibility(View.GONE);
        mLoadingIconSpinner.setVisibility(View.GONE);

        if (mBackend.getSharedPreferences("firstrun") == "yes") {
            mBackend.setSharedPreferences("firstrun", "no");
        }

        token = mBackend.getUserToken();

        if (token != null && token != "") {
            setLoggedInState();
        }

        Integer recycle = getIntent().getIntExtra("recycle", 0);
        if (recycle == 1) {
            mSwipeView.removeAllViews();
            getIntent().putExtra("recycle", 0);
        }

        fetchDestinations(newDestinationsToLoad);
    }

    @Override
    protected void onPause() {
        merlin.unbind();
        super.onPause();
    }

    @Override
    public void onProfileMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, ProfileFilterActivity.class);
        intent.putExtra("tabtoshow", 0);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onSettingsMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, ProfileFilterActivity.class);
        intent.putExtra("tabtoshow", 1);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onTermsMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, TermsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onRegisterMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onLogoutMenuSelected() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BaseResponse> call = apiService.logout(token);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {

                mBackend.Logout();
                mDrawer.closeDrawers();
                setLoggedOutState();
            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onLoginMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onAdminMenuSelected() {
        mDrawer.closeDrawers();

    }

    @Override
    public void onSavedItemMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, SavedActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onHelpItemMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onFeedbackItemMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, FeedbackActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPrivacyItemMenuSelected() {
        mDrawer.closeDrawers();
        Intent intent = new Intent(this, PrivacyActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
