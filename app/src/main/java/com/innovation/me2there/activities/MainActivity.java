package com.innovation.me2there.activities;


import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.maps.model.LatLng;
import com.innovation.me2there.adapters.MainFragmentAdapter;
import com.innovation.me2there.R;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;


import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.EventDetailVO;
import com.innovation.me2there.util.GoogleMapTask;

import java.util.Set;

import com.innovation.me2there.fragments.FragmentDrawer;
import com.innovation.me2there.extras.SortListener;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends Mee2ThereActivity implements
        AbsListView.OnScrollListener,
        AbsListView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        View.OnClickListener{
    //tag associated with the FAB menu button that sorts by name
    private static final String TAG_SORT_NAME = "sortName";
    //tag associated with the FAB menu button that sorts by date
    private static final String TAG_SORT_DATE = "sortDate";
    //tag associated with the FAB menu button that sorts by ratings
    private static final String TAG_SORT_RATINGS = "sortRatings";

    private ViewPager viewPager;
    private ActionBar actionBar;
    private GoogleMapTask mapTask;
    //private MaterialTabHost mTabHost;
    private Toolbar mToolbar;
    protected ViewGroup mContainerToolbar;
    private FragmentDrawer mDrawerFragment;


    public static float densityMultiplier;

    private static final String TAG = "StaggeredGridActivity";

    private boolean mHasRequestedMore;
    private boolean hasMoreToFetchDiscoverEvents = true;
    private boolean hasMoreToFetchMyEvents = true;
    private boolean hasMoreToFetchGoingEvents = true;

    private boolean isFetchExecuting = false;
    private boolean settingSelected = false;

    private FloatingActionButton mFAB;
    private FloatingActionMenu mFABMenu;
    protected MainFragmentAdapter mAdapter;
    private PagerSlidingTabStrip tabs;
    private Drawable oldBackground = null;
    private final Handler handler = new Handler();


    @Bind(R.id.app_bar_button) TextView appBarButton;
    @Bind(R.id.activity_title) TextView appBarTitle;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        appBarButton.setVisibility(View.INVISIBLE);
        //appBarTitle.setVisibility(View.INVISIBLE);

        densityMultiplier = getApplicationContext().getResources().getDisplayMetrics().density;
        Intent intent = getIntent();
        String message = intent.getStringExtra(SignInActivity.EXTRA_MESSAGE);
        String facebookId =intent.getStringExtra(SignInActivity.FB_ID);



        viewPager = (ViewPager) findViewById(R.id.pager);

        final LinearLayout dashboardLayout = (LinearLayout) findViewById(R.id.setting_dashboard);
        dashboardLayout.setVisibility(View.INVISIBLE);
        mAdapter = new MainFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        viewPager.setPageMargin(pageMargin);


        // Bind the tabs to the ViewPager
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

        //tabs.setIndicatorColor(R.color.colorPrimary);
        //tabs.setIndicatorHeight(10);

        ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "ViewPager onPageSelected");

                //changeColor(R.color.colorPrimary);
                //viewPager.setCurrentItem(position);
                tabs.notifyDataSetChanged();
                // on changing the page
                // make respected tab selected
                //getSupportActionBar().setSelectedNavigationItem(position);
/*

                mAdapter.getItem(position);
                if(position == 0){
                    activityFeed.setBackgroundResource(R.drawable.tab_selected);
                    nearMe.setBackgroundResource(R.drawable.tab_unselected);

                }else{
                    activityFeed.setBackgroundResource(R.drawable.tab_unselected);
                    nearMe.setBackgroundResource(R.drawable.tab_selected);
                }
*/
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                Log.d(TAG, "ViewPager onPageScrolled");

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                Log.d(TAG, "ViewPager onPageScrollStateChanged");

            }
        };

        /**
         * on swiping the viewpager make respective tab selected
         * */

        viewPager.setOnPageChangeListener(mPageChangeListener);
        // continued from above
        tabs.setOnPageChangeListener(mPageChangeListener);

        if(isInternetAvailable()) {

            fetchMoreEvents();

            mapTask = new GoogleMapTask() {
                @Override
                protected void onPostExecute(Void result) {
                    super.onPostExecute(result);
                    Set<EventDetailVO> events = DataStore.getEvents();

                    if (events == null || events.size() == 0) {
                        fetchMoreEvents();
                    }
                }
            };
            mapTask.execute(this);
        }else{
            noInternetToast();

        }

        setupFAB();
        setupDrawer();
        //changeColor(R.color.colorPrimary);

        //animate the Toolbar when it comes into the picture
       // AnimationUtils.animateToolbarDroppingDown(mContainerToolbar);

    }
    @Override
    protected void onResume() {
        super.onResume();
    }
/*
    private void setupTabs() {

        mTabHost = (MaterialTabHost) findViewById(R.id.materialTabHost);

        //Add all the Tabs to the TabHost
        for (int i = 0; i < mAdapter.getCount(); i++) {
            mTabHost.addTab(
                    mTabHost.newTab()
                            //.setIcon(mAdapter.getIcon(i))
                            .setText(mAdapter.getTitle(i))
                            .setTabListener(this));
        }

    }*/

    private void setupDrawer() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mContainerToolbar = (ViewGroup) findViewById(R.id.container_app_bar);
        //set the Toolbar as ActionBar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //setup the NavigationDrawer
        mDrawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
    }

    private void setupFAB() {
        //define the icon for the main floating action button
        ImageView iconFAB = new ImageView(this);
        iconFAB.setImageResource(R.drawable.ic_add_black_24dp);

        //set the appropriate background for the main floating action button along with its icon
        mFAB = new FloatingActionButton.Builder(this)
                .setContentView(iconFAB)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_LEFT)
                .setBackgroundDrawable(R.drawable.selector_button_red)
                .build();
        final Context theActivity = this;
        iconFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = new LatLng(getLatitute(), getLongitude());
                Intent intent = new Intent(theActivity, CreateActivity.class);
                intent.putExtra("currentLocation", currentLocation);
                startActivity(intent);
            }
        });

        }
    private void changeColor(int newColor) {
        //tabs.setIndicatorColor(R.color.white);


        // change ActionBar color just if an ActionBar is available
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            Drawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
            Drawable bottomDrawable = getResources().getDrawable(R.drawable.actionbar_bottom);
            LayerDrawable ld = new LayerDrawable(new Drawable[] { colorDrawable, bottomDrawable });

            if (oldBackground == null) {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    ld.setCallback(drawableCallback);
                } else {
                    //getSupportActionBar().setBackgroundDrawable(ld);
                    getSupportActionBar().setBackgroundDrawable(ld);

                }

            } else {

                TransitionDrawable td = new TransitionDrawable(new Drawable[] { oldBackground, ld });

                // workaround for broken ActionBarContainer drawable handling on
                // pre-API 17 builds
                // https://github.com/android/platform_frameworks_base/commit/a7cc06d82e45918c37429a59b14545c6a57db4e4
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    td.setCallback(drawableCallback);
                } else {
                    //getSupportActionBar().setBackgroundDrawable(td);
                    getSupportActionBar().setBackgroundDrawable(ld);

                }

                td.startTransition(200);

            }

            oldBackground = ld;

            // http://stackoverflow.com/questions/11002691/actionbar-setbackgrounddrawable-nulling-background-from-thread-handler
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

        }

        //currentColor = newColor;

    }
    private Drawable.Callback drawableCallback = new Drawable.Callback() {
        @Override
        public void invalidateDrawable(Drawable who) {
            getSupportActionBar().setBackgroundDrawable(who);
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            handler.postAtTime(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            handler.removeCallbacks(what);
        }
    };


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
            case R.id.action_logout:
               // getUser = null;
                logout();
                invalidateOptionsMenu();
                return true;
            case R.id.action_profile:
                intent = new Intent(this, PreferenceActivity.class);
                intent.putExtra("FromMain", true);
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
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
//    @Override
//    public void onTabSelected(MaterialTab materialTab) {
//
//        //when a Tab is selected, update the ViewPager to reflect the changes
//        viewPager.setCurrentItem(materialTab.getPosition());
//    }
//    @Override
//    public void onTabReselected(MaterialTab materialTab) {
//        //when a Tab is selected, update the ViewPager to reflect the changes
//        viewPager.setCurrentItem(materialTab.getPosition());
//    }
//    @Override
//    public void onTabUnselected(MaterialTab materialTab) {
//    }
    @Override
    public void onStop() {
        mapTask.disconnect();
        super.onStop();
    }

    public void fetchMoreEvents() {
        Log.d(TAG, "fetchMoreEvents: hasMoreToFetchDiscoverEvents" + hasMoreToFetchDiscoverEvents);
        Log.d(TAG, "fetchMoreEvents: hasMoreToFetchMyEvents" + hasMoreToFetchMyEvents);
        Log.d(TAG, "fetchMoreEvents: hasMoreToFetchGoingEvents" + hasMoreToFetchGoingEvents);

        if(hasMoreToFetchDiscoverEvents) {
            new GetEventsTask(this).execute(DataStore.nextDiscoverEventsPageNo());
        }
        if(hasMoreToFetchMyEvents) {
            new GetMyEventsTask(this).execute(DataStore.nextMyEventsPageNo());
        }
        if(hasMoreToFetchGoingEvents) {
            new GetRSVPedEventsTask(this).execute(DataStore.nextGoingEventsPageNo());
        }

    }

    private class GetEventsTask extends AsyncTask<Integer,Void,Integer> {
        Set<EventDetailVO> fetchedEvents ;

        Context activityContext;
        public GetEventsTask(Context theContext) {
             activityContext = theContext;
        }

        Double latitudeToFetch,longitudeToFetch;

        //@Override
        protected void onPreExecute(){
            isFetchExecuting = true;
//            mAdapter.getMainFragment().setProgressBarVisibile();
            if(latitute != null && longitude != null){
                latitudeToFetch = latitute;
                longitudeToFetch = longitude;
            }else{
                latitudeToFetch = userLastLatitude;
                longitudeToFetch = userLastLongitude;
            }
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            Integer pageNo = arg0[0];
            Log.i("MainActvity", "PageNo" + pageNo);
            fetchedEvents = DataStore.getAroundYouEvents(latitudeToFetch, longitudeToFetch, pageNo, activityContext);
            return fetchedEvents.size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i("GetEventsTask", "onPostExecute" + fetchedEvents.size());
            if(result == 0){
                hasMoreToFetchDiscoverEvents = false;
            }else {
                //viewPager.setAdapter(mAdapter);
                mAdapter.getMainFragment().fillCards();
                mAdapter.getMainFragment().setProgressBarInVisibile();
//                mAdapter.getMyEventsFragment().fillCards(fetchedEvents);
                //mAdapter.getGoingEventsFragment().fillCards();

            }
            isFetchExecuting = false;
        }
    }


    private class GetMyEventsTask extends AsyncTask<Integer,Void,Integer> {
        Set<EventDetailVO> fetchedEvents ;

        Context activityContext;
        public GetMyEventsTask(Context theContext) {
            activityContext = theContext;
        }

        //@Override
        protected void onPreExecute(){
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            Integer pageNo = arg0[0];
            Log.i("GetMyEventsTask", "PageNo" + pageNo);
            fetchedEvents = DataStore.getMyEvents(pageNo,activityContext);
            return fetchedEvents.size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i("GetMyEventsTask", "onPostExecute" + fetchedEvents.size());
            if(result == 0){
                hasMoreToFetchMyEvents = false;
            }else {
                //viewPager.setAdapter(mAdapter);
                //mAdapter.getMainFragment().fillCards();
                //mAdapter.getMyEventsFragment().fillCards();
                //mAdapter.getGoingEventsFragment().fillCards();
                mAdapter.getMyEventsFragment().setProgressBarInVisibile();

            }
        }
    }


    private class GetRSVPedEventsTask extends AsyncTask<Integer,Void,Integer> {
        Set<EventDetailVO> fetchedEvents ;

        Context activityContext;
        public GetRSVPedEventsTask(Context theContext) {
            activityContext = theContext;
        }

        //@Override
        protected void onPreExecute(){
        }

        @Override
        protected Integer doInBackground(Integer... arg0) {
            Integer pageNo = arg0[0];
            Log.i("GetRSVPedEventsTask", "PageNo " + pageNo);
            fetchedEvents = DataStore.getRSVPedEvents(pageNo,activityContext);
            return fetchedEvents.size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.i("GetRSVPedEventsTask", "onPostExecute " + fetchedEvents.size());
            if(result == 0){
                hasMoreToFetchGoingEvents = false;
            }else {
                //viewPager.setAdapter(mAdapter);
                //mAdapter.getMainFragment().fillCards();
                //mAdapter.getMyEventsFragment().fillCards(fetchedEvents);
                mAdapter.getGoingEventsFragment().fillCards();
                mAdapter.getGoingEventsFragment().setProgressBarInVisibile();


            }
        }
    }






    @Override
    public void onClick(View v) {
        //call instantiate item since getItem may return null depending on whether the PagerAdapter is of type FragmentPagerAdapter or FragmentStatePagerAdapter
        Fragment fragment = (Fragment) mAdapter.instantiateItem(viewPager, viewPager.getCurrentItem());
        if (fragment instanceof SortListener) {

            if (v.getTag().equals(TAG_SORT_NAME)) {
                //call the sort by name method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByName();
            }
            if (v.getTag().equals(TAG_SORT_DATE)) {
                //call the sort by date method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByDate();
            }
            if (v.getTag().equals(TAG_SORT_RATINGS)) {
                //call the sort by ratings method on any Fragment that implements sortlistener
                ((SortListener) fragment).onSortByRating();
            }
        }

    }
    public void onDrawerItemClicked(int index) {
        viewPager.setCurrentItem(index);
    }
    public void onDrawerSlide(float slideOffset) {
        toggleTranslateFAB(slideOffset);
    }
    private void toggleTranslateFAB(float slideOffset) {
        if (mFABMenu != null) {
            if (mFABMenu.isOpen()) {
                mFABMenu.close(true);
            }
            mFAB.setTranslationX(slideOffset * 200);
        }
    }

}
