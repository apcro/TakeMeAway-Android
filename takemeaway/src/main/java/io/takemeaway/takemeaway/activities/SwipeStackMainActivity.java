package io.takemeaway.takemeaway.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mindorks.placeholderview.PlaceHolderView;
import com.mindorks.placeholderview.annotations.Resolve;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Disconnectable;
import com.novoda.merlin.Merlin;

import java.util.ArrayList;
import java.util.List;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.DestinationSwipeStackAdapter;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.datamodels.UserDetails;
import io.takemeaway.takemeaway.libraries.SwipeStack;
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
public class SwipeStackMainActivity
        extends AppCompatActivity
        implements
        SwipeStack.SwipeStackListener,
        View.OnClickListener, DrawerMenuItem.DrawerCallBack
{

    private static final String TAG = SwipeStackMainActivity.class.getSimpleName();

    private Context mContext;

    private ArrayList<SingleDestination> mData;

    private DestinationSwipeStackAdapter mAdapter;

    boolean _isFirstRun = false;

    private static Integer initialDestinationsToLoad = 3;
    private static Integer newDestinationsToLoad = 1;

    private PlaceHolderView mDrawerView;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;
    private ProgressBar mInitialLoader;

    private Backend mBackend;

    private SwipeStack mSwipeStack;

    private String token;

    private int mSwipedPosition;
    private int mLoaderImage;
    private ImageView mLoaderView;
    private RelativeLayout mLoadFader;

    protected Merlin merlin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.activity_main);

        mBackend = TakeMeAway.getBackend();

        mContext = this;

        mSwipeStack = (SwipeStack) findViewById(R.id.swipeStack);

        mLoaderView = (ImageView) findViewById(R.id.loaderView);
        mLoadFader = (RelativeLayout) findViewById(R.id.loadFader);
        mLoadFader.setVisibility(View.GONE);

        mData = new ArrayList<>();
        mAdapter = new DestinationSwipeStackAdapter(mData, getApplicationContext());

        mSwipeStack.setAdapter(mAdapter);
        mSwipeStack.setListener(this);

        com.github.clans.fab.FloatingActionButton mFab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fabAdd);
        mFab.setOnClickListener(this);

        token = mBackend.getUserToken();

        mInitialLoader = (ProgressBar) findViewById(R.id.initialLoader);

        fetchDestinations(initialDestinationsToLoad);

        mDrawer = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerView = (PlaceHolderView) findViewById(R.id.drawerView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText(R.string.mainheadertext);
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
            _isFirstRun = true;
            mBackend.setSharedPreferences("firstrun", "no");
        }

        if (_isFirstRun) {
            SetupTutorial();
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Lato-Semibold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void SetupTutorial() {
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

        ImageView toolbarProfilePic = (ImageView) findViewById(R.id.toolbarProfilePic);
        toolbarProfilePic.setVisibility(View.GONE);

        mDrawerView.refresh();
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

            String admin = currentUser.getMail();
            if (admin.contentEquals("tom@takemeaway.io")) {
                DrawerMenuItem mAdminItem = new DrawerMenuItem(this.getApplicationContext(), DrawerMenuItem.DRAWER_MENU_ITEM_ADMIN);
                mAdminItem.setDrawerCallBack(this);
                mDrawerView.addView((mAdminItem));
            }

            ImageView toolbarProfilePic = (ImageView) findViewById(R.id.toolbarProfilePic);
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

            mDrawerView.refresh();
        }
    }

    private void fetchDestinations(Integer count) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<DestinationsResponse> call = apiService.findDestination(token, count);
        call.enqueue(new Callback<DestinationsResponse>() {
            @Override
            public void onResponse(Call<DestinationsResponse>call, Response<DestinationsResponse> response) {

                if (response.body() != null) {
                    List<SingleDestination> destinations = response.body().getDestinations();
                    Log.d(TAG, "Number of records received: " + destinations.size());

                    final int size = destinations.size();
                    for (int i = 0; i < size; i++)
                    {
                        SingleDestination destination = destinations.get(i);
                        //do something with i
                        mData.add(destination);
                    }
                    mAdapter.notifyDataSetChanged();

                    mSwipeStack.allowLayout();

                    mInitialLoader.setVisibility(View.GONE);

                    Log.d("SwipeStack", "NotifyDatasetChanged");
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                    builder.setMessage("There was an error on the server and no destinations were loaded.");
                    builder.setPositiveButton("OK", null);
                    builder.show();
                }



            }

            @Override
            public void onFailure(Call<DestinationsResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.load_cities:
                com.github.clans.fab.FloatingActionButton moreCities = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.load_cities);
                moreCities.setVisibility(View.INVISIBLE);
                fetchDestinations(initialDestinationsToLoad);
                break;
        }
    }

    @Override
    public void onViewSwipedToRight(int position) {
        Log.d(TAG, "Swiped right");

        mSwipedPosition = position;

        mLoadFader.setVisibility(View.VISIBLE);
        mLoaderImage = Utilities.getRandomLoaderImage();
        mLoaderView.setImageResource(mLoaderImage);
        mLoadFader.setAlpha(0);
        mLoadFader.animate().setDuration(200).alpha(1).withEndAction(new Runnable() {
            @Override
            public void run() {
                goHotel();
            }
        }).start();

        if (position >= (mAdapter.getCount() - 3)) {
            fetchDestinations(newDestinationsToLoad);
        }

    }

    private void goHotel() {
        SingleDestination swipedElement = mAdapter.getItem(mSwipedPosition);

        // at this point we'll load a new intent and show some hotels
        Intent intent = new Intent(mContext, HotelActivity.class);
        int cityId = swipedElement.getImageId();
        String cityName = swipedElement.getCityName();
        intent.putExtra("cityId", cityId);
        intent.putExtra("cityName", cityName);
        intent.putExtra("hotelId", 0);
        intent.putExtra("imageId", mLoaderImage);

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    @Override
    public void onViewSwipedToLeft(int position) {
        mAdapter.cancelImageLoad(mAdapter.getItem(position).getCityName());
        if (position >= (mAdapter.getCount() - 3)) {
            fetchDestinations(newDestinationsToLoad);
        }

        if (position >= 50) {
            // let's reset the entire stack
            // and tell the user
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setTitle("Hi there");
            builder.setMessage("We've shown you more than 50 destinations so far! We'll shake the randomiser a bit more.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSwipeStack.resetStack();
                    mData = new ArrayList<>();
                    fetchDestinations(initialDestinationsToLoad);
                    mAdapter.notifyDataSetChanged();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onViewSwipedUp(int position) {
        if (position >= (mAdapter.getCount() - 3)) {
            fetchDestinations(newDestinationsToLoad);
        }

    }

    @Override
    public void onViewSwipedDown(int position) {
        if (position >= (mAdapter.getCount() - 3)) {
            fetchDestinations(newDestinationsToLoad);
        }
    }

    @Override
    public void onStackEmpty() {
        Log.d(TAG, "SwipeStack empty!");

        mInitialLoader.setVisibility(View.VISIBLE);

        com.github.clans.fab.FloatingActionButton moreCities = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.load_cities);

        Drawable saveIcon = getDrawable(R.drawable.ic2_takeoff);
        if (saveIcon != null) {
            saveIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            moreCities.setImageDrawable(saveIcon);
        }
        moreCities.setVisibility(View.VISIBLE);
        fetchDestinations(newDestinationsToLoad);
    }

    @Override
    protected void onResume() {
        super.onResume();
        merlin.bind();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        mLoadFader.setVisibility(View.GONE);

        if (mBackend.getSharedPreferences("firstrun") == "yes") {
            _isFirstRun = true;
            mBackend.setSharedPreferences("firstrun", "no");
        }

        _isFirstRun = true;

        token = mBackend.getUserToken();

        if (token != null && token !="") {
            setLoggedInState();
        }
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
