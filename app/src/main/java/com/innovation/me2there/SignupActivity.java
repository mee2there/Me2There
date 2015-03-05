package com.innovation.me2there;
import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.facebook.widget.ProfilePictureView;



public class SignupActivity extends FragmentActivity {

    private LoginButton loginBtn;
    private TextView txtUserName;
    private TextView txtEmail;
    private TextView txtLocation;



    private Button launchBtn;
    private TextView greeting;
    private UiLifecycleHelper uiHelper;
    private ProfilePictureView profilePictureView;
    private GraphUser currentUser;

    public final static String EXTRA_MESSAGE = "com.innovation.me2there.MESSAGE";
    public final static String FB_ID = "com.innovation.me2there.FBID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);



        loginBtn = (LoginButton) findViewById(R.id.fbsignup_button);

        launchBtn = (Button)findViewById(R.id.btnEnroll);


        txtUserName = (TextView)findViewById(R.id.txtPersonName);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        txtLocation = (TextView)findViewById(R.id.txtAddress);

        //  loginBtn.setReadPermissions(Arrays.asList("email"));
        loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback()
        {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    currentUser = user;
                    //greeting.setText("You are currently logged in as " + user.getName());

                    txtUserName.setText(user.getName());
                  //  txtEmail.setText(user.getLocation().getName());

                    launchBtn.setVisibility(View.VISIBLE);


                } else {
                    //greeting.setText("You are not logged in.");
                    launchBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {
                Log.d("MainActivity", "Facebook session opened.");
            } else if (state.isClosed()) {
                Log.d("MainActivity", "Facebook session closed.");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
    }


    public void btnSignupClick(View view) {

        MongoDB mongoDB = new MongoDB();
        mongoDB.insertUser(currentUser.getId(),txtUserName.toString(),txtLocation.toString());

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from signup screen");
        intent.putExtra(FB_ID,currentUser.getName() );
        startActivity(intent);
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
}