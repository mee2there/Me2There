package com.innovation.me2there;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;

/**
 * Created by ashley on 3/11/15.
 */
public class ActivityCard  extends Card {


    protected TextView mTitle;
    protected TextView mDesc;
    protected TextView mFontPic1, mFontPic2, mFontPic3;
    protected TextView mSecondaryTitle;
    protected TextView mPhone;
    protected EventDetailVO mActivity;
    protected ImageView mImage;
    int listImages[] = new int[]{R.drawable.meetup_1, R.drawable.meetup_2,
            R.drawable.meetup_3, R.drawable.meetup_4, R.drawable.meetup_5};
    /**
     * Constructor with a custom inner layout
     * @param context
     */
    public ActivityCard(Context context) {
        this(context, R.layout.row_card);
    }
    /**
     *
     * @param context
     * @param innerLayout
     */
    public ActivityCard(Context context, int innerLayout) {
        super(context, innerLayout);
        init();
    }

    /**
     *
     * @param context
     * @param innerLayout
     */
    public ActivityCard(Context context, int innerLayout,EventDetailVO inActivity) {
        super(context, innerLayout);
        mActivity = inActivity;
        init();

    }

    /**
     * Init
     */
    private void init(){

        //No Header

        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getContext(), EventDetail.class);
                Bundle b = new Bundle();
                Log.i("ActivityCard", "init: " + mActivity.getEventID());
                //b.putInt("eventId", mActivity.getEventID());
                //b.putParcelable("event_detail",mActivity);
                //b.putLong("selectedID", id); //Your id
                intent.putExtra("eventId", mActivity.getEventID()); //Put your id to your next Intent
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //Retrieve elements
        mFontPic1 = (TextView) parent.findViewById(R.id.card_main_font_pic1);
        mFontPic2 = (TextView) parent.findViewById(R.id.card_main_font_pic2);
        //mFontPic3 = (TextView) parent.findViewById(R.id.card_main_font_pic3);
        AssetManager mngr = getContext().getAssets();
        Typeface font = Typeface.createFromAsset(mngr, "fontello.ttf");

        mFontPic1.setTypeface(font);
        mFontPic1.setText("\ue802");
        mFontPic2.setTypeface(font);
        mFontPic2.setText("\ue814");
//        mFontPic3.setTypeface(font);
//        mFontPic3.setText("\ue804");

        mTitle = (TextView) parent.findViewById(R.id.card_main_inner_simple_title);
        //mDesc = (TextView) parent.findViewById(R.id.card_main_inner_desc);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.card_main_inner_secondary_title);
        //mPhone = (TextView) parent.findViewById(R.id.card_main_phone);
        mImage = (ImageView) parent.findViewById(R.id.card_main_inner_image);


        if (mTitle != null) {
            mTitle.setText(mActivity.getEventName());
        }
        if (mDesc != null) {
            mDesc.setText(mActivity.getEventDesc());
        }
        if (mSecondaryTitle != null) {
            mSecondaryTitle.setText(mActivity.getEventTimeSimpleFormat());
        }
        // Create a CardHeader
        //CardHeader headerItem = new CardHeader(getContext());
        // Add Header to card
        //headerItem.setTitle(mActivity.getEventName());

        //this.addCardHeader(headerItem);

        CardThumbnail thumbItem = new CardThumbnail(getContext());
        Bitmap eventImage = mActivity.getThumbNailEventImage();
        if (eventImage != null) {

            mImage.setImageBitmap(eventImage);

        }

    }
}
