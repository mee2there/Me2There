package com.innovation.me2there.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * Created by ashley on 5/31/15.
 */
public class PreferenceVO  implements Serializable {

    @Expose

    private String _type;
    @Expose
    private String _subType;

    public PreferenceVO(){

    }
    public PreferenceVO(String _type, String _subType) {
        this._type = _type;
        this._subType = _subType;
    }

    public String get_type() {
        return _type;
    }

    public String get_subType() {
        return _subType;
    }
    public void set_type(String _type) {
        this._type = _type;
    }

    public void set_subType(String _subType) {
        this._subType = _subType;
    }

    public BasicDBObject getDbObject(){
        BasicDBObject location = new BasicDBObject();
        location.put("type", _type);
        location.put("subtype", _subType);
        return location;

    }

    public BasicDBObject bsonFromPojo()
    {
        BasicDBObject document = new BasicDBObject();
        document.put("type", this._type);
        document.put("subtype", this._subType);

        return document;
    }

    public void pojoFromBson( BasicDBObject bson )
    {
        BasicDBObject b =  bson;

        this._type        = (String) b.get( "type" );
        this._subType        = ( String )  b.get( "subtype" );
    }


    // Convert the POJO to a JSON string
    public String jsonFromPojo(){

        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this, PreferenceVO.class);
    }


}
