package com.innovation.me2there.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.innovation.me2there.R;

import com.innovation.me2there.fragments.DiscoverEventsFragment;
import com.innovation.me2there.fragments.GoingEventsFragment;
import com.innovation.me2there.fragments.MyEventsFragment;

/**
 * Created by ashley on 5/11/15.
 */


public class MainFragmentAdapter extends FragmentPagerAdapter {
    private Context _context;

    DiscoverEventsFragment mainFragment;
    MyEventsFragment myEventsFragment;
    GoingEventsFragment goingEventsFragment;
    int icons[] = {
            R.drawable.ic_action_trending,
            R.drawable.ic_action_feed,
            R.drawable.ic_myevents,
            R.drawable.ic_action_search
    };

    public DiscoverEventsFragment getMainFragment() {
        return mainFragment;
    }

    public MyEventsFragment getMyEventsFragment() {
        return myEventsFragment;
    }

    public GoingEventsFragment getGoingEventsFragment() {
        return goingEventsFragment;
    }


    public MainFragmentAdapter(FragmentManager fm,Context c) {
        super(fm);
        mainFragment = new DiscoverEventsFragment();
        myEventsFragment = new MyEventsFragment();
        goingEventsFragment = new GoingEventsFragment();
        _context = c;

    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // fragment activity
                return mainFragment;
            case 1:
                // fragment activity
                return goingEventsFragment;
            case 2:
                // fragment activity
                return myEventsFragment;

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

    public Drawable getIcon(int position) {

        return _context.getResources().getDrawable(icons[position]);
    }

}