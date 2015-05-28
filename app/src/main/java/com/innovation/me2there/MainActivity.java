package com.innovation.me2there;


import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;

//import android.support.v7.app.ActionBar;

public class MainActivity extends Mee2ThereActivity implements
        ActionBar.TabListener,
        AbsListView.OnScrollListener,
        AbsListView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private ViewPager viewPager;
    private MainFragmentAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = {"Discover", "Manage"};

    private Double latitute;
    private Double longitutde;

    public static float densityMultiplier;


    private static final String TAG = "StaggeredGridActivity";
    public static final String SAVED_DATA_KEY = "SAVED_DATA";

    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    //private SampleAdapter mAdapter;
    private ArrayList<String> mData;


    public Double getLatitute() {
        return latitute;
    }

    public Double getLongitude() {
        return longitutde;
    }

    public void setLatitute(Double latParm) {
        latitute = latParm;
    }

    public void setLongitude(Double longParm) {
        longitutde = longParm;
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
        AssetManager mngr = getAssets();
        Typeface font = Typeface.createFromAsset(mngr, "fontello.ttf");


        densityMultiplier = getApplicationContext().getResources().getDisplayMetrics().density;
        Intent intent = getIntent();
        String message = intent.getStringExtra(StartActivity.EXTRA_MESSAGE);
        String facebookId =intent.getStringExtra(StartActivity.FB_ID);


        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new MainFragmentAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });



    }


//    private void fillNewCards(){
//        if(mee2ThereDataStore == null) {
//            mee2ThereDataStore = new DataStore(latitute, longitutde);
//        }
//        //Need to retrieve the activity list and image name from DB
//        List<EventDetailVO> events = DataStore.getActivities();
//
//        mAdapter = new SampleAdapter(this, R.id.txt_line1);
//
//        // do we have saved data?
//        /*if (savedInstanceState != null) {
//            mData = savedInstanceState.getStringArrayList(SAVED_DATA_KEY);
//        }*/
//
//        for(EventDetailVO anEvent:events){
//            mAdapter.add(anEvent);
//        }
//
//        mGridView.setAdapter(mAdapter);
//
//
//    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                return true;
//            case R.id.action_login:
//                intent = new Intent(this,FirstActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.action_logout:
//                loggedInUser = null;
//                invalidateOptionsMenu();
//                return true;
            case R.id.action_profile:
                intent = new Intent(this, EditProfile.class);
                startActivity(intent);
                return true;
//            case R.id.action_settings:
//                intent = new Intent(this,Settings.class);
//                startActivity(intent);
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem loginItem = menu.findItem(R.id.action_login);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        MenuItem profileItem = menu.findItem(R.id.action_profile);
/*
        if(loggedInUser != null && ! loggedInUser.isEmpty()){
            Log.i("Main Activity","Login item"+loginItem.toString()+"onPrepareOptionsMenu : "+loggedInUser);
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            profileItem.setVisible(true);
        }else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            profileItem.setVisible(false);
        }
*/
        return true;

    }


    @Override
    public void onScrollStateChanged(final AbsListView view, final int scrollState) {
        Log.d(TAG, "onScrollStateChanged:" + scrollState);
    }

    @Override
    public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
        Log.d(TAG, "onScroll firstVisibleItem:" + firstVisibleItem +
                " visibleItemCount:" + visibleItemCount +
                " totalItemCount:" + totalItemCount);
        // our handling
        if (!mHasRequestedMore) {
            int lastInScreen = firstVisibleItem + visibleItemCount;
            if (lastInScreen >= totalItemCount) {
                Log.d(TAG, "onScroll lastInScreen - so load more");
                mHasRequestedMore = true;
                onLoadMoreItems();
            }
        }
    }

    private void onLoadMoreItems() {
        //final ArrayList<String> sampleData = SampleData.generateSampleData();
        //for (String data : sampleData) {
        //    mAdapter.add(data);
        //}
        // stash all the data in our backing store
//        mData.addAll(sampleData);
        // notify the adapter that we can update now
        //      mAdapter.notifyDataSetChanged();
        //    mHasRequestedMore = false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, "Item Clicked: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Item Long Clicked: " + position, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

}
