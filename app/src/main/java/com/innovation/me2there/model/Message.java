package com.innovation.me2there.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message> {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_LOG = 1;
    public static final int TYPE_ACTION = 2;

    private int mType;
    private String mMessage;
    private String mUsername;
    private String mUserObjectId;
    private String mEventID;
    private Date mTime;

    public Date getmTime() {
        return mTime;
    }

    public void setmTime(Date mTime) {
        this.mTime = mTime;
    }

    @Override
    public int compareTo(Message o) {
        return mTime.compareTo(o.mTime);
    }


    public String getmUserObjectId() {
        return mUserObjectId;
    }

    public void setmUserObjectId(String mUserObjectId) {
        this.mUserObjectId = mUserObjectId;
    }

    public Message() {
    }

    public int getType() {
        return mType;
    }

    ;

    public String getMessage() {
        return mMessage;
    }

    ;

    public String getUsername() {
        return mUsername;
    }

    ;


    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private String mUserObjectId;
        private Date mTime;


        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }
        public Builder time(Date time) {
            mTime = time;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder userid(String userid) {
            mUserObjectId = userid;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
            message.mUserObjectId = mUserObjectId;
            message.mTime = mTime;

            return message;
        }
    }


    public void pojoFromJson( JSONObject json ) throws JSONException
    {
        this.mEventID = json.getString("eventId");
        this.mUserObjectId = json.getString("userId");
        this.mUsername = json.getString("username");

        this.mMessage = json.getString("message");

        String dateStr = json.getString("time");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        try {
            this.mTime = sdf.parse(dateStr);
        }catch (ParseException p){
            Log.i("Message","pojoFromJson: "+p.getMessage());
            return;
        }


    }
}
