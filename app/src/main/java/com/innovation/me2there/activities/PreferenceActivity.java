package com.innovation.me2there.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.innovation.me2there.R;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.fragments.Mee2ThereCardFragmentBase;
import com.innovation.me2there.fragments.PreferenceDetailsFragment;
import com.innovation.me2there.fragments.PreferenceTopicsFragment;
import com.innovation.me2there.model.UserVO;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PreferenceActivity extends Mee2ThereActivity {
    @Bind(R.id.app_bar_button)
    TextView appBarButton;
    @Bind(R.id.activity_title) TextView appBarTitle;
    boolean isSave = false;
    Boolean fromMain = false;
    static final String FROM_MAIN = "FromMain";
    PreferenceTopicsFragment topicsFragment;
    PreferenceDetailsFragment newDetailsFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);
        fromMain= getIntent().getBooleanExtra(PreferenceActivity.FROM_MAIN, false);

        ButterKnife.bind(this);
        appBarButton.setText("Next");
        appBarTitle.setText("Preferences");

        topicsFragment = new PreferenceTopicsFragment();
        newDetailsFragment =  PreferenceDetailsFragment.newInstance(DataStore.getUser().getAboutYourself(),
                DataStore.getUser().getDistanceWillingToTravel());
        Bundle newBundle = new Bundle();
        topicsFragment.setArguments(newBundle);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (savedInstanceState == null) {
            transaction
                    .add(R.id.fragment_topics, topicsFragment)
                    .commit();
        }
        appBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSave) {
                    isSave = true;
                    Log.i("ButtonClick", "Next Clicked");
                    appBarButton.setText("Save");

                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_topics, newDetailsFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }else{

                    new UserUpdateTask().execute(DataStore.getUser());

                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_preference, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class UserUpdateTask extends AsyncTask<UserVO, Void, Void> {

        @Override
        protected Void doInBackground(UserVO... users) {
            UserVO user = users[0];
            Log.d("DataStore", "createUser" + user);
            user.setAboutYourself(newDetailsFragment.getAboutText());
            user.setDistanceWillingToTravel(newDetailsFragment.getDistanceWilling());
            Log.i(" update user", "distance  " + user.getDistanceWillingToTravel());

            DataStore.updateUser(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            finish();
            if(!fromMain) {
                goToMainActivity();
            }

        }
    };

}
