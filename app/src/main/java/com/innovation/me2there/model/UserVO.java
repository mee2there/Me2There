package com.innovation.me2there.model;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public  class UserVO //implements Parcelable
{
    @Expose
    private String _userFullName;
    @Expose
    private String _password;
    @Expose
    private String _email;
    @Expose
    private String _fbID;
    @Expose
    private LocationVO _location;
    @Expose
    private List<PreferenceVO> _preferences;

    @Expose
    private String _aboutYourself;
    @Expose
    private Integer _distanceWillingToTravel;

    private String _objID;


    private String userToken;

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
    public String get_userFullName() {
        return _userFullName;
    }

    public void set_userFullName(String _userFullName) {
        this._userFullName = _userFullName;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_fbID() {
        return _fbID;
    }

    public void set_fbID(String _fbID) {
        this._fbID = _fbID;
    }

    public LocationVO get_location() {
        return _location;
    }

    public void set_location(LocationVO _location) {
        this._location = _location;
    }

    public List<PreferenceVO> get_preferences() {
        return _preferences;
    }

    public String getAboutYourself() {
        return _aboutYourself;
    }

    public void setAboutYourself(String _aboutYourself) {
        this._aboutYourself = _aboutYourself;
    }

    public Integer getDistanceWillingToTravel() {
        return _distanceWillingToTravel;
    }

    public void setDistanceWillingToTravel(Integer _distanceWillingToTravel) {
        this._distanceWillingToTravel = _distanceWillingToTravel;
    }

    public List<BasicDBObject> get_preferencesDBObjects() {
        List<BasicDBObject> returnObjects = new ArrayList<BasicDBObject>();
        for(PreferenceVO pref :  _preferences){
            returnObjects.add(pref.getDbObject());
        }
        return returnObjects;
    }

    public void set_preferences(List<PreferenceVO> _preferences) {
        this._preferences = _preferences;
    }
    public UserVO(String name, String email){
        this._userFullName = name;
        this._email = email;
    }
    public UserVO(String _userFullName, String _password, String _email, String _fbID, LocationVO _location, List<PreferenceVO> _preferences) {
        this._userFullName = _userFullName;
        this._password = _password;
        this._email = _email;
        this._fbID = _fbID;
        this._location = _location;
        this._preferences = _preferences;
    }
    public UserVO(String id, String _userFullName, String _password, String _email, String _fbID, LocationVO _location, List<PreferenceVO> _preferences) {
        this(_userFullName,_password,_email,_fbID, _location, _preferences);
        this._objID = id;
    }

    public String get_objID() {
        return _objID;
    }

    public void set_objID(String _objID) {
        this._objID = _objID;
    }

    public BasicDBObject bsonFromPojo()
    {
        BasicDBObject document = new BasicDBObject();
        document.put( "_id",    this._objID );
        document.put("email", this._email);
        document.put("fullname", this._userFullName);
        document.put("password", this._password);
        document.put("fbid", this._fbID);
        document.put("preference", this.get_preferencesDBObjects());
        if(this.get_location() != null) {
            document.put("location", this.get_location().bsonFromPojo());
        }

        return document;
    }

    // Convert the POJO to a JSON string
    public String jsonFromPojo(){

        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this, UserVO.class);
    }

    public void addPreference(String prefTitle) {
        Log.i("User add preference", prefTitle);
        if(_preferences == null){
            _preferences = new ArrayList<PreferenceVO>();
        }
        for (PreferenceVO child : _preferences) {
            if (child.get_subType().equals(prefTitle)) {
                return;
            }
        }
        PreferenceVO pref = new PreferenceVO(null, prefTitle);
        _preferences.add(pref);
    }

    public void removePreference(String prefTitle) {
        Log.i("User remove preference", prefTitle);
        if(_preferences == null){
            return;
        }
        PreferenceVO toRemove = null;
        for (PreferenceVO child : _preferences) {
            if (child.get_subType().equals(prefTitle)) {
                toRemove = child;
            }
        }
        if(toRemove != null) {
            _preferences.remove(toRemove);
        }

    }
    public boolean userHasPreference(String pref){
        for (PreferenceVO child : _preferences) {
            if (child.get_subType().equals(pref)) {
                return true;
            }
        }
        return false;
    }
}