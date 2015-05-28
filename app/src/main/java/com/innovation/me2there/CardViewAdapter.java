package com.innovation.me2there;


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

import java.util.List;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    protected List<EventDetailVO> mEventVOs;
    private int rowLayout;
    private Context mContext;

    public CardViewAdapter(List<EventDetailVO> eventsParm, int rowLayout, Context context) {
        this.mEventVOs = eventsParm;
        this.rowLayout = rowLayout;
        this.mContext = context;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final EventDetailVO event = mEventVOs.get(i);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EventDetail.class);
                Bundle b = new Bundle();
                Log.i("ActivityCard", "init: " + event.getEventID());
                intent.putExtra("eventId", event.getEventID()); //Put your id to your next Intent
                mContext.startActivity(intent);
            }
        });

        AssetManager mngr = this.mContext.getAssets();
        Typeface font = Typeface.createFromAsset(mngr, "fontello.ttf");
        viewHolder.mFontPic1.setTypeface(font);
        viewHolder.mFontPic1.setText("\ue802");
        viewHolder.mFontPic2.setTypeface(font);
        viewHolder.mFontPic2.setText("\ue814");
//        viewHolder.mFontPic3.setTypeface(font);
//        viewHolder.mFontPic3.setText("\ue804");


        if (viewHolder.mTitle != null) {
            viewHolder.mTitle.setText(event.getEventName());
        }
//        if (viewHolder.mDesc!=null) {
//            viewHolder.mDesc.setText(event.getEventDesc());
//        }
        if (viewHolder.mSecondaryTitle != null) {
            viewHolder.mSecondaryTitle.setText(event.getEventTimeSimpleFormat());
        }

        Bitmap eventImage = event.getThumbNailEventImage();
        if (eventImage != null) {

            viewHolder.mImage.setImageBitmap(eventImage);

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
        protected TextView mFontPic1, mFontPic2, mFontPic3;
        protected TextView mSecondaryTitle;
        //protected TextView mPhone;
        protected ImageView mImage;


        public ViewHolder(View parent) {
            super(parent);
            //Retrieve elements
            layout = (CardView) parent.findViewById(R.id.card_view);
            mTitle = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);
            //mDesc = (TextView) parent.findViewById(R.id.card_main_inner_desc);
            mSecondaryTitle = (TextView) parent.findViewById(R.id.card_main_inner_secondary_title);
            //mPhone = (TextView) parent.findViewById(R.id.card_main_phone);
            mImage = (ImageView) parent.findViewById(R.id.card_main_inner_image);
            mFontPic1 = (TextView) parent.findViewById(R.id.card_main_font_pic1);
            mFontPic2 = (TextView) parent.findViewById(R.id.card_main_font_pic2);
            //mFontPic3 = (TextView) parent.findViewById(R.id.card_main_font_pic3);

        }

    }
}
