package com.innovation.me2there;

/**
 * Created by ashley on 1/28/15.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

public  class EventDetailVO implements Parcelable {

    private int _eventID;
    private String _eventName;
    private String _eventDesc;
    private LatLng _eventLoc;

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

    public LatLng getEventLoc() {
        return _eventLoc;
    }
    public double[] getEventLocAsArray() {
        return new double[]{_eventLoc.latitude, _eventLoc.longitude};
    }

    private Date _eventTime;

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

    }      // Parcelling part
    public EventDetailVO(Parcel in){
        String[] data = new String[2];
        double[] locCoordinates = new double[2];

        this._eventID = in.readInt();
        in.readStringArray(data);
        this._eventName = data[0];
        this._eventDesc = data[1];
        this._eventTime = new Date(in.readLong());
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
        parcel.writeStringArray(new String[] {this._eventName,
                this._eventDesc});
        parcel.writeLong(this._eventTime.getTime());
        if(this._eventLoc != null) {
            parcel.writeDoubleArray(new double[]{this._eventLoc.latitude, this._eventLoc.longitude});
        }


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