package com.innovation.me2there;


import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

//import android.support.v7.app.ActionBar;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActionBar.TabListener {

    private static DataStore mee2ThereDataStore;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation ;
    private Double latitute;
    private Double longitutde;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    public static float densityMultiplier;
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        buildGoogleApiClient();
		setContentView(R.layout.activity_main);

        densityMultiplier = getApplicationContext().getResources().getDisplayMetrics().density;
        Intent intent = getIntent();
        String message = intent.getStringExtra(StartActivity.EXTRA_MESSAGE);
        String facebookId =intent.getStringExtra(StartActivity.FB_ID);

        // Set up the action bar.
        //final ActionBar actionBar = getSupportActionBar();
        final ActionBar actionBar = getActionBar();
        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        // actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        TextView createButton = (TextView) findViewById(R.id.btnOrganize);
        createButton.setKeyListener(null);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = new LatLng(latitute,longitutde);
                Intent intent = new Intent(getApplicationContext(), CreateActivity.class);
                intent.putExtra("currentLocation",currentLocation);
                startActivity(intent);
            }
        });

    }
    protected synchronized void buildGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                //.addScope(LocationServices.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitute = mLastLocation.getLatitude();
            longitutde = mLastLocation.getLongitude();
        }
        Log.i("Main Activity", "lat:" + latitute.toString() + " long:" + longitutde.toString());
        fillCards();
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
        Log.i("Main Activity","Location Connection Failed "+result.toString());
        Log.i("Main Activity","Location Connection Failed Resolution "+result.getResolution());
        Log.i("Main Activity","Location Connection Failed Error Code "+result.getErrorCode());
        Log.i("Main Activity","Location Connection Failed Error Code "+result.hasResolution());
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
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

    private void fillCards(){
        if(mee2ThereDataStore == null) {
            mee2ThereDataStore = new DataStore(latitute, longitutde);
        }
        //Need to retrieve the activity list and image name from DB
        List<EventDetailVO> events = DataStore.getActivities();

        ArrayList<Card> cards = new ArrayList<Card>();

        for(EventDetailVO anEvent:events){
            // Create a Card
            Card cardItem = new ActivityCard(this,R.layout.row_card,anEvent);
            cards.add(cardItem);
        }

        CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(this, cards);

        CardListView listView = (CardListView) this.findViewById(R.id.activityList);
        if (listView != null) {
            listView.setAdapter(mCardArrayAdapter);
        }

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


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
//            case R.id.action_profile:
//                intent = new Intent(this,EditProfile.class);
//                startActivity(intent);
//                return true;
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
}
