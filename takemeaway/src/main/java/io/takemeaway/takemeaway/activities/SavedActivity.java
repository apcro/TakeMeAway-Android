package io.takemeaway.takemeaway.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionMenu;
import com.hrskrs.instadotlib.InstaDotView;

import java.util.ArrayList;
import java.util.List;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.adapters.SavedItemAdapter;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SavedItem;
import io.takemeaway.takemeaway.responsemodels.BaseResponse;
import io.takemeaway.takemeaway.responsemodels.SavedItemsResponse;
import io.takemeaway.takemeaway.services.ApiClient;
import io.takemeaway.takemeaway.services.ApiInterface;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.takemeaway.takemeaway.R.id.toolbar;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SavedActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SavedActivity.class.getSimpleName();

    private String token;

    private Backend mBackend;
    private Context mContext;
    private Activity mActivity;

    private SavedItemAdapter mSavedItemAdapter;
    private ViewPager mSavedItemPager;
//    private TabLayout mTabLayout;
    public ArrayList<SavedItem> mItems;
    private com.github.clans.fab.FloatingActionButton mBookTravel;

    private TextView noitemslabel;
    private ProgressDialog progressDialog;

    private RelativeLayout mLoadfader;
    private ImageView mShareIcon;

    private FloatingActionMenu mHotelFab;

    private InstaDotView mPageIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.hideUI(this);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        setContentView(R.layout.activity_saveditems);

        int loaderImage = Utilities.getRandomLoaderImage();
        ImageView loaderView = (ImageView) findViewById(R.id.loaderImage);
        loaderView.setImageResource(loaderImage);
        mLoadfader = (RelativeLayout) findViewById(R.id.loadFader);

        mBackend = TakeMeAway.getBackend();
        mContext = this;
        mActivity = (Activity) this;

        token = mBackend.getUserToken();

        if (token == null) {
            goHome();
            finish();
        }

        mShareIcon = findViewById(R.id.shareIcon);
        mShareIcon.setVisibility(View.VISIBLE);
        Drawable toolbarShareIcon = getDrawable(R.drawable.ic_share_white_24dp);
        if (toolbarShareIcon != null) {
            toolbarShareIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mShareIcon.setImageDrawable(toolbarShareIcon);
        }
        mShareIcon.setOnClickListener(this);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbarText);
        toolbarTitle.setText("");

        Toolbar mToolbar = (Toolbar) findViewById(toolbar);
        setSupportActionBar(mToolbar);

        final Drawable upArrow = mContext.getDrawable(R.drawable.ic2_left);
        if (upArrow != null) {
            upArrow.setColorFilter(mContext.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
        }

        noitemslabel = (TextView) findViewById(R.id.noitemslabel);

        noitemslabel.setVisibility(View.INVISIBLE);

        mHotelFab = (FloatingActionMenu) findViewById(R.id.hotel_fab_menu);
        mHotelFab.setClosedOnTouchOutside(true);

        mBookTravel = findViewById(R.id.bookTravel);
        mBookTravel.setOnClickListener(this);

        Drawable bookIcon = getDrawable(R.drawable.ic2_takeoff);
        if (bookIcon != null) {
            bookIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            mBookTravel.setImageDrawable(bookIcon);
        }

        mItems = new ArrayList<>();
        mSavedItemAdapter = new SavedItemAdapter(mItems, getApplicationContext(), mActivity);

        mSavedItemPager = (ViewPager) findViewById(R.id.saveditemsPager);
        mSavedItemPager.setAdapter(mSavedItemAdapter);

        mPageIndicatorView = findViewById(R.id.instaDotView);
        mPageIndicatorView.setVisibleDotCounts(6);

        mSavedItemPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mHotelFab.close(true);
            }

            @Override
            public void onPageSelected(int position) {
                mPageIndicatorView.onPageChange(position);
                if (mItems.get(position).getHotelid() == 0) {
                    mBookTravel.setVisibility(View.INVISIBLE);
                } else {
                    mBookTravel.setVisibility(View.VISIBLE);
                    Drawable bookIcon = getDrawable(R.drawable.ic2_takeoff);
                    if (bookIcon != null) {
                        bookIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                        mBookTravel.setImageDrawable(bookIcon);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        progressDialog = new ProgressDialog(SavedActivity.this, R.style.AppTheme_Dark_Dialog);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.loadsavedtripsnotice));
        progressDialog.show();


        com.github.clans.fab.FloatingActionButton moreInfo = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.more_information);
        moreInfo.setOnClickListener(this);
        Drawable infoIcon = getDrawable(R.drawable.ic2_hotel);
        if (infoIcon != null) {
            infoIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            moreInfo.setImageDrawable(infoIcon);
        }

        com.github.clans.fab.FloatingActionButton deleteItem = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.delete_item);
        deleteItem.setOnClickListener(this);
        Drawable deleteIcon = getDrawable(R.drawable.ic2_delete);
        if (deleteIcon != null) {
            deleteIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            deleteItem.setImageDrawable(deleteIcon);
        }
        getSavedItems();
    }

    @Override
    public void onClick(View v) {
        int item = mSavedItemPager.getCurrentItem();
        if (mItems.size() > 0) {
            switch (v.getId()) {
                case R.id.more_information:
                    goSavedDetail(mItems.get(item));
                    break;
                case R.id.bookTravel:
                    goBook(mItems.get(item));
                    break;
                case R.id.delete_item:
                    removeItem(mItems.get(item));
                    break;
                case R.id.shareIcon:
                    shareSavedItem(mItems.get(item));
                    break;
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBackend = TakeMeAway.getBackend();
        getSavedItems();
        token = mBackend.getUserToken();
        Utilities.hideUI(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getSavedItems() {

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<SavedItemsResponse> call = apiService.loadSavedItems(token);
        call.enqueue(new Callback<SavedItemsResponse>() {
            @Override
            public void onResponse(Call<SavedItemsResponse>call, Response<SavedItemsResponse> response) {
                if (response.body() != null) {
                    List<SavedItem> items = response.body().getSaveditems();
                    Log.d(TAG, "Number of records received: " + items.size());

                    final int size = items.size();

                    if (size == 0) {
                        noitemslabel.setVisibility(View.VISIBLE);
                        mSavedItemPager.setVisibility(View.GONE);
                        mHotelFab.setVisibility(View.GONE);
                        mBookTravel.setVisibility(View.GONE);
                    } else {
                        mItems = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            SavedItem destination = items.get(i);
                            //do something with i
                            mItems.add(destination);
                        }
                        mSavedItemAdapter.setItems(mItems);
                        mSavedItemAdapter.notifyDataSetChanged();
                        mPageIndicatorView.setNoOfPages(mItems.size());
                        if (mItems.get(0).getHotelid() == 0) {
                            mBookTravel.setVisibility(View.INVISIBLE);
                        } else {
                            mBookTravel.setVisibility(View.VISIBLE);
                            Drawable bookIcon = getDrawable(R.drawable.ic2_takeoff);
                            if (bookIcon != null) {
                                bookIcon.setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                                mBookTravel.setImageDrawable(bookIcon);
                            }
                        }
                    }
                } else {
                    // @TODO handle the error where there's no data coming back
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle(R.string.sorry);
                    builder.setMessage(R.string.somethingwentwrongerror);
                    builder.setPositiveButton(R.string.oktext, null);
                    builder.show();
                }
                mLoadfader.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }

            @Override
            public void onFailure(Call<SavedItemsResponse>call, Throwable t) {
                Log.e(TAG, t.toString());
                progressDialog.dismiss();
            }
        });

    }

    private void removeItem(SavedItem item) {
        mHotelFab.close(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.removesaveditemmessage));
        progressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<BaseResponse> call = apiService.deleteSavedItem(token, item.getCityId(), item.getHotelid());
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse>call, Response<BaseResponse> response) {
                progressDialog.dismiss();
                int item = mSavedItemPager.getCurrentItem();
                mItems.remove(item);

                if (mItems.size() != 0) {
                    mSavedItemAdapter.setItems(mItems);
                    mSavedItemAdapter.notifyDataSetChanged();
                    mSavedItemPager.setCurrentItem(0);
                } else {
                    mSavedItemPager.setVisibility(View.GONE);
                    mHotelFab.setVisibility(View.GONE);
                    mBookTravel.setVisibility(View.GONE);

                    noitemslabel.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onFailure(Call<BaseResponse>call, Throwable t) {
                // Log error here since request failed
                Log.e(TAG, t.toString());
                progressDialog.dismiss();

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setTitle(R.string.sorry);
                builder.setMessage("Something went wrong and we couldn't delete that item");
                builder.setNegativeButton(R.string.oktext, null);
                builder.show();
            }
        });
    }

    private void goHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void goBook(final SavedItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.savedbookingwarningheading);
        builder.setMessage("This hotel may not be available anymore");
        builder.setNegativeButton(R.string.canceltext, null);
        builder.setPositiveButton(R.string.oktext, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reallyGoBook(item);
            }
        });
        builder.show();
    }

    private void reallyGoBook(SavedItem item) {
        Intent intent = new Intent(this, BookingActivity.class);
        intent.putExtra("cityId", item.getCityId());
        intent.putExtra("hotelId", item.getHotelid());
        intent.putExtra("hotelPrice", -1);
        intent.putExtra("flightCost", "none");
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }


    private void goSavedDetail(SavedItem item) {
        mHotelFab.close(true);
    }


    private void shareSavedItem(SavedItem item) {
        String shareHash = Utilities.md5(String.valueOf(String.valueOf(item.getCityId()) + ":" + item.getHotelid()));

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Check this out on TakeMeAway! We could be there next weekend! https://takemeaway.io/tma/" + shareHash;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "TakeMeAway");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

}
