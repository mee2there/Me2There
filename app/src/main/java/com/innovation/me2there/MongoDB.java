package com.innovation.me2there;

import android.util.Log;

import com.google.android.gms.internal.lo;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ashley on 2/10/15.
 */
public class MongoDB {

    private DBCollection userCollection;

    public MongoDB(){
        //final BasicDBObject[] seedData = createSeedData();
        connectDB();
        //eventCollection.insert(seedData);
    }

    private void  connectDB() {
        try {
            //mongodb://mee2thereservice:rva@2015@ds041831.mongolab.com:41831/mee2theredb
            // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
            MongoClientURI uri = new MongoClientURI("mongodb://mee2thereservice:rva2015@ds041831.mongolab.com:41831/mee2theredb");
            MongoClient client = new MongoClient(uri);
            DB db = client.getDB(uri.getDatabase());
            Log.i("connectDB","after getDB : "+db.toString());
        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */

            userCollection = db.getCollection("users");
            Log.i("connectDB","after eventCollection : "+userCollection.toString());
        }catch (UnknownHostException unknownHost) {
            Log.e("connectDB","Exception : "+unknownHost.getMessage());

        }
        }



    public boolean insertUser(String userId,String userName, String locationDetails) {
        BasicDBObject newUser = new BasicDBObject();
        newUser.put("userId", userId);
        newUser.put("userName", userName);
        newUser.put("locationDetails", locationDetails);
        userCollection.insert(newUser);
        return true;
    }

    public UserEntity getUserInfo(String userId) {

        return  null;
    }


}
