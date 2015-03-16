package com.innovation.me2there;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;
import it.gmariotti.cardslib.library.view.CardView;

public class MainActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static DataStore mee2ThereDataStore;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation ;
    private Double latitute;
    private Double longitutde;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;

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


        Intent intent = getIntent();
        String message = intent.getStringExtra(StartActivity.EXTRA_MESSAGE);
        String facebookId =intent.getStringExtra(StartActivity.FB_ID);
        Button createButton = (Button) findViewById(R.id.btnOrganize);
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

}
