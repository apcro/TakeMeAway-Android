package io.takemeaway.takemeaway.adapters;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import io.takemeaway.takemeaway.fragments.FragmentFilters;
import io.takemeaway.takemeaway.fragments.FragmentFlightBooking;
import io.takemeaway.takemeaway.fragments.FragmentHotelBooking;
import io.takemeaway.takemeaway.fragments.FragmentProfile;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */

public class BookingPagesAdapter extends FragmentStatePagerAdapter {

    private Fragment mFlightBookingFragment;
    private Fragment mHotelBookingFragment;

    public BookingPagesAdapter(FragmentManager fm, Bundle bundle) {
        super(fm);
        mFlightBookingFragment = new FragmentFlightBooking();
        mHotelBookingFragment = new FragmentHotelBooking();
        mFlightBookingFragment.setArguments(bundle);
        mHotelBookingFragment.setArguments(bundle);
    }

    @Override
    public Fragment getItem(int position) {
        // For a given position, return two different potential fragments based on a condition
        switch (position) {
            case 0:
                return mFlightBookingFragment;
            case 1:
                return mHotelBookingFragment;
            default:
                return null;
        }
    }

    // Force a refresh of the page when a different fragment is displayed
    @Override
    public int getItemPosition(Object object) {
        // this method will be called for every fragment in the ViewPager
        if (object instanceof FragmentFlightBooking) {
            return POSITION_UNCHANGED; // don't force a reload
        } else if (object instanceof FragmentHotelBooking) {
            return POSITION_UNCHANGED; // don't force a reload
        } else {
            // POSITION_NONE means something like: this fragment is no longer valid
            // triggering the ViewPager to re-build the instance of this fragment.
            return POSITION_NONE;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Flight";
            case 1:
                return "Hotel";
        }
        return "";
    }

}