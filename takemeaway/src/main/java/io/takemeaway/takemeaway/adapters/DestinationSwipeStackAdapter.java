package io.takemeaway.takemeaway.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.services.Utilities;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class DestinationSwipeStackAdapter extends BaseAdapter {
    private static final String TAG = DestinationSwipeStackAdapter.class.getSimpleName();

    private List<SingleDestination> mData;
    private Context mContext;
    private Picasso.Builder mPicasso;

    public DestinationSwipeStackAdapter(List<SingleDestination> data) {
        this.mData = data;
    }

    public DestinationSwipeStackAdapter(List<SingleDestination> data, Context context) {
        this.mData = data;
        this.mContext = context;
        this.mPicasso = new Picasso.Builder(context);
        mPicasso.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public SingleDestination getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        convertView = LayoutInflater.from(mContext).inflate(R.layout.destination_card, null);
        TextView cityName = convertView.findViewById(R.id.cityName);
        TextView countryName = convertView.findViewById(R.id.countryName);

        final TextView cityDescription = convertView.findViewById(R.id.cityDescription);
        final ImageView cityImage = convertView.findViewById(R.id.cityImage);
        final ProgressBar progressView = convertView.findViewById(R.id.destinationCardProgressView);
        final TextView flightCost = convertView.findViewById(R.id.flightCost);

        SingleDestination dest = mData.get(position);

        String countryNameValue = dest.getCountryName();

        cityName.setText(dest.getCityName());
        countryName.setText(countryNameValue);
        cityDescription.setText(dest.getCityDescription());

        String mFlightCost = dest.getFlightCost();

        if (mFlightCost == null) {
            mFlightCost = "9999";
        }
        switch (mFlightCost) {
            case "9999":
                RelativeLayout flightPrice = convertView.findViewById(R.id.flightPrice);
                flightPrice.setVisibility(View.GONE);
                break;
            case "8888":
                flightCost.setText("");
                break;
            default:
                flightCost.setText(Utilities.getCurrencySymbol("EUR") + mFlightCost);
                break;
        }

        parent.invalidate();
        parent.refreshDrawableState();

        String cityImageUri = TakeMeAway.DestinationImageUri() + dest.getCityImage();

        progressView.setVisibility(View.VISIBLE);

        mPicasso.build()
                .load(cityImageUri)
                .tag(dest.getCityName())
                .error(R.drawable.logo)
                .fit()
                .centerCrop()
                .into(cityImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }});

        convertView.refreshDrawableState();

        return convertView;
    }



    public void cancelImageLoad(String tag) {
        mPicasso.build().cancelTag(tag);
    }
}