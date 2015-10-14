package com.innovation.me2there.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mongodb.BasicDBObject;

import java.io.Serializable;

/**
 * Created by ashley on 5/31/15.
 */
public class LocationVO implements Serializable{

    private String _address1;
    private String _address2;
    private String _city;
    private String _state;
    private String _postal;
    private String _country;
    @Expose
    private Double _latitude;
    @Expose
    private Double _longitude;

    public String get_address1() {
        return _address1;
    }

    public void set_address1(String _address1) {
        this._address1 = _address1;
    }

    public String get_address2() {
        return _address2;
    }

    public void set_address2(String _address2) {
        this._address2 = _address2;
    }

    public String get_city() {
        return _city;
    }

    public void set_city(String _city) {
        this._city = _city;
    }

    public String get_state() {
        return _state;
    }

    public void set_state(String _state) {
        this._state = _state;
    }

    public String get_postal() {
        return _postal;
    }

    public void set_postal(String _postal) {
        this._postal = _postal;
    }

    public String get_country() {
        return _country;
    }

    public void set_country(String _country) {
        this._country = _country;
    }

    public Double get_latitude() {
        return _latitude;
    }

    public void set_latitude(Double _latitude) {
        this._latitude = _latitude;
    }

    public Double get_longitude() {
        return _longitude;
    }

    public void set_longitude(Double _longitude) {
        this._longitude = _longitude;
    }

    public LocationVO(){

    }

    public LocationVO(String address1, String address2, String city, String state, String postal, String country, Double latitude, Double longitude) {
        _address1 = address1;
        _address2 = address2;
        _city = city;
        _state = state;
        _postal = postal;
        _country = country;
        _latitude = latitude;
        _longitude = longitude;
    }

    @Override
    public String toString() {
        return "LocationVO{" +
                "_address1='" + _address1 + '\'' +
                ", _address2='" + _address2 + '\'' +
                ", _city='" + _city + '\'' +
                ", _state='" + _state + '\'' +
                ", _postal='" + _postal + '\'' +
                ", _country='" + _country + '\'' +
                '}';
    }

    public String displayName() {
        StringBuffer buffer = new StringBuffer();

        if(_address1 != null){
            buffer.append(_address1 + ',');
        }
        if(_address2 != null){
            buffer.append(_address2 + ',');
        }
        if(_city != null){
            buffer.append(_city + ',');
        }
        if(_state != null){
            buffer.append(_state + ',');
        }
        if(_postal != null){
            buffer.append(_postal + ',');
        }
        if(_country != null){
            buffer.append(_country );
        }

        return buffer.toString();
    }
    public String shortDisplayName() {
        return
                _address1 +
                        ", " + _city +
                        ", " + _state +
                        ", " +  _postal +
                        ", " + _country
                ;
    }
    public String cityAndState() {
        return
                      _city +
                        ", " + _state +
                        ", " + _country
                ;
    }
    public BasicDBObject bsonFromPojo(){
        BasicDBObject location = new BasicDBObject();
        location.put("address1", _address1);
        location.put("address2", _address2);
        location.put("city", _city);
        location.put("state", _state);
        location.put("postal", _postal);
        location.put("country", _country);
        location.put("latitude", _latitude);
        location.put("longitude", _longitude);
        return location;

    }
    public void pojoFromBson( BasicDBObject bson )
    {
        BasicDBObject b = ( BasicDBObject ) bson;

        this._address1        = (String) b.get( "address1" );
        this._address2        = (String) b.get( "address2" );
        this._city        = (String) b.get( "city" );
        this._state        = (String) b.get( "state" );
        this._postal        = (String) b.get( "postal" );
        this._country        = (String) b.get( "country" );
        this._latitude        = (Double) b.get( "latitude" );
        this._longitude        = (Double) b.get( "longitude" );
    }

    // Convert the POJO to a JSON string
    public String jsonFromPojo(){

        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this, PreferenceVO.class);
    }

}
