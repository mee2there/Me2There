package com.innovation.me2there.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.innovation.me2there.R;
import com.innovation.me2there.adapters.CardViewAdapter;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.fragments.Mee2ThereCardFragmentBase;
import com.innovation.me2there.model.EventDetailVO;
import com.innovation.me2there.model.UserVO;
import com.innovation.me2there.others.Mee2ThereApplication;
import com.innovation.me2there.util.DateUtil;
import com.innovation.me2there.util.ImageUtil;
import com.innovation.me2there.util.LocationUtil;
import com.innovation.me2there.views.GradientOverImageDrawable;
import com.innovation.me2there.views.morphingbutton.MorphingButton;


import org.apache.commons.collections4.IteratorUtils;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;


public class EventDetail extends Mee2ThereActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_event_detail);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("eventId", 0);

        Log.i("EventDetail", "onCreateView : " + intValue);
        Mee2ThereCardFragmentBase detailFragment = new EventDetailFragment();

        Bundle newBundle = new Bundle();

        newBundle.putInt("eventId", intValue);
        detailFragment.setArguments(newBundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailFragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        Log.i("EventDetail", "onDestroy : ");

        super.onDestroy();
        unbindDrawables(findViewById(R.id.container));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    /**
     * Call this when your activity is done and should be closed.  The
     * ActivityResult is propagated back to whoever launched you via
     * onActivityResult().
     */
    @Override
    public void finish() {
        Log.i("EventDetail", "finish : ");

        super.finish();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("EventDetail", "onKeyDown : ");

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class EventDetailFragment extends Mee2ThereCardFragmentBase implements SwipeRefreshLayout.OnRefreshListener {
        EventDetailVO event;

        @Bind(R.id.media_panel)
        ScrollView mediaPanel;
        @Bind(R.id.activity_image)
        ImageView activityImage;
        @Bind(R.id.media_panel_group)
        LinearLayout eventDetailLayout;

        @Bind(R.id.horizontal_user_view)
        RecyclerView userHorizontalView;

        @Bind(R.id.map_poster)
        ImageView staticMap;
        @Bind(R.id.event_name)
        TextView nameTextView;
        @Bind(R.id.event_desc)
        TextView descTextView;
        @Bind(R.id.swipe_refresh_layout)
        SwipeRefreshLayout swipeRefreshLayout;
        @Bind(R.id.location_text)
        TextView locationText;
        @Bind(R.id.goto_chat)
        ImageView chatButton;
        @Bind(R.id.event_year)
        TextView eventYear;
        @Bind(R.id.event_month)
        TextView eventMonth;
        @Bind(R.id.event_date)
        TextView eventDate;
        @Bind(R.id.start_time)
        TextView startTime;
        @Bind(R.id.end_time)
        TextView endTime;

        @Bind(R.id.no_of_participants)
        TextView noOfParticipants;

        @Bind(R.id.rsvp_morph_button)
        MorphingButton rsvpMorphButton;
        @Bind(R.id.share_morph_button)
        MorphingButton shareMorphButon;
        List<UserViewHolder> participantViews;

        private int mMorphButonCounter = 1;
        private Boolean hasUserJoined;

        public EventDetailFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            Bundle args = getArguments();
            final Integer eventId = args.getInt("eventId");
            event = DataStore.getEventById(eventId);

            Log.d("EventDetail", "event : " + event.toString());

            View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
            ButterKnife.bind(this, rootView);
            swipeRefreshLayout.setOnRefreshListener(this);

            nameTextView.setText(event.getEventName());
            descTextView.setText(event.getEventDesc());

            eventDate.setText(String.valueOf(event.getEventTime().getDate()));
            eventMonth.setText(String.valueOf(DateUtil.getMonth(event.getEventTime().getMonth())));
            eventYear.setText(String.valueOf(event.getEventTime().getYear()));
            startTime.setText(String.valueOf(DateUtil.getTimeAsText(event.getEventTime())));

            if (event.getEventEndTime() != null) {
                endTime.setText(String.valueOf(DateUtil.getTimeAsText(event.getEventEndTime())));

            } else {
                endTime.setVisibility(View.INVISIBLE);
            }
            noOfParticipants.setText(String.valueOf(event.noOfParticipants()) );
            Bitmap image = event.getThumbNailEventImage();


            if (image != null) {
                //activityImage.setImageBitmap(image);


                //Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.house);
                int gradientStartColor = Color.argb(0, 0, 0, 0);
                int gradientEndColor = Color.argb(255, 0, 0, 0);
                GradientOverImageDrawable gradientDrawable = new GradientOverImageDrawable(getResources(), image);
                gradientDrawable.setGradientColors(gradientStartColor, gradientEndColor);

                activityImage.setImageDrawable(gradientDrawable);


            }
//            mediaPanel.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    Intent intent = new Intent(getActivity(), FullscreenImageActivity.class);
////                    Log.i("Activity Card", "init: " + event.getEventIndex());
////                    intent.putExtra("eventId", event.getEventIndex()); //Put your id to your next Intent
//                    startActivity(intent);
//                    return false;
//                }
//
//            });
            hasUserJoined = event.isUserAParticipant();

            rsvpMorphButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toggleRsvp(rsvpMorphButton);
                }
            });

            initGoingButton(rsvpMorphButton);
            MorphingButton.Params square = MorphingButton.Params.create()
                    .cornerRadius(dimen(R.dimen.mb_corner_radius_2))
                    //.width(dimen(R.dimen.mb_width_120))

                    //.height(dimen(R.dimen.mb_height_56))
                    //.color(color(R.color.mb_blue))
                    .colorPressed(color(R.color.mb_blue_dark))
                    .icon(R.drawable.ic_share_white_24dp);
                    //.text(getString(R.string.share));
            shareMorphButon.morph(square);

            shareMorphButon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareOnClick();
                }
            });
            AssetManager mngr = container.getContext().getAssets();
            Typeface font = Typeface.createFromAsset(mngr, "fontello.ttf");

            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    intent.putExtra("eventId", eventId);
                    startActivity(intent);
                }
            });

            // Setup dim the fanart when scroll changes. Full dim on 4 * iconSize dp
            Resources resources = getActivity().getResources();
            final int pixelsToTransparent = 4 * resources.getDimensionPixelSize(R.dimen.default_icon_size);
            mediaPanel.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    float y = mediaPanel.getScrollY();
                    float newAlpha = Math.min(1, Math.max(0, 1 - (y / pixelsToTransparent)));
                    activityImage.setAlpha(newAlpha);
                }
            });
            if(event.get_latitude() != null && event.get_longitude() != null) {
                locationText.setText(LocationUtil.getVeryShortAddressFromLocation(event.get_latitude(), event.get_longitude(), this.getActivity()));
            }

            //userHorizontalView = (RecyclerView) rootView.findViewById(R.id.custom_list);

            userHorizontalView.setLayoutManager(new LinearLayoutManager(getActivity()));
            userHorizontalView.setItemAnimator(new DefaultItemAnimator());

            final LinearLayoutManager mLayoutManager;
            mLayoutManager =
                    //        new LinearLayoutManager(getActivity());
                    new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

            userHorizontalView.setLayoutManager(mLayoutManager);

            getStaticMap(event.get_latitude(), event.get_longitude());
            getEventParticipants(event);
            getEventImage(event);

            return rootView;
        }

        public class HorizontalViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
            List<String> mUserViews ;
            private int rowLayout;


            public HorizontalViewAdapter(List<String> userViews,int inRowLayout) {
                if(userViews!= null) {
                    this.mUserViews = userViews;

                }
                this.rowLayout = inRowLayout;


            }
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
                return new RecyclerView.ViewHolder(v) {
                    @Override
                    public String toString() {
                        return super.toString();
                    }
                };
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

                TextView userNameView = (TextView)holder.itemView.findViewById(R.id.person_name);
                userNameView.setText(mUserViews.get(position));
                ImageView personPhoto = (ImageView) holder.itemView.findViewById(R.id.person_photo);
                personPhoto.setImageBitmap(event.getThumbNailEventImage());


            }

            @Override
            public int getItemCount() {
                return mUserViews.size();
            }
        }
            private void toggleRsvp(final MorphingButton btnMorph) {
            if (hasUserJoined) {
                hasUserJoined = false;
                morphToSquare(btnMorph, integer(R.integer.mb_animation));
                event.removeFromParticipant(DataStore.getUser().get_objID().toString());
                DataStore.withdrawRSVP(event, DataStore.getUser());


            } else {
                hasUserJoined = true;
                morphToSuccess(btnMorph);
                event.addToParticipant(DataStore.getUser().get_objID().toString());
                DataStore.rsvpEvent(event, DataStore.getUser());
            }
        }

        private void initGoingButton(final MorphingButton btnMorph) {
            Log.i("initGoingButton", "hasUserJoined" + hasUserJoined);
            if (hasUserJoined) {
                morphToSuccess(btnMorph);
            } else {
                morphToSquare(btnMorph, integer(R.integer.mb_animation));
            }
        }

        private void morphToSquare(final MorphingButton btnMorph, int duration) {
            MorphingButton.Params square = MorphingButton.Params.create()
                    .duration(duration)
                    .cornerRadius(dimen(R.dimen.mb_height_26))
                    .width(dimen(R.dimen.mb_width_80))
                    .height(dimen(R.dimen.mb_height_26))
                    .color(color(R.color.mb_red))
                    .colorPressed(color(R.color.mb_red_dark))
                    .text(getString(R.string.rsvp));
            btnMorph.morph(square);
        }

        private void morphToSuccess(final MorphingButton btnMorph) {
            MorphingButton.Params circle = MorphingButton.Params.create()
                    .duration(integer(R.integer.mb_animation))
                    .cornerRadius(dimen(R.dimen.mb_height_26))
                    .width(dimen(R.dimen.mb_width_80))
                    .height(dimen(R.dimen.mb_height_26))
                    .color(color(R.color.mb_green))
                    .colorPressed(color(R.color.mb_green_dark))
                    .icon(R.drawable.ic_done);
                    //.text("Going");
            btnMorph.morph(circle);
        }

        private void shareOnClick() {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = event.shareBody(getActivity());
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, event.getEventName());
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            ButterKnife.unbind(this);
            //rsvp = null;
            mediaPanel = null;
            eventDetailLayout = null;
            staticMap = null;
            swipeRefreshLayout = null;
            chatButton = null;
            //joined = null;
            activityImage = null;
            participantViews = null;
            System.gc();
            //RefWatcher refWatcher = Mee2ThereApplication.getRefWatcher(getActivity());
            //refWatcher.watch(this);

        }

        public void onRefresh() {

        }

        public void getStaticMap(double latitude, double longitude) {
            new StaticMapTask().execute(latitude, longitude);

        }

        public void getEventImage(EventDetailVO eventToGet) {
            Log.d("EventDetail", "getEventImage : " + eventToGet.toString());
            Mee2ThereApplication app = (Mee2ThereApplication)getActivity().getApplication();
            Bitmap bitmap = app.getBitmapFromMemCache(event.getImageID());
            if(bitmap != null){
                Log.i("EventDetail","getEventImage cached image found for "+event.getImageID());

                activityImage.setImageBitmap(bitmap);
            }else {
                Log.i("EventDetail","getEventImage no cached image found for "+event.getImageID());

                new ImageFetchTask().execute(eventToGet);
            }

        }

        public void getEventParticipants(EventDetailVO eventToGet) {
            Log.d("EventDetail", "getEventParticipants : " + eventToGet.toString());

            new ParticipantsFetchTask(this.getActivity()).execute(eventToGet);

        }

        private class StaticMapTask extends AsyncTask<Double, String, Bitmap> {

            @Override
            protected void onPostExecute(Bitmap bmp) {
                Log.d("EventDetail", "StaticMapTask : post execute ");
                if (staticMap != null) {
                    staticMap.setImageBitmap(bmp);
                }
            }

            @Override
            protected Bitmap doInBackground(Double... params) {
                Log.d("EventDetail", "StaticMapTask : do in backgroud ");

                Bitmap bm = ImageUtil.getGoogleMapThumbnail(params[0], params[1]);
                return bm;

            }

        }


        private class ImageFetchTask extends AsyncTask<EventDetailVO, Void, Bitmap> {
            EventDetailVO theEvent;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                Log.d("EventDetail", "ImageFetchTask : post execute ");

                super.onPostExecute(bitmap);
                if (activityImage != null) {
                    Mee2ThereApplication app = (Mee2ThereApplication)getActivity().getApplication();
                    app.addBitmapToMemoryCache(event.getImageID(),bitmap);
                    Log.i("EventDetail", "ImageFetchTask added image to cache for " + event.getImageID());

                    activityImage.setImageBitmap(bitmap);
                }

            }

            @Override
            protected Bitmap doInBackground(EventDetailVO... events) {
                Log.d("EventDetail", "ImageFetchTask : do in backgroud ");

                theEvent = events[0];
                return DataStore.getImage(theEvent.getEventName(), theEvent.getImageID());

            }

        }


        private class ParticipantsFetchTask extends AsyncTask<EventDetailVO, Void, Set<UserVO>> {
            EventDetailVO theEvent;

            Context activityContext;

            public ParticipantsFetchTask(Context theContext) {
                activityContext = theContext;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Set<UserVO> participants) {
                Log.d("EventDetail", "ParticipantsFetchTask : on post execute ");

                super.onPostExecute(participants);
/*
                participantViews = new ArrayList<UserViewHolder>();
                if (event.get_participantsObjIds() != null) {
                    for (String user : event.get_participantsObjIds()) {
                        View v = LayoutInflater.from(getActivity()).inflate(R.layout.participant_card, container, false);
                        UserViewHolder pvh = new UserViewHolder(v);
                        participantViews.add(pvh);

                        //userHorizontalView.addView(v);


                    }

                }*/

                // Create a LinearLayout element
                // LinearLayout ll = new LinearLayout(activityContext);
                //ll.setOrientation(LinearLayout.VERTICAL);
                int indx = 0;
                List<String> userNames = new ArrayList<String>();
                for (UserVO user : participants) {
                    // Add text
                    //if (indx < participantViews.size()) {
                        userNames.add(user.get_userFullName());
                    //    participantViews.get(indx).getPersonName().setText(user.get_userFullName());
                    //    indx++;
                   // }

                }
                HorizontalViewAdapter mAdapter = new HorizontalViewAdapter(userNames, R.layout.participant_card);
                if(userHorizontalView!=null) {
                    userHorizontalView.setAdapter(mAdapter);
                }

                //  mediaPanel.removeView(eventDetailLayout);
                // Display the view
                // getActivity().setContentView(eventDetailLayout);

                // Add the LinearLayout element to the ScrollView
                //mediaPanel.addView(ll);

                // Display the view
                //setContentView(v);

//                userCardAdapter = new ParticipantCardAdapter(participants, R.layout.participant_card, activityContext);
//                userListView.setAdapter(userCardAdapter);

            }

            @Override
            protected Set<UserVO> doInBackground(EventDetailVO... events) {
                Log.d("EventDetail", "ParticipantsFetchTask : do in backgroud ");

                theEvent = events[0];
                return DataStore.getParticipants(theEvent, activityContext);

            }

        }


    }

    public static class UserViewHolder {
        //CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;

        UserViewHolder(View itemView) {
            //super(itemView);
            //cv = (CardView) itemView.findViewById(R.id.cv);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            personAge = (TextView) itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView) itemView.findViewById(R.id.person_photo);
        }

        public TextView getPersonName() {
            return this.personName;
        }
    }


}
