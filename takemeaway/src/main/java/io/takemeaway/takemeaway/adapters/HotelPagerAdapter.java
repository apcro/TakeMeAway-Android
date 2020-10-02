package io.takemeaway.takemeaway.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Currency;

import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.activities.HotelActivity;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.datamodels.SingleRoom;
import io.takemeaway.takemeaway.services.Utilities;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class HotelPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<SingleHotel> mHotels;

    public HotelPagerAdapter(ArrayList<SingleHotel> hotels, Context context) {
        mContext = context;
        this.mHotels = hotels;
    }

    @Override
    public int getCount() {
        return mHotels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mLayoutInflater = LayoutInflater.from(container.getContext());
        View itemView = mLayoutInflater.inflate(R.layout.hotel_fragment, container, false);
        itemView.setTag("View"+position);

        TextView noRooms = (TextView) itemView.findViewById(R.id.noRoomsLeft);
        TextView roomsAvail = (TextView) itemView.findViewById(R.id.roomsAvailable);
        TextView mReviewScore = itemView.findViewById(R.id.reviewScore);

        noRooms.setVisibility(View.GONE);
        roomsAvail.setVisibility(View.GONE);

        final SingleHotel hotel = mHotels.get(position);

        itemView.setTag(hotel.getHotelId());

        ImageView imageView = (ImageView) itemView.findViewById(R.id.hotelImage);
        Picasso.with(mContext).load(hotel.getHotelPhoto()).into(imageView);

        TextView textView = (TextView) itemView.findViewById(R.id.hotelName);
        textView.setText(hotel.getName());

        TextView ratingView = (TextView) itemView.findViewById(R.id.hotelRating);
        mReviewScore.setText(hotel.getRating().toString());

        TextView perNight = (TextView) itemView.findViewById(R.id.hotelMaxprice);

        container.addView(itemView);

        if (hotel.getRooms() != null) {

            ProgressBar avail = (ProgressBar) itemView.findViewById(R.id.availabilityChecker);
            avail.setVisibility(View.GONE);

            int rooms = hotel.getRooms().get(0).getRoomsAtThisprice();

            String roomsText;
            if (rooms == 1) {
                roomsText = "Only 1 room left!";
            } else {
                roomsText = String.valueOf(rooms) + " rooms available";
            }

            roomsAvail.setText(roomsText);
            roomsAvail.setVisibility(View.VISIBLE);

            String token = TakeMeAway.getBackend().getUserToken();
            String textPerNight;
            int people = TakeMeAway.getBackend().getCurrentUser().getPeople();
            if (token == null) {
                textPerNight = String.format(
                        mContext.getString(R.string.hotelCostBookingScreen),
                        Utilities.getCurrencySymbol(hotel.getCurrencycode()),
                        String.valueOf(Math.round(hotel.getRooms().get(0).getPrice())
                        ));
            } else {

                ArrayList<SingleRoom> thishotelrooms = hotel.getRooms();
                float lowprice = 999;

                if (thishotelrooms.size() == 1) {
                    lowprice = hotel.getRooms().get(0).getPrice();
                } else {
                    for (SingleRoom aroom: thishotelrooms) {
                        if (aroom.getPrice() < lowprice && aroom.getAdults() == people) {
                            lowprice = aroom.getPrice();
                        }
                    }
                }
                if (lowprice == 999) {
                    lowprice = hotel.getRooms().get(0).getPrice();
                }

                textPerNight = String.format(
                    mContext.getString(R.string.hotelCostBookingScreen),
                    Utilities.getCurrencySymbol(TakeMeAway.getBackend().getCurrentUser().getCurrencyCode()),
                    String.valueOf(Math.round(lowprice)
                ));
            }
            perNight.setText(textPerNight);

        }

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    private String getCurrencySymbol(String currencyCode) {
        try {
            Currency currency = Currency.getInstance(currencyCode);
            return currency.getSymbol();
        } catch (Exception e) {
            return currencyCode;
        }
    }

}