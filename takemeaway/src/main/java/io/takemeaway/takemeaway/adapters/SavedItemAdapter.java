package io.takemeaway.takemeaway.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.activities.HotelActivity;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SavedItem;
import io.takemeaway.takemeaway.datamodels.SingleDestination;
import io.takemeaway.takemeaway.services.Backend;
import io.takemeaway.takemeaway.services.Utilities;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class SavedItemAdapter extends PagerAdapter {

    private Context mContext;
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;

    private Backend mBackend;

    private ArrayList<SavedItem> mItems;

    public SavedItemAdapter(ArrayList<SavedItem> items, Context context, Activity activity) {
        mContext = context;
        mActivity = activity;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBackend = TakeMeAway.getBackend();
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.saveditem_fragment, container, false);
        itemView.setTag("View"+position);

        SavedItem item = mItems.get(position);


        LinearLayout cityHolder = itemView.findViewById(R.id.cityHolder);
        cityHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save it in the current session
                SingleDestination destination = new SingleDestination();

                destination.setCityId(item.getCityId());
                destination.setCityName(item.getCityname());
                destination.setCityImage(item.getCityimage());
                destination.setLat(item.getAirportLat());
                destination.setLon(item.getAirportLon());
                destination.setCountryName(item.getCountry());
                destination.setFlightCost(String.valueOf(item.getFlightprice()));


                mBackend.setSelectedDestination(destination);
                mBackend.setCurrentFlightPrice(Integer.parseInt(destination.getFlightCost()));

                goHotel(destination);
            }
        });

        ImageView imageView = (ImageView) itemView.findViewById(R.id.destinationImage);
        Picasso.with(mContext).load(item.getCityimage()).fit().centerCrop().into(imageView);
        TextView textView = (TextView) itemView.findViewById(R.id.destinationName);
        textView.setText(item.getCityname());
        RelativeLayout flightPriceLayout = itemView.findViewById(R.id.flightPrice);
        TextView flightPrice = itemView.findViewById(R.id.flightCost);

        if (item.getFlightprice() != 0) {
            flightPrice.setText(Utilities.getCurrencySymbol(TakeMeAway.getBackend().getCurrentUser().getCurrencyCode()) + String.valueOf(item.getFlightprice()) + " per person");
        } else {
            flightPriceLayout.setVisibility(View.GONE);
        }

        RelativeLayout savedOnLayout = itemView.findViewById(R.id.savedDate);
        TextView savedOn = itemView.findViewById(R.id.savedOnDate);
        if (item.getSavedon() != 0) {
            savedOn.setText("Saved on " + Utilities.GetDateString(item.getSavedon()));
        } else {
            savedOnLayout.setVisibility(View.GONE);
        }

        LinearLayout destHolder = itemView.findViewById(R.id.destHolder);
        if (item.getHotelid() == 0) {
            destHolder.setVisibility(View.GONE);
        } else {
            RelativeLayout hotelPriceLayout = itemView.findViewById(R.id.hotelPrice);
            TextView hotelPrice = itemView.findViewById(R.id.hotelCost);

            if (item.getHotelprice() != 0) {
                hotelPrice.setText(Utilities.getCurrencySymbol(TakeMeAway.getBackend().getCurrentUser().getCurrencyCode()) + String.valueOf(item.getHotelprice()) + " per night");
            } else {
                hotelPriceLayout.setVisibility(View.GONE);
            }

            ImageView hotelImageView = (ImageView) itemView.findViewById(R.id.hotelImage);
            Picasso.with(mContext).load(item.getHotelimage()).fit().centerCrop().into(hotelImageView);
            TextView hotelTextView = (TextView) itemView.findViewById(R.id.hotelName);
            hotelTextView.setText(item.getHotelname());

            TextView reviewScore = itemView.findViewById(R.id.reviewScore);
            reviewScore.setText(String.valueOf(item.getRating()));
        }



        container.addView(itemView);

        return itemView;
    }

    private void goHotel(SingleDestination destination) {

            Intent intent = new Intent(mContext, HotelActivity.class);
            intent.putExtra("cityId", destination.getImageId());
            intent.putExtra("cityName", destination.getCityName());
            intent.putExtra("lat", destination.getLat());
            intent.putExtra("lon", destination.getLon());

            Bundle bundle = new Bundle();
            bundle.putSerializable("destination", destination);
            intent.putExtras(bundle);


            mActivity.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
            mActivity.startActivity(intent);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    public void setItems(ArrayList<SavedItem> mItems) {
        this.mItems = mItems;
    }
}