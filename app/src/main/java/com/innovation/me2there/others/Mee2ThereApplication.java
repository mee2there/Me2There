package com.innovation.me2there.others;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.LruCache;

import com.github.nkzawa.socketio.client.IO;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by ashley on 8/16/15.
 */
public class Mee2ThereApplication extends Application {

    public static final String API_KEY_ROTTEN_TOMATOES = "54wzfswsa4qmjg8hjwa64d4c";
    //public static final String SERVER_ADDRESS = "http://192.168.0.12:3000";
    public static final String SERVER_ADDRESS = "http://mee2there-dbnode.rhcloud.com/";
    public static final String EVENTS_URL = SERVER_ADDRESS+"/events";
    public static final String USERS_URL = SERVER_ADDRESS+"/users";
    public static final String GOING_EVENTS_URL = EVENTS_URL+"/goingevents";
    public static final String MY_EVENTS_URL = EVENTS_URL+"/myevents";

    public static final String IMAGE_UPLOAD_URL = EVENTS_URL+"/uploadimage";
    public static final String CREATE_EVENT_URL = EVENTS_URL+"/createevent";
    public static final String RSVP_EVENT_URL = EVENTS_URL+"/rsvpevent";
    public static final String EVENTS_PARTICIPANTS_URL = EVENTS_URL+"/eventparticipants";



    public static final String CREATE_USER_URL = USERS_URL+"/createuser";
    public static final String UPDATE_USER_URL = USERS_URL+"/updateuser";
    public static final String AUTHENTICATE_USER_URL = USERS_URL+"/authenticate";
    public static final String VERIFY_USER_URL = USERS_URL+"/verify";

    private static Mee2ThereApplication sInstance;

    private RefWatcher refWatcher;

    public static Mee2ThereApplication getInstance() {
        return sInstance;
    }

    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }
    private LruCache<String, Bitmap> mMemoryCache;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        //  refWatcher = LeakCanary.install(this);

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            if(key != null && bitmap != null) {
                mMemoryCache.put(key, bitmap);
            }
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        if(key != null) {
            return mMemoryCache.get(key);
        }else{
            return null;
        }
    }

    public static RefWatcher getRefWatcher(Context context) {
        Mee2ThereApplication application = (Mee2ThereApplication) context.getApplicationContext();
        return application.refWatcher;
    }


    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    public static void saveToPreferences(Context context, String preferenceName, boolean preferenceValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(preferenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPreferences(Context context, String preferenceName, String defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getString(preferenceName, defaultValue);
    }

    public static boolean readFromPreferences(Context context, String preferenceName, boolean defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        return sharedPreferences.getBoolean(preferenceName, defaultValue);
    }

}
