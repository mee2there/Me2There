package com.innovation.me2there;

/**
 * Created by ashley on 1/28/15.
 */

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public  class EventDetailVO implements Parcelable {
    static String dateFormat = "MM/dd/yy hh:mm a";
    private int _eventID;
    private String _eventName;
    private String _eventDesc;
    private LatLng _eventLoc;
    private Date _eventTime;
    private String _imageID;
    private Bitmap _eventThumbNailBitmap;
    private Bitmap _eventDetailBitmap;
    private Bitmap _eventFullBitmap;
    public int getEventID() {
        return _eventID;
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

    public String getEventTimeSimpleFormat() {

        return DateFormat.format(dateFormat, _eventTime).toString();

    }

    public LatLng getEventLoc() {
        return _eventLoc;
    }

    public Object getEventImageID() {
        return _imageID;
    }

    public double[] getEventLocAsArray() {
        return new double[]{_eventLoc.latitude, _eventLoc.longitude};
    }

    public Bitmap getThumbNailEventImage() {
        Log.i("EventDetail", "get thumbNail image " + _imageID);
        if (_eventThumbNailBitmap == null) {
            if (getFullEventImage() != null) {
                _eventThumbNailBitmap = ImageUtil.scaleBitmap(getFullEventImage(), 80, 80, MainActivity.densityMultiplier);
            }
        }
        return _eventThumbNailBitmap;
    }

    public Bitmap getEventDetailImage() {
        Log.i("EventDetail", "get Detail image " + _imageID);
        if (_eventDetailBitmap == null) {
            if (getFullEventImage() != null) {
                _eventDetailBitmap = ImageUtil.scaleBitmap(getFullEventImage(), 400, 400, MainActivity.densityMultiplier);
            }
        }
        return _eventDetailBitmap;
    }

    public Bitmap getFullEventImage() {
        Log.i("EventDetail", "get full image " + _imageID);
        if (_eventFullBitmap == null) {
            _eventFullBitmap = DataStore.downloadImage(_imageID);
        }
        return _eventFullBitmap;
    }

    public EventDetailVO(String name,int id) {
        _eventID = id;
        _eventName = name;
        _eventDesc = name;
        _eventTime = new Date();

    }

    public EventDetailVO(int id, String name,String desc,Date eventDate) {
        _eventID = id;
        _eventName = name;
        _eventDesc = desc;
        _eventTime = eventDate;

    }
    public EventDetailVO(int id, String name,String desc,Date eventDate,LatLng eventLocation) {
        _eventID = id;
        _eventName = name;
        _eventDesc = desc;
        _eventTime = eventDate;
        _eventLoc = eventLocation;
    }

    public EventDetailVO(int id, String name, String desc, Date eventDate, LatLng eventLocation, String imageID) {
        _eventID = id;
        _eventName = name;
        _eventDesc = desc;
        _eventTime = eventDate;
        _eventLoc = eventLocation;
        _imageID = imageID;
    }

    // Parcelling part
    public EventDetailVO(Parcel in){
        String[] data = new String[2];
        double[] locCoordinates = new double[2];

        this._eventID = in.readInt();
        this._imageID = in.readString();
        in.readStringArray(data);
        this._eventName = data[0];
        this._eventDesc = data[1];
        this._eventTime = new Date(in.readLong());
        //this._eventFullBitmap = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
//        in.readDoubleArray(locCoordinates);

  //      this._eventLoc = new LatLng(locCoordinates[0],locCoordinates[1]);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._eventID);
        parcel.writeString(this._imageID);
        parcel.writeStringArray(new String[] {this._eventName,
                this._eventDesc});
        parcel.writeLong(this._eventTime.getTime());
        if(this._eventLoc != null) {
            parcel.writeDoubleArray(new double[]{this._eventLoc.latitude, this._eventLoc.longitude});
        }
        //parcel.writeValue(_eventFullBitmap);


    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EventDetailVO createFromParcel(Parcel in) {
            return new EventDetailVO(in);
        }

        public EventDetailVO[] newArray(int size) {
            return new EventDetailVO[size];
        }
    };
}