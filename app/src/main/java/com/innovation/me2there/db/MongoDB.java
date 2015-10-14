package com.innovation.me2there.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.innovation.me2there.model.EventDetailVO;
import com.innovation.me2there.model.UserVO;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import org.bson.types.ObjectId;

import java.io.ByteArrayOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ashley on 2/10/15.
 */
public class MongoDB {

    private DBCollection eventCollection;
    private DBCollection userCollection;

    private DB db;
    private GridFS fs;

    private Double defaultMaxDistance = 10.00;
    public MongoDB(){
        connectDB();
    }

    private void  connectDB() {
        try {
            //mongodb://mee2thereservice:rva@2015@ds041831.mongolab.com:41831/mee2theredb
            // Standard URI format: mongodb://[dbuser:dbpassword@]host:port/dbname
            MongoClientURI uri = new MongoClientURI("mongodb://mee2thereservice:rva2015@ds041831.mongolab.com:41831/mee2theredb");
            MongoClient client = new MongoClient(uri);
            db = client.getDB(uri.getDatabase());
            Log.i("connectDB","after getDB : "+db.toString());
        /*
         * First we'll add a few songs. Nothing is required to create the
         * songs collection; it is created automatically when we insert.
         */

            eventCollection = db.getCollection("events");
            eventCollection.createIndex(new BasicDBObject("loc", "2d"), "geospacialIdx");

            userCollection = db.getCollection("users");

            Log.i("connectDB","after eventCollection : "+eventCollection.toString());

            fs = new GridFS(db);
        }catch (UnknownHostException unknownHost) {
            Log.e("connectDB","Exception : "+unknownHost.getMessage());

        }
    }

    public ObjectId insertEvent(EventDetailVO event) {
        BasicDBObject newEvent = event.bsonFromPojo();
        eventCollection.insert(newEvent);
        return (ObjectId)newEvent.get("_id");
    }

    public boolean updateUserToEvent(String eventID,String userID){
        DBObject event = new BasicDBObject();
        event.put("_id", eventID);
        BasicDBObject userMod = new BasicDBObject();
        userMod.put("participants",userID);
        BasicDBObject usersAdded = new BasicDBObject();
        usersAdded.put("$addToSet", userMod);
        eventCollection.update(event, usersAdded, true /* upsert */, false /* multi */);
        return true;
    }
    public boolean withdrawUserToEvent(String eventID,String userID){
        DBObject event = new BasicDBObject();
        event.put("_id", eventID);
        BasicDBObject toRemove = new BasicDBObject("participants", userID);
        eventCollection.update(event, new BasicDBObject("$pull", toRemove));
        return true;
    }
    public List<DBObject> nearWithMaxDistance(BasicDBObject filterNearMe ,int pageNumber) {
        int limitPerPage = 5;
        final BasicDBObject query = new BasicDBObject("loc", filterNearMe);

        return eventCollection
                .find(query)
                //.sort(new BasicDBObject("date",-1))
                .skip((pageNumber-1)*limitPerPage)
                .limit(limitPerPage)
                .toArray();
    }

    public void withinBoxExample() {
        final LinkedList<double[]> box = new LinkedList<double[]>();

        // Set the lower left point - Washington square park
        box.addLast(new double[] {  -73.99756, 40.73083 });

        // Set the upper right point - Flatiron Building
        box.addLast(new double[] { -73.988135, 40.741404 });

        final BasicDBObject query
                = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$box", box)));

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }

    public void withinPolygonExample() {

        final LinkedList<double[]> polygon = new LinkedList<double[]>();

        // Long then lat

        // Create the shape.
        polygon.addLast(new double[] {  -73.99756, 40.73083 });
        polygon.addLast(new double[] { -73.988135, 40.741404 });
        polygon.addLast(new double[] { -73.99171, 40.738868  });

        final BasicDBObject query
                = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$polygon", polygon)));

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }

    public void withinCircleExample() {
        final LinkedList circle = new LinkedList();

        // Set the center - 10gen Office
        circle.addLast(new double[] { -73.99171, 40.738868 });

        // Set the radius
        circle.addLast(0.01);

        final BasicDBObject query
                = new BasicDBObject("loc", new BasicDBObject("$within", new BasicDBObject("$center", circle)));

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }

    public void fast() {
        for (int idx=0; idx < 10000; idx++) nearSphereWIthMaxDistance();
    }


    public void nearSphereWIthMaxDistance() {
        final BasicDBObject filter = new BasicDBObject("$nearSphere", new double[] { -73.99171, 40.738868 });
        //filter.put("$maxDistance", 0.002572851730235);
        //filter.put("$maxDistance", 0.0036998565637149016);
        filter.put("$maxDistance", 0.003712240453784);

        // Radius of the earth: 3959.8728
        // Distance to Maplewood, NJ (in radians): 0.0036998565637149016
        // Distance to Maplewood, NJ (in miles 0.0036998565637149016  * 3959.8728): 14.65

        //db.example.find( { loc: { $nearSphere: [ -73.99171, 40.738868 ], $maxDistance: 0.0036998565637149016 }});

        // To get a list of all places (with distance in radians):
        //db.runCommand( { geoNear : "example" , near : [-73.99171,40.738868], spherical: true } );
        //db.runCommand( { geoNear : "example" , near : [-73.99171,40.738868], maxDistance : 0.0036998565637149016, spherical: true } );

        final BasicDBObject query = new BasicDBObject("loc", filter);

        int count = 0;
        for (final DBObject venue : eventCollection.find(query).toArray()) {
            //System.out.println("---- near venue: " + venue.get("name"));
            count++;
        }

    }

    private void addVenue(  final String pName,
                            final double [] pLocation)
    {
        final BasicDBObject loc = new BasicDBObject("name", pName);
        loc.put("loc", pLocation);
        eventCollection.update(new BasicDBObject("name", pName), loc, true, false);
    }


    public ObjectId insertUser(UserVO userToCreate) {

        Log.d("MongoDB", "Trying to Insert User");

        DBCollection userCollection = db.getCollection("users");

        BasicDBObject newUser = userToCreate.bsonFromPojo();

        userCollection.save(newUser);
        return (ObjectId)newUser.get("_id");
    }


    public String storeImage(Bitmap inBitmap, String fileName) {

        //Load our image
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        inBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        byte[] imageBytes = bos.toByteArray();
        //Save image into database
        GridFSInputFile in = fs.createFile( imageBytes );
        in.setFilename(fileName);
        in.save();
        Log.i("MongoDb", "Stored Image and generated Id " + in.getId());
        return in.getId().toString();
    }

//    public Bitmap getImage(String forID) {
//        try {
//            //Find saved image
//            GridFSDBFile out = fs.findOne(forID);
//            //Log.i("MongoDb", "Retrieved Image and with Id " + out.getId());
//            Bitmap imageBitMap = BitmapFactory.decodeStream(out.getInputStream());
//            return imageBitMap;
//        } catch (Exception e) {
//
//        }
//        return null;
//    }


}
