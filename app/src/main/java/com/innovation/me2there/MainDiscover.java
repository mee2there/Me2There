package com.innovation.me2there;

import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;


public class MainDiscover extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private MainActivity activity;
    private View rootView;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public boolean cardsLoaded;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    public DataStore mee2ThereDataStore;

    private RecyclerView mRecyclerView;
    private CardViewAdapter mAdapter;

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main_discover, container, false);
        activity = (MainActivity) getActivity();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.custom_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.activity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //fillCards(activity);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        buildGoogleApiClient();
    }

    public void fillCards(MainActivity activity) {
        if (mee2ThereDataStore != null) {
            //Need to retrieve the activity list and image name from DB
            List<EventDetailVO> events = DataStore.getActivities();
//
//            ArrayList<Card> cards = new ArrayList<Card>();
//
//            for (EventDetailVO anEvent : events) {
//                // Create a Card
//                Card cardItem = new ActivityCard(activity, R.layout.row_card, anEvent);
//                cards.add(cardItem);
//            }
//
//            CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(activity, cards);
//
//            CardListView listView = (CardListView) rootView.findViewById(R.id.activityList);
//            if (listView != null) {
//                listView.setAdapter(mCardArrayAdapter);
//            }


            mAdapter = new CardViewAdapter(events, R.layout.row_card, this.activity);
            mRecyclerView.setAdapter(mAdapter);

            cardsLoaded = true;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this.activity)
                .addApi(LocationServices.API)
                        //.addScope(LocationServices.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            activity.setLatitute(mLastLocation.getLatitude());
            activity.setLongitude(mLastLocation.getLongitude());
        }
        Log.i("Main Activity", "lat:" + activity.getLatitute().toString() + " long:" + activity.getLongitude().toString());
        if (!cardsLoaded) {

            mee2ThereDataStore = new DataStore(activity.getLatitute(), activity.getLongitude());
//            MainDiscover discover = (MainDiscover) getSupportFragmentManager().findFragmentById(R.layout.fragment_main_discover);
            fillCards(activity);
            cardsLoaded = true;

        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.i("MainActivity", "Location Connection Suspended ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.
        Log.i("Main Activity", "Location Connection Failed " + result.toString());
        Log.i("Main Activity", "Location Connection Failed Resolution " + result.getResolution());
        Log.i("Main Activity", "Location Connection Failed Error Code " + result.getErrorCode());
        Log.i("Main Activity", "Location Connection Failed Error Code " + result.hasResolution());
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this.activity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }

    }
}
