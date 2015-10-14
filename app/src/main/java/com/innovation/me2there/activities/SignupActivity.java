package com.innovation.me2there.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.innovation.me2there.R;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.db.MongoDB;
import com.innovation.me2there.model.LocationVO;
import com.innovation.me2there.model.PreferenceVO;
import com.innovation.me2there.model.UserVO;
import com.innovation.me2there.util.LocationUtil;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignupActivity extends Mee2ThereActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private LoginButton loginBtn;
    private TextView txtUserFullName;
    private TextView txtEmail;
    private EditText txtLocation, txtPassword, retypePassword;
    private LocationVO locationDetails;

    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private Location mLastLocation;
    private Double latitute;
    private Double longitutde;
    SharedPreferences sharedpreferences;
    public static final String APP_PREFERENCES = "App_Prefs" ;
    public static final String loogedUserId = "UserKey";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;



    String get_id, get_name, get_password, get_retypePassword, get_gender, get_email, get_birthday, get_locale, get_location;


    private Button launchBtn;
    private TextView greeting;
    private UiLifecycleHelper uiHelper;
    private ProfilePictureView profilePictureView;
    private GraphUser currentUser;

    private String LOG_TAG = "Signupactivity";
    public final static String EXTRA_MESSAGE = "com.innovation.me2there.MESSAGE";
    public final static String FB_ID = "com.innovation.me2there.FBID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);


        openActiveSession(this, true, statusCallback, Arrays.asList(
                new String[]{"email", "user_location", "user_birthday",
                        "user_likes", "publish_actions"}), savedInstanceState);




        loginBtn = (LoginButton) findViewById(R.id.fbsignup_button);

        launchBtn = (Button) findViewById(R.id.btnEnroll);


        txtUserFullName = (TextView) findViewById(R.id.txtPersonName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtLocation = (EditText) findViewById(R.id.txtAddress);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        retypePassword =  (EditText) findViewById(R.id.retypePassword);

        Log.d(LOG_TAG, "Location service started");



        sharedpreferences = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);



        //  loginBtn.setReadPermissions(Arrays.asList("email"));
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    currentUser = user;
                    //greeting.setText("You are currently logged in as " + user.getName());


                    txtUserFullName.setText(user.getName());
                    //  txtEmail.setText(user.getLocation().getName());

                    launchBtn.setVisibility(View.VISIBLE);


                } else {
                    //greeting.setText("You are not logged in.");
                    //launchBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    private Session openActiveSession(Activity activity, boolean allowLoginUI,
                                      Session.StatusCallback callback, List<String> permissions, Bundle savedInstanceState) {
        Session.OpenRequest openRequest = new Session.OpenRequest(activity);

        openRequest.setPermissions(permissions);
        openRequest.setCallback(callback);

        Session session = Session.getActiveSession();
        Log.d(LOG_TAG, "" + session);
        Log.d(LOG_TAG, " Open Active Session");
        if (session == null) {
            Log.d(LOG_TAG, "" + savedInstanceState);
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) || allowLoginUI) {
                session.openForRead(openRequest);
                return session;
            }
        }
        return null;
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        Log.i("Main Activity", "Trying to get the location info");

        if (!mGoogleApiClient.isConnected()) {

            Log.i("Main Activity", "Google Api client is not connected $$$$$$$$$");
        }


        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if (mLastLocation != null) {
            latitute = mLastLocation.getLatitude();
            longitutde = mLastLocation.getLongitude();
        }


        Log.i("Main Activity", "lat:" + latitute.toString() + " long:" + longitutde.toString());

        locationDetails = LocationUtil.getLocation(latitute, longitutde, this);

        txtLocation.setText(locationDetails.cityAndState());

        Log.i("Main Activity", "Last Known location based on coordinates \n" + locationDetails);
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

    protected synchronized void buildGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    protected void onStart() {
        Log.i("Main Activity", "On start - start");

        super.onStart();
        mGoogleApiClient.connect();

        Log.i("Main Activity", "On start - End");
    }



    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {

            if (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException) {
                //new AlertDialog.Builder(this).setTitle(R.string.cancelled).setMessage(R.string.permission_not_granted).setPositiveButton(R.string.ok, null).show();
                Log.e(LOG_TAG, "Facebook session not opened.");

            } else {
                if (state.isOpened()) {
                    Log.d("MainActivity", "Facebook session opened.");

                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        public void onCompleted(GraphUser user, Response response) {
                            if (response != null) {
                                // do something with <response> now
                                try {

                                    Log.d(LOG_TAG, "Trying to get FB info.");

                                    Log.d(LOG_TAG, user.getId());
                                    Log.d(LOG_TAG, user.getName());
                                    Log.d(LOG_TAG, (String) user.getProperty("gender"));
                                    Log.d(LOG_TAG, (String) user.getProperty("email"));
                                    Log.d(LOG_TAG, (String) user.getProperty("locale"));
                                    //Log.d(LOG_TAG, user.getBirthday());
                                    //Log.d(LOG_TAG, user.getLocation().toString());

                                    Log.d(LOG_TAG, user.getInnerJSONObject().getJSONObject("location").getString("id"));

                                    get_id = user.getId();
                                    get_name = user.getName();
                                    get_gender = (String) user.getProperty("gender");
                                    get_email = (String) user.getProperty("email");
                                    get_birthday = user.getBirthday();
                                    get_locale = (String) user.getProperty("locale");
                                    get_location = user.getLocation().toString();

                                    Log.d(LOG_TAG, user.getId() + "; " +
                                            user.getName() + "; " +
                                            (String) user.getProperty("gender") + "; " +
                                            (String) user.getProperty("email") + "; " +
                                            user.getBirthday() + "; " +
                                            (String) user.getProperty("locale") + "; " +
                                            user.getLocation());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(LOG_TAG, "Exceception: " + e.getMessage());
                                }

                            }
                        }
                    });
                } else if (state.isClosed()) {
                    Log.d("MainActivity", "Facebook session closed.");
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }


    public void btnSignupClick(View view) {
        get_id = txtEmail.getText().toString();
        get_name = txtUserFullName.getText().toString();
        get_password = txtPassword.getText().toString();
        get_retypePassword = retypePassword.getText().toString();

        if(validateSignup()) {
            Intent intent = new Intent(this, PreferenceActivity.class);

            startActivity(intent);

            UserVO newUser = new UserVO(get_name, get_password, get_id, null, locationDetails, new ArrayList<PreferenceVO>());
            Log.d(LOG_TAG, "New User to be created" + get_name);

            //DataStore.createOrUpdateUser(newUser);
            new UserSaveTask().execute(newUser);

            saveUser();


        }


    }

    private Boolean validateSignup(){
        Boolean isValid = true;


        if(get_name.length() == 0 ) {
            validationText.setText("Please enter an Name ");
            isValid = false;
        }
        else if(get_id.length() == 0) {
            validationText.setText("Please enter an Email ");
            isValid = false;
        }
        else if(get_password.length() == 0) {
            validationText.setText("Please enter a Password ");
            isValid = false;
        }
        else if(!get_password.equals(get_retypePassword)) {
            validationText.setText("Passwords do not match ");
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }


    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException) {
            new AlertDialog.Builder(this).setTitle(R.string.cancelled).setMessage(R.string.permission_not_granted).setPositiveButton(R.string.ok, null).show();

        } else {

            Session sessionObj = Session.getActiveSession();

            if ((sessionObj != null && sessionObj.isOpened())) {
                // Kill login activity and go back to main
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("fb_session", sessionObj);
                startActivity(intent);
            }
        }
    }


    private class UserSaveTask extends AsyncTask<UserVO, Void, Void>  {

        @Override
        protected Void doInBackground(UserVO... users) {

            UserVO user = users[0];
            Log.d("DataStore", "createUser" + user);
            DataStore.setUser(user);
//            DataStore.getUser().set_objID(insertResult);
            String userToken = DataStore.createUser(user);
            user.setUserToken(userToken);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            finish();
        }

    };


}