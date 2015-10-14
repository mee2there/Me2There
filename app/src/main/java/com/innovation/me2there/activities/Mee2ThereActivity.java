package com.innovation.me2there.activities;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.IntegerRes;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.innovation.me2there.R;
import com.innovation.me2there.adapters.MainFragmentAdapter;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.EventDetailVO;

/**
 * Created by ashley on 5/9/15.
 */
public class Mee2ThereActivity extends ActionBarActivity {
    //protected Typeface font;
    protected final static String EXTRA_MESSAGE = "com.innovation.me2there.MESSAGE";
    protected final static String FB_ID = "com.innovation.me2there.FBID";
    protected static final String APP_PREFERENCES = "App_Prefs";
    protected static final String loggedUserId = "UserKey";

    public static final String storedLatitude = "StoredLatitude";
    public static final String storedLongitude = "StoredLongitude";

    String lastUserToken;
    protected TextView validationText;

    protected Double userLastLatitude;
    protected Double userLastLongitude;

    protected Double latitute;
    protected Double longitude;

    public Double getLatitute() {
        if (latitute != null) {
            return latitute;
        } else {
            return userLastLatitude;
        }
    }

    public Double getLongitude() {

        if (longitude != null) {
            return longitude;
        } else {
            return userLastLongitude;
        }
    }

    public void setLatitute(Double latParm) {
        latitute = latParm;
    }

    public void setLongitude(Double longParm) {
        longitude = longParm;
    }

    public SharedPreferences getSharedpreferences() {
        return getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
    }


    protected void inflateActionBar() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isInternetAvailable()) {
            Log.i("Mee2ThereActivity", "No Internet detected");
        }
        AssetManager mngr = getAssets();
        //font = Typeface.createFromAsset(mngr, "fontello.ttf");
        inflateActionBar();
        SharedPreferences sharedpreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        lastUserToken = sharedpreferences.getString(loggedUserId, null);

        String userLastLatitudeStr = sharedpreferences.getString(Mee2ThereActivity.storedLatitude, "43.7000");
        String userLastLongitudeStr = sharedpreferences.getString(Mee2ThereActivity.storedLongitude, "79.4000");

        userLastLatitude = new Double(userLastLatitudeStr);
        userLastLongitude = new Double(userLastLongitudeStr);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected static void scaleImage(ImageView view, Bitmap bitmap) {

        Drawable drawing = view.getDrawable();
        if (drawing == null) {
            return; // Checking for null & return, as suggested in comments
        }

        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(400);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private static int dpToPx(int dp) {
        float density = MainActivity.densityMultiplier;
        return Math.round((float) dp * density);
    }

    protected void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_MESSAGE, "from Start screen");
        //intent.putExtra(FB_ID,currentUser.getName() );
        intent.putExtra(FB_ID, "Ashley George");
        startActivity(intent);
    }

    protected void logout() {
        lastUserToken = null;
        SharedPreferences.Editor editor = getSharedpreferences().edit();
        editor.putString(loggedUserId, null);
        editor.commit();
        this.finish();
        Intent intent = new Intent(this, LaunchActivity.class);
        startActivity(intent);
    }

    protected void saveUser() {
        SharedPreferences.Editor editor = getSharedpreferences().edit();
        editor.putString(loggedUserId, DataStore.getUser().getUserToken());
        editor.commit();

    }

    protected void removeView(View toRemoveView) {
        if (toRemoveView != null) {
            ViewGroup layout = (ViewGroup) toRemoveView.getParent();
            if (null != layout) { //for safety only  as you are doing onClick
                layout.removeView(toRemoveView);
            }
        }
    }

    protected void addView(ViewGroup layout, View toRemoveView) {
        if (toRemoveView != null && layout != null) {
            layout.addView(toRemoveView);
        }
    }

    //Get the path from Uri
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        //This method was deprecated in API level 11
        //Cursor cursor = managedQuery(contentUri, proj, null, null, null);

        CursorLoader cursorLoader = new CursorLoader(
                this,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    protected boolean isInternetAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    protected void noInternetToast() {
        Toast.makeText(this, "No Internet Connection Detected", Toast.LENGTH_LONG).show();
        setContentView(R.layout.no_network_layout);
        TextView appBarButton = (TextView) findViewById(R.id.app_bar_button);
        appBarButton.setVisibility(View.INVISIBLE);
        Button refreshButton = (Button) findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()) {
                    Intent starterIntent = getIntent();
                    finish();
                    startActivity(starterIntent);
                }else{
                    Toast.makeText(getApplicationContext(), "No Internet Connection Detected", Toast.LENGTH_LONG).show();

                }
            }
        });

    }


    public int dimen(@DimenRes int resId) {
        return (int) getResources().getDimension(resId);
    }

    public int color(@ColorRes int resId) {
        return getResources().getColor(resId);
    }

    public int integer(@IntegerRes int resId) {
        return getResources().getInteger(resId);
    }

}