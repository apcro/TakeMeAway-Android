package io.takemeaway.takemeaway.adapters;

import android.content.Context;
import android.util.Log;
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

import androidx.viewpager.widget.PagerAdapter;
import io.takemeaway.takemeaway.R;
import io.takemeaway.takemeaway.application.TakeMeAway;
import io.takemeaway.takemeaway.datamodels.SingleHotel;
import io.takemeaway.takemeaway.datamodels.SingleRoom;
import io.takemeaway.takemeaway.datamodels.SingleRoomBlock;
import io.takemeaway.takemeaway.services.Utilities;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class RoomBlockPagerAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<SingleRoomBlock> mRooms;

    public RoomBlockPagerAdapter(ArrayList<SingleRoomBlock> rooms, Context context) {
        mContext = context;
        this.mRooms = rooms;
    }

    @Override
    public int getCount() {
        return mRooms.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        mLayoutInflater = LayoutInflater.from(container.getContext());
        View itemView = mLayoutInflater.inflate(R.layout.fragment_room, container, false);
        itemView.setTag("View"+position);

        final SingleRoomBlock roomBlock = mRooms.get(position);

        TextView roomname = itemView.findViewById(R.id.roomname);
        roomname.setText(roomBlock.getRoomName());
        TextView roomdescription = itemView.findViewById(R.id.roomdescription);
        roomdescription.setText(roomBlock.getRoomDescription());
        TextView sleepscount = itemView.findViewById(R.id.sleepscount);
        sleepscount.setText("Sleeps " + roomBlock.getMaxOccupancy().toString());

        ImageView roomImage = itemView.findViewById(R.id.roomImage);
        Log.d("RoomBlockAdaptor", roomBlock.getPhotos().get(0).getUrlOriginal());

        Picasso.with(container.getContext())
                .load(roomBlock.getPhotos().get(0).getUrlOriginal())
                .error(R.drawable.logo)
                .resize(roomImage.getWidth(), roomImage.getHeight())
                .onlyScaleDown()
                .centerCrop()
                .into(roomImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("RoomBlockAdaptor", "Success");
                    }

                    @Override
                    public void onError() {
                        Log.d("RoomBlockAdaptor", "Failure");
                    }});

        itemView.setTag(roomBlock.getRoomId());

        container.addView(itemView);

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