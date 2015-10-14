package com.innovation.me2there.adapters;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.etsy.android.grid.util.DynamicHeightTextView;
import com.innovation.me2there.R;
import com.innovation.me2there.activities.EventDetail;
import com.innovation.me2there.model.EventDetailVO;

import java.util.ArrayList;
import java.util.Random;

/**
 * ADAPTER
 */

public class SampleAdapter extends ArrayAdapter<EventDetailVO> {

    private static final String TAG = "SampleAdapter";

    static class ViewHolder {
        DynamicHeightTextView txtLineOne;
        Button btnGo;
        ImageView imageCard;
    }

    private final LayoutInflater mLayoutInflater;
    private final Random mRandom;
    private final ArrayList<Integer> mBackgroundColors;

    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();

    public SampleAdapter(final Context context, final int textViewResourceId) {
        super(context, textViewResourceId);
        mLayoutInflater = LayoutInflater.from(context);
        mRandom = new Random();
        mBackgroundColors = new ArrayList<Integer>();
        //mBackgroundColors.add(R.color.orange);
        //mBackgroundColors.add(R.color.green);
        //mBackgroundColors.add(R.color.blue);
        //mBackgroundColors.add(R.color.yellow);
        mBackgroundColors.add(R.color.grey);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_sample, parent, false);
            vh = new ViewHolder();
            vh.txtLineOne = (DynamicHeightTextView) convertView.findViewById(R.id.txt_line1);
            //vh.btnGo = (Button) convertView.findViewById(R.id.btn_go);
            vh.imageCard = (ImageView) convertView.findViewById(R.id.card_main_inner_image);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);
        int backgroundIndex = position >= mBackgroundColors.size() ?
                position % mBackgroundColors.size() : position;

        convertView.setBackgroundResource(mBackgroundColors.get(backgroundIndex));

        Log.d(TAG, "getView position:" + position + " h:" + positionHeight);

        vh.txtLineOne.setHeightRatio(positionHeight);
        vh.txtLineOne.setText(getItem(position).getEventName());
        vh.imageCard.setImageBitmap(getItem(position).getThumbNailEventImage());
        /*vh.btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Toast.makeText(getContext(), "Button Clicked Position " +
                        position, Toast.LENGTH_SHORT).show();
            }
        });*/
        vh.imageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //Log.i("FragmentList", "Item clicked: " + id);
                Intent intent = new Intent(getContext(), EventDetail.class);
                Bundle b = new Bundle();
                b.putParcelable("event_detail", getItem(position));
                //b.putLong("selectedID", id); //Your id
                intent.putExtras(b); //Put your id to your next Intent

                getContext().startActivity(intent);
            }
        });
        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 the width
    }
}