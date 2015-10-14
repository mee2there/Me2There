package com.innovation.me2there.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.innovation.me2there.R;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.db.MongoDB;
import com.innovation.me2there.model.UserVO;

import butterknife.Bind;


public class LaunchActivity extends Mee2ThereActivity {

    private ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Log.i("LaunchActivity", "Last user found " + lastUserToken);
        if (lastUserToken != null) {
            Log.i("LaunchActivity", "to Redirect per Last user found " + lastUserToken);
            setContentView(R.layout.activity_progress);
            bar = (ProgressBar) findViewById(R.id.progressBar);
            if(isInternetAvailable()) {
                new ProgressTask().execute();
            }else {
                noInternetToast();
                //finish();

            }
        }else{
            setContentView(R.layout.activity_launch);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_launch, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }


    public void launchSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from launch screen");
        startActivity(intent);
    }


    public void launchLogin(View view) {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from launch screen");
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ProgressTask extends AsyncTask<Void,Void,Void> {
        //@Override
        protected void onPreExecute(){
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            Log.i("LaunchActivity", "Get Events for " + lastUserToken);

            UserVO user = DataStore.verifyUser(lastUserToken);
            DataStore.setUser(user);
            //DataStore.addToEvents(db.getEvents(userLastLatitude, userLastLongitude, DataStore.nextDiscoverEventsPageNo()));

            Log.i("LaunchActivity", "end of Events for" + lastUserToken);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            bar.setVisibility(View.GONE);
            finish();
            goToMainActivity();

        }
    }

}

