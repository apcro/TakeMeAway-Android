package io.takemeaway.takemeaway.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import io.takemeaway.takemeaway.fragments.FragmentFilters;
import io.takemeaway.takemeaway.fragments.FragmentProfile;

/*
 * TakeMeAway
 *
 * (C) 2017-present Tom Gordon
 */
public class ProfileFilterAdaptor extends FragmentPagerAdapter {

    private Fragment mFilterFragment;
    private Fragment mProfileFragment;

    public ProfileFilterAdaptor(FragmentManager fm) {
        super(fm);
        mFilterFragment = new FragmentFilters();
        mProfileFragment = new FragmentProfile();
    }

    @Override
    public Fragment getItem(int position) {
        // For a given position, return two different potential fragments based on a condition
        switch (position) {
            case 0:
                return mProfileFragment;
            case 1:
                return mFilterFragment;
            default:
                return null;
        }
    }

    // Force a refresh of the page when a different fragment is displayed
    @Override
    public int getItemPosition(Object object) {
        // this method will be called for every fragment in the ViewPager
        if (object instanceof FragmentFilters) {
            return POSITION_UNCHANGED; // don't force a reload
        } else if (object instanceof FragmentProfile) {
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
                return "Profile";
            case 1:
                return "Filters";
        }
        return "";
    }

    public Fragment getFilterFragment() {
        return mFilterFragment;
    }

    public Fragment getProfileFragment() {
        return this.mProfileFragment;
    }
}