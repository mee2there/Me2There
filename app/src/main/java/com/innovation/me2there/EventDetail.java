package com.innovation.me2there;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class EventDetail extends Mee2ThereActivity {

    private Integer eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_event_detail);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("eventId", 0);

        Log.i("EventDetail", "onCreateView : " + eventId);
        Fragment detailFragment = new PlaceholderFragment();

        Bundle newBundle = new Bundle();

        newBundle.putInt("eventId", intValue);
        detailFragment.setArguments(newBundle);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        protected TextView chatPic, mFontPic2, mFontPic3;

        protected TextView mPhone;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            Integer eventId = args.getInt("eventId");
            EventDetailVO event = DataStore.getEventById(eventId);


            View rootView = inflater.inflate(R.layout.fragment_event_detail, container, false);
            TextView nameTextView = ((TextView) rootView.findViewById(R.id.event_name));
            nameTextView.setText(event.getEventName());

            TextView descTextView = ((TextView) rootView.findViewById(R.id.event_desc));
            descTextView.setText(event.getEventDesc());

            TextView dateTextView = ((TextView) rootView.findViewById(R.id.event_time));
            dateTextView.setText(event.getEventTimeSimpleFormat());
            ImageView activityImage = (ImageView) rootView.findViewById(R.id.activity_image);
            Bitmap image = event.getFullEventImage();


            if (image != null) {
                activityImage.setImageBitmap(image);
                //scaleImage(activityImage,image);
            }


            //Retrieve elements
            chatPic = (TextView) rootView.findViewById(R.id.chat_font_pic);
            mFontPic2 = (TextView) rootView.findViewById(R.id.card_main_font_pic2);
            mFontPic3 = (TextView) rootView.findViewById(R.id.card_main_font_pic3);
            mPhone = (TextView) rootView.findViewById(R.id.card_main_phone);

            AssetManager mngr = container.getContext().getAssets();
            Typeface font = Typeface.createFromAsset(mngr, "fontello.ttf");

            chatPic.setTypeface(font);
            chatPic.setText("\ue802");
            mFontPic2.setTypeface(font);
            mFontPic2.setText("\ue814");
            mFontPic3.setTypeface(font);
            mFontPic3.setText("\ue804");

            chatPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
                    startActivity(intent);
                }
            });
            return rootView;
        }
    }
}
