package com.innovation.me2there.adapters;


/**
 * Created by ashley on 5/12/15.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innovation.me2there.R;
import com.innovation.me2there.activities.EventDetail;
import com.innovation.me2there.animations.AnimationUtils;
import com.innovation.me2there.model.EventDetailVO;
import com.innovation.me2there.util.DateUtil;
import com.innovation.me2there.util.LocationUtil;

import org.apache.commons.collections4.IteratorUtils;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    protected List<EventDetailVO> mEventVOs;
    private int rowLayout;
    private Context mContext;
    private int mPreviousPosition = 0;

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        //mContext = null;
    }

    public CardViewAdapter(Set<EventDetailVO> eventsParm, int rowLayout, Context context) {
        if(eventsParm!= null) {
            this.mEventVOs = IteratorUtils.toList(eventsParm.iterator());

        }
        this.rowLayout = rowLayout;
        this.mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final EventDetailVO event = mEventVOs.get(position);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventDetail.class);
                Bundle b = new Bundle();
                Log.i("Activity Card", "init: " + event.getEventIndex());
                intent.putExtra("eventId", event.getEventIndex()); //Put your id to your next Intent
                mContext.startActivity(intent);
            }
        });

        //AssetManager mngr = this.mContext.getAssets();
        //Typeface font = Typeface.createFromAsset(mngr, "fontello.ttf");
        //viewHolder.mActivityStartTime.setTypeface(font);
        //viewHolder.mActivityStartTime.setText(event.getEventTimeFormat());
        //viewHolder.mActivityEndime.setText(event.getEventTimeFormat());
//        viewHolder.mFontPic3.setTypeface(font);
//        viewHolder.mFontPic3.setText("\uE81c");


        if (viewHolder.mTitle != null) {
            viewHolder.mTitle.setText(event.getEventName());
        }
//        if (viewHolder.mDesc!=null) {
//            viewHolder.mDesc.setText(event.getEventDesc());
//        }
//        if (viewHolder.mSecondaryTitle != null) {
//            viewHolder.mSecondaryTitle.setText(event.getEventDateFormat());
//        }
        if (position > mPreviousPosition) {
            //AnimationUtils.animateSunblind(viewHolder, true);
//            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animate1(holder, true);
//            AnimationUtils.animate(holder,true);
        } else {
       //     AnimationUtils.animateSunblind(viewHolder, false);
//            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animate1(holder, false);
//            AnimationUtils.animate(holder, false);
        }
        mPreviousPosition = position;



        Bitmap eventImage = event.getThumbNailEventImage();
        if (eventImage != null) {

            viewHolder.mImage.setImageBitmap(eventImage);

        }
        viewHolder.eventDate.setText(String.valueOf(DateUtil.getMonth(event.getEventTime().getMonth()))+" "+event.getEventTime().getDate());
        viewHolder.locationText.setText(event.getShortLocText());
        viewHolder.participantsCount.setText(String.valueOf(event.noOfParticipants())+" participants");
        if(event.isUserCreateUser()) {
            viewHolder.yourRsvp.setText(R.string.manage);

            viewHolder.yourRsvp.setTextColor(mContext.getResources().getColor(R.color.light_blue));

        }else if(event.isUserAParticipant()){
            viewHolder.yourRsvp.setText(R.string.yourRsvp);

        }
    }

    @Override
    public int getItemCount() {
        return mEventVOs == null ? 0 : mEventVOs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected CardView layout;
        protected TextView mTitle;
        protected TextView mDesc;
        //protected TextView mActivityStartTime, mActivityEndime, mFontPic3;
        //protected TextView mSecondaryTitle;
        //protected TextView mPhone;
        protected ImageView mImage;

        @Bind(R.id.event_day) TextView eventDate;
        @Bind(R.id.location_text) TextView locationText;
        @Bind(R.id.participants_count) TextView participantsCount;
        @Bind(R.id.comments_count) TextView commentsCount;
        @Bind(R.id.your_rsvp) TextView yourRsvp;

        public ViewHolder(View parent) {
            super(parent);
            //Retrieve elements
            layout = (CardView) parent.findViewById(R.id.card_view);
            ButterKnife.bind(this, layout);

            mTitle = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);
            //mDesc = (TextView) parent.findViewById(R.id.card_main_inner_desc);
            //mSecondaryTitle = (TextView) parent.findViewById(R.id.card_main_inner_secondary_title);
            //mPhone = (TextView) parent.findViewById(R.id.card_main_phone);
            mImage = (ImageView) parent.findViewById(R.id.card_main_inner_image);
            //mActivityStartTime = (TextView) parent.findViewById(R.id.activity_start_time);
            //mActivityEndime = (TextView) parent.findViewById(R.id.activity_end_time);
//            mFontPic3 = (TextView) parent.findViewById(R.id.row_join_pic);

        }

    }


    public void addItem(EventDetailVO item) {
        Log.i("CardViewAdapter","add item?");

        mEventVOs.add(item);
        this.notifyDataSetChanged();
    }
    public void removeItem(EventDetailVO item) {
        mEventVOs.remove(item);
        this.notifyDataSetChanged();
    }
}
