package io.takemeaway.takemeaway.adapters;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.takemeaway.takemeaway.R;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class CustomHotelDetailsAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<String> mHotelImages;

    public CustomHotelDetailsAdapter(ArrayList<String> hotels, Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mHotelImages = hotels;
    }

    @Override
    public int getCount() {
        if (mHotelImages != null) {
            return mHotelImages.size();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.hotel_detail_fragment, container, false);
        itemView.setTag("View"+position);

        String hotelImage = mHotelImages.get(position);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.hotelImage);
        Picasso.with(mContext).load(hotelImage).into(imageView);

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

    public void setmHotelImages(ArrayList<String> mHotelImages) {
        this.mHotelImages = mHotelImages;
    }
}