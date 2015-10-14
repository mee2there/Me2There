package com.innovation.me2there.util;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.innovation.me2there.activities.Mee2ThereActivity;

/**
 * Created by ashley on 6/13/15.
 */
public class GoogleMapTask extends AsyncTask<Mee2ThereActivity,Void,Void> implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Mee2ThereActivity requestedActivity;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    protected Void doInBackground(Mee2ThereActivity... arg0) {
        requestedActivity = arg0[0];
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        try {
            while (!mGoogleApiClient.isConnected()) {
                Thread.sleep(200);
            }
        }catch(Exception e){
            Log.i("MainActivity", "Exception from thread sleep? "+e.getMessage());

            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        requestedActivity.setLatitute(mLastLocation.getLatitude());
        requestedActivity.setLongitude(mLastLocation.getLongitude());

    }



    protected synchronized void buildGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(requestedActivity)
                .addApi(LocationServices.API)
                        //.addScope(LocationServices.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void disconnect() {
        if(mGoogleApiClient!= null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            requestedActivity.setLatitute(mLastLocation.getLatitude());
            requestedActivity.setLongitude(mLastLocation.getLongitude());

            SharedPreferences.Editor editor = requestedActivity.getSharedpreferences().edit();
            editor.putString(Mee2ThereActivity.storedLatitude, requestedActivity.getLatitute().toString());
            editor.putString(Mee2ThereActivity.storedLongitude, requestedActivity.getLongitude().toString());
            editor.commit();
        }
        Log.i("Main Activity", "lat:" + requestedActivity.getLatitute().toString() + " long:" + requestedActivity.getLongitude().toString());
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
                result.startResolutionForResult(requestedActivity, REQUEST_RESOLVE_ERROR);
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
