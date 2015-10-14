package com.innovation.me2there.model;

/**
 * Created by ashley on 1/28/15.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.util.DateUtil;
import com.innovation.me2there.util.ImageUtil;
import com.innovation.me2there.activities.MainActivity;
import com.innovation.me2there.util.LocationUtil;
import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDetailVO implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventDetailVO createFromParcel(Parcel in) {
            return new EventDetailVO(in);
        }

        public EventDetailVO[] newArray(int size) {
            return new EventDetailVO[size];
        }
    };

    static String dateFormat = "yyyy-MM-dd";
    static String timeFormat = "hh:mm a";
    private String _objID;
    private int _eventIndx;
    @Expose
    private String _eventName;
    @Expose
    private String _eventDesc;
    private LatLng _eventLoc;
    @Expose
    private Double _latitude;
    @Expose
    private Double _longitude;
    @Expose
    private Date _eventTime;
    @Expose
    private Date _eventEndTime;
    @Expose
    private String _createUser;
    @Expose
    private String _eventType;

    private String _shortLocText;
    private String _longLocText;

    private String _imageID;

    public void set_imageTumbNailID(String _imageTumbNailID) {
        this._imageTumbNailID = _imageTumbNailID;
    }

    public void set_imageID(String _imageID) {
        this._imageID = _imageID;
    }

    private String _imageTumbNailID;
    private String _imagePath;

    public void setEventThumbNailBitmap(Bitmap _eventThumbNailBitmap) {
        this._eventThumbNailBitmap = _eventThumbNailBitmap;
    }

    private Bitmap _eventThumbNailBitmap;
    private Bitmap _eventDetailBitmap;
    private Bitmap _eventFullBitmap;
    //DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(Mee2ThereApplication.getAppContext());
    private SimpleDateFormat _mFormatter = new SimpleDateFormat(dateFormat, Locale.US);
    private List<String> _participantsObjIds;
    private List<UserVO> _participants;

    public EventDetailVO() {
    }

    public EventDetailVO(int indx, String name, String desc, Date eventDate,Date eventEndDate, LatLng eventLocation, String imagePath,String eventType) {
        this();
        _eventIndx = indx;
        _eventName = name;
        _eventDesc = desc;
        _eventTime = eventDate;
        _eventEndTime = eventEndDate;
        _eventLoc = eventLocation;
        _imagePath = imagePath;
        _eventType = eventType;
        if (_eventLoc != null) {
            _latitude = _eventLoc.latitude;
            _longitude = _eventLoc.longitude;
        }

    }

    public EventDetailVO(int indx, String id, String name, String desc, Date eventDate,Date eventEndDate, LatLng eventLocation, String imageID,String eventType) {
        this(indx, name, desc, eventDate,eventEndDate, eventLocation, imageID,eventType);
        _objID = id;
    }

    // Parcelling part
    public EventDetailVO(Parcel in) {
        String[] data = new String[2];
        double[] locCoordinates = new double[2];

        this._eventIndx = in.readInt();
        this._imageID = in.readString();
        in.readStringArray(data);
        this._eventName = data[0];
        this._eventDesc = data[1];
        this._eventTime = new Date(in.readLong());
        //this._eventFullBitmap = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
//        in.readDoubleArray(locCoordinates);

        //      this._eventLoc = new LatLng(locCoordinates[0],locCoordinates[1]);
    }


    public String get_createUser() {

        return _createUser;
    }

    public void setCreateUser(String _createUser) {
        this._createUser = _createUser;
    }

    public List<String> get_participantsObjIds() {
        return _participantsObjIds;
    }

    public Integer noOfParticipants() {
        if(_participantsObjIds == null){
            return 0;
        }
        return _participantsObjIds.size();
    }

    public void set_participantsObjIds(List<String> _participantsObjIds) {
        this._participantsObjIds = _participantsObjIds;
    }

    public List<UserVO> get_participants() {
        return _participants;
    }

    public void set_participants(List<UserVO> _participants) {
        this._participants = _participants;
    }

    public int getEventIndex() {
        return _eventIndx;
    }

    public void setEventIndx(int _eventIndx) {
        this._eventIndx = _eventIndx;
    }

    public String getEventName() {
        return _eventName;
    }

    public String getEventDesc() {
        return _eventDesc;
    }

    public Date getEventTime() {
        return _eventTime;
    }

    public Date getEventEndTime() {
        return _eventEndTime;
    }

    public String getImageID() {
        return _imageID;
    }

    public String getImageTumbNailID() {
        return _imageTumbNailID;
    }

    public String getShortLocText() {
        return _shortLocText;
    }

    public String getLongLocText() {
        return _longLocText;
    }

    public void setShortLocText(String _shortLocText) {
        this._shortLocText = _shortLocText;
    }

    public void setLongLocText(String _longLocText) {
        this._longLocText = _longLocText;
    }

    public String getEventDateFormat() {
//        SimpleDateFormat sdfAmerica = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a");
//        sdfAmerica.setTimeZone(TimeZone.getTimeZone("America/New_York"));
//        String sDateInAmerica = sdfAmerica.format(_eventTime);
//        return sDateInAmerica;
        //return _mFormatter.format(_eventTime);

        return android.text.format.DateFormat.format(dateFormat, _eventTime).toString();

    }

    public String getEventTimeFormat() {

        return android.text.format.DateFormat.format(timeFormat, _eventTime).toString();
        //return _eventTime.toString();
    }

    public void removeFromParticipant(String userToRemove) {
        _participantsObjIds.remove(userToRemove);

    }

    public void addToParticipant(String userToAdd) {
        Log.i("addToParticipant", userToAdd);
        _participantsObjIds.add(userToAdd);


    }

    public LatLng getEventLoc() {
        return _eventLoc;
    }

    public Object getEventImageID() {
        return _imageID;
    }

    public String getImagePath() {
        return _imagePath;
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

    public double[] getEventLocAsArray() {
        if (_eventLoc != null) {
            return new double[]{_eventLoc.latitude, _eventLoc.longitude};
        } else {
            return new double[]{0, 0};

        }
    }

    public Bitmap getThumbNailEventImage() {
//        if (_eventThumbNailBitmap == null) {
//            downloadThumbNailImage();
//        }
        return _eventThumbNailBitmap;
    }

    public Bitmap getEventDetailImage() {
        if (_eventDetailBitmap == null) {
            if (getFullEventImage() != null) {
                _eventDetailBitmap = ImageUtil.scaleBitmap(getFullEventImage(), 400, 400, MainActivity.densityMultiplier);
            }
        }
        return _eventDetailBitmap;
    }

    public Bitmap getFullEventImage() {
        return _eventFullBitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._eventIndx);
        parcel.writeString(this._imageID);
        parcel.writeStringArray(new String[]{this._eventName,
                this._eventDesc});
        parcel.writeLong(this._eventTime.getTime());
        if (this._eventLoc != null) {
            parcel.writeDoubleArray(new double[]{this._eventLoc.latitude, this._eventLoc.longitude});
        }
        //parcel.writeValue(_eventFullBitmap);


    }

    public String get_objID() {
        return _objID;
    }

    public void set_objID(String _objID) {
        this._objID = _objID;
    }


    public boolean isUserAParticipant() {
        if(this.get_participantsObjIds() == null){
            return false;
        }
        return this.get_participantsObjIds().contains(DataStore.getUser().get_objID().toString());
    }
    public boolean isUserCreateUser() {
        if(this._createUser == null){
            return false;
        }
        return this._createUser.equals(DataStore.getUser().get_objID().toString());
    }
    public BasicDBObject bsonFromPojo() {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

        BasicDBObject document = new BasicDBObject();
        document.put("_id", this._objID);
        document.put("name", this._eventName);
        document.put("description", this._eventDesc);
        document.put("date", this._eventTime);
        document.put("created", new Date());

        document.put("imageId", this._imageID);
        document.put("loc", this.getEventLocAsArray());

        return document;

    }

    public void pojoFromJson(JsonObject b) {
        Log.i("EventDetailVO", "pojoFromJson" + b.toString());
        this._objID = b.get("_id").getAsString();
        this._eventName = b.get("name").getAsString();
        this._eventDesc = b.get("description").getAsString();
        if (b.get("date") != null) {
            this._eventTime = DateUtil.parse(b.get("date").getAsString());
        }
        if (b.get("imageId") != null) {
            this._imageID = b.get("imageId").getAsString();
        }
        if (b.get("thumbnailId") != null) {
            this._imageTumbNailID = b.get("thumbnailId").getAsString();
        }
        if (b.get("createUser") != null) {
            this._createUser = b.get("createUser").getAsString();
        }
        if (b.get("endDate") != null) {
            this._eventEndTime = DateUtil.parse(b.get("endDate").getAsString());
        }
        if (b.get("eventType") != null) {
            this._eventType = b.get("eventType").getAsString();
        }
        this._latitude = b.get("loc").getAsJsonArray().get(0).getAsDouble();
        this._longitude = b.get("loc").getAsJsonArray().get(1).getAsDouble();
        this._participantsObjIds = new ArrayList<String>();

        JsonArray participantJsons = b.get("participants").getAsJsonArray();
        for (int i = 0; i < participantJsons.size(); i++) {
            addToParticipant(participantJsons.get(i).getAsString());
        }

        Log.i("EventDetailVO", "Participants" + _participantsObjIds);

        Log.i("EventDetailVO", "pojoFromJson" + _objID);

        downloadThumbNailImage();
        //_imageDownloader.execute();

    }

    // Convert the POJO to a JSON string
    public String jsonFromPojo() {
        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this, EventDetailVO.class);
    }

    private void downloadImage() {
        Log.i("DownloadImageTask", " Downloading image for " + _eventName + "Image ID" + _imageID);
        _eventFullBitmap = DataStore.getImage(_eventName, _imageID);
        Log.i("DownloadImageTask", " After Downloading image for " + _eventFullBitmap);


    }

    private void downloadThumbNailImage() {
        Log.i("DownloadImageTask", " Downloading image for " + _eventName + "Image ID" + _imageTumbNailID);
        _eventThumbNailBitmap = DataStore.getImage(_eventName, _imageTumbNailID);
        if (_eventThumbNailBitmap != null) {
            _eventThumbNailBitmap = ImageUtil.scaleBitmap(_eventThumbNailBitmap, 100, 100, MainActivity.densityMultiplier);
        }

        Log.i("DownloadImageTask", " After Downloading image for " + _eventThumbNailBitmap);


    }

    @Override
    public String toString() {
        return "EventDetailVO{" +
                "_eventName='" + _eventName + '\'' +
                ", _latitude=" + _latitude +
                ", _longitude=" + _longitude +
                ", _eventTime=" + _eventTime +
                ", _imageID='" + _imageID + '\'' +
                ", _eventIndx=" + _eventIndx +
                '}';
    }
    public String shareBody(Context inContext) {
        return
                "Event : " + _eventName +"\n" +
                "Date :" + _eventTime +  "\n" +
                "Location : " + LocationUtil.getAddressFromLocation(_latitude,_longitude,inContext) +"\n" +
                "No Of Participants : " + noOfParticipants() ;
    }

    @Override
    public boolean equals(Object o) {
        return _objID.equals(((EventDetailVO) o)._objID);
    }
    @Override
    public int hashCode()
    {
        if(_objID != null) {
            return _objID.hashCode();
        }else{
            return toString().hashCode();
        }
    }

}