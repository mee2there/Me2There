package com.innovation.me2there.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.innovation.me2there.layouts.FlowLayout;
import com.innovation.me2there.others.ExpListChildItem;
import com.innovation.me2there.others.ExpListGroupItem;
import com.innovation.me2there.adapters.MyExpandableListAdapter;
import com.innovation.me2there.R;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.PreferenceItem;
import com.innovation.me2there.model.PreferenceVO;
import com.innovation.me2there.model.UserVO;
import com.innovation.me2there.views.RangeBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class PreferenceTopicsFragment extends Mee2ThereCardFragmentBase {
    ArrayList<ExpListGroupItem> expListGroupItems;
    HashMap<String, List<ExpListChildItem>> expListChildItems;
    MyExpandableListAdapter listAdapter;
    private ArrayList<Filter_Object> mArrFilter;
    private Filter_Adapter mFilter_Adapter ;

    @Bind(R.id.scroll_view) ScrollView mScrollViewFilter;
    @Bind(R.id.listViewFilter) ListView mListView;

    @Bind(R.id.flowLayout) FlowLayout mFlowLayoutFilter ;

    public final static String EXTRA_MESSAGE = "com.innovation.me2there.MESSAGE";
    public final static String FB_ID = "com.innovation.me2there.FBID";

    List<PreferenceItem> childTexts;
    public PreferenceTopicsFragment()  {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        childTexts = DataStore.getUserPreferences();
        setRetainInstance(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final LayoutInflater mInflater = inflater;
        final ViewGroup mContainer = container;
        View rootView = inflater.inflate(R.layout.fragment_preference_topics, container, false);
        ButterKnife.bind(this, rootView);
//        appBarButton.setText("Next");
//        appBarTitle.setText("Preferences");
        // get the listview
        //expListView = (ExpandableListView) rootView.findViewById(R.id.lvExpCategory);

        // preparing list data

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;


        mArrFilter = new ArrayList<>();
        for(PreferenceItem child: childTexts) {

            Filter_Object filter_object = new Filter_Object();
            filter_object.mName = child.getPreference();
            filter_object.mIsSelected = DataStore.getUser().userHasPreference(child.getPreference());
            mArrFilter.add(filter_object);
        }

        mFilter_Adapter = new Filter_Adapter(mArrFilter);
        mListView.setAdapter(mFilter_Adapter);
        return rootView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    public final class ChildViewHolder {
        LinearLayout outerLayout;
        int innerWidth;
        int occupiedWidth = 0;
        //CheckBox mCheckBox;

        public LinearLayout getNewLayout(View convertView){
            LinearLayout newLayout = new LinearLayout(convertView.getContext());
            newLayout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.gravity = Gravity.CENTER;

            newLayout.setLayoutParams(params);
            newLayout.setPadding(7, 7, 7, 7);

            this.outerLayout.addView(newLayout);
            return newLayout;
        }
    }

    static final class PreferenceTouchListner implements View.OnTouchListener{
        PreferenceItem _prefItem;
        PreferenceTouchListner(PreferenceItem item){
            super();
            _prefItem = item;
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextView rowTextView = (TextView) v.findViewById(R.id.pref_text_view);
            final int greyColor = v.getResources().getColor(R.color.grey);
            final int whiteColor = v.getResources().getColor(R.color.white);
            if(!_prefItem.getIsSelected()) {
                _prefItem.setSelected(true);
                v.setBackgroundResource(R.drawable.pressed_preference);
                rowTextView.setTextColor(whiteColor);
            }else {
                _prefItem.setSelected(false);
                v.setBackgroundResource(R.drawable.preference);
                rowTextView.setTextColor(greyColor);
            }
            return false;
        }
    }
    public void addFilterTag() {
        UserVO user = DataStore.getUser();

        final ArrayList<Filter_Object> arrFilterSelected = new ArrayList<>();

        mFlowLayoutFilter.removeAllViews();

        int length = mArrFilter.size();
        boolean isSelected = false;
        for (int i = 0; i < length; i++) {
            Filter_Object fil = mArrFilter.get(i);
            if (fil.mIsSelected) {
                user.addPreference(fil.mName);
                isSelected = true;
                arrFilterSelected.add(fil);
            }else{
                user.removePreference(fil.mName);

            }
        }
        if (isSelected) {
            mScrollViewFilter.setVisibility(View.VISIBLE);
        } else {
            mScrollViewFilter.setVisibility(View.GONE);
        }
        int size = arrFilterSelected.size();
        LayoutInflater layoutInflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < size; i++) {
            View view = layoutInflater.inflate(R.layout.filter_tag_edit, null);

            TextView tv = (TextView) view.findViewById(R.id.tvTag);
            LinearLayout linClose = (LinearLayout) view.findViewById(R.id.linClose);
            final Filter_Object filter_object = arrFilterSelected.get(i);
            linClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showToast(filter_object.name);


                    int innerSize = mArrFilter.size();
                    for (int j = 0; j < innerSize; j++) {
                        Filter_Object mFilter_Object = mArrFilter.get(j);
                        if (mFilter_Object.mName.equalsIgnoreCase(filter_object.mName)) {
                            mFilter_Object.mIsSelected = false;

                        }
                    }
                    addFilterTag();
                    mFilter_Adapter.updateListView(mArrFilter);
                }
            });


            tv.setText(filter_object.mName);
            int color = getResources().getColor(R.color.themecolor);

            View newView = view;
            newView.setBackgroundColor(color);

            FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 10;
            params.topMargin = 5;
            params.leftMargin = 10;
            params.bottomMargin = 5;

            newView.setLayoutParams(params);

            mFlowLayoutFilter.addView(newView);
        }
    }

    public class Filter_Object {
        public String mName ;
        public boolean mIsSelected ;
    }

    public class Filter_Adapter extends BaseAdapter {
        ArrayList<Filter_Object> arrMenu;

        public Filter_Adapter(ArrayList<Filter_Object> arrOptions) {
            this.arrMenu = arrOptions;
        }

        public void updateListView(ArrayList<Filter_Object> mArray) {
            this.arrMenu = mArray;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return this.arrMenu.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.preference_list, null);
                viewHolder = new ViewHolder();
                viewHolder.mTtvName = (TextView) convertView.findViewById(R.id.tvName);
                viewHolder.mTvSelected = (TextView) convertView.findViewById(R.id.tvSelected);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final Filter_Object mService_Object = arrMenu.get(position);
            viewHolder.mTtvName.setText(mService_Object.mName);

            if (mService_Object.mIsSelected) {
                viewHolder.mTvSelected.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mTvSelected.setVisibility(View.INVISIBLE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mService_Object.mIsSelected = !mService_Object.mIsSelected;
                    mScrollViewFilter.setVisibility(View.VISIBLE);

                    addFilterTag();
                    notifyDataSetChanged();
                }
            });
//            addFilterTag();

            return convertView;
        }

        public class ViewHolder {
            TextView mTtvName, mTvSelected;

        }
    }


}
