package com.innovation.me2there.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innovation.me2there.others.ExpListChildItem;
import com.innovation.me2there.others.ExpListGroupItem;
import com.innovation.me2there.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Eclipse wanted me to use a sparse array instead of my hashmaps, I just suppressed that suggestion
@SuppressLint("UseSparseArrays")
public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    // Define activity context
    private Context mContext;

    /*
     * Here we have a Hashmap containing a String key
     * (can be Integer or other type but I was testing
     * with contacts so I used contact name as the key)
    */
    private HashMap<String, List<ExpListChildItem>> mListDataChild;

    // ArrayList that is what each key in the above
    // hashmap points to
    private ArrayList<ExpListGroupItem> mListDataGroup;

    private ArrayList<String> selectedChildren;

    // Hashmap for keeping track of our checkbox check states
    private HashMap<Integer, boolean[]> mChildCheckStates;

    // Our getChildView & getGroupView use the viewholder patter
    // Here are the viewholders defined, the inner classes are
    // at the bottom

    private GroupViewHolder groupViewHolder;

    /*
     *  For the purpose of this document, I'm only using a single
     *  textview in the group (parent) and child, but you're limited only
     *  by your XML view for each group item :)
    */
    private String groupText;
    private String childText;
    private List<String> childTexts;


    /*  Here's the constructor we'll use to pass in our calling
     *  activity's context, group items, and child items
    */
    public MyExpandableListAdapter(Context context,
                                   ArrayList<ExpListGroupItem> listDataGroup, HashMap<String, List<ExpListChildItem>> listDataChild) {

        mContext = context;
        mListDataGroup = listDataGroup;
        mListDataChild = listDataChild;

        // Initialize our hashmap containing our check states here
        mChildCheckStates = new HashMap<Integer, boolean[]>();
        selectedChildren = new ArrayList<String>();
    }

    @Override
    public int getGroupCount() {
        return mListDataGroup.size();
    }

    /*
     * This defaults to "public object getGroup" if you auto import the methods
     * I always make a point to change it from "object" to whatever item
     * I passed through the constructor
    */
    @Override
    public ExpListGroupItem getGroup(int groupPosition) {
        return mListDataGroup.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        //  I passed a text string into an activity holding a getter/setter
        //  which I passed in through "ExpListGroupItems".
        //  Here is where I call the getter to get that text
        groupText = getGroup(groupPosition).getGroupText();

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);

            // Initialize the GroupViewHolder defined at the bottom of this document
            groupViewHolder = new GroupViewHolder();

            groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.lblListHeader);

            convertView.setTag(groupViewHolder);
        } else {

            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(mListDataGroup.get(groupPosition).getMyText()).size();
    }

    /*
     * This defaults to "public object getChild" if you auto import the methods
     * I always make a point to change it from "object" to whatever item
     * I passed through the constructor
    */
    @Override
    public ExpListChildItem getChild(int groupPosition, int childPosition) {
        return mListDataChild.get(mListDataGroup.get(groupPosition).getMyText()).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View currentView = null;
        if(convertView != null) {
            currentView = (View) convertView
                    .getTag(R.layout.list_item + groupPosition);
        }
        Log.i("getChildView", "Positions "+groupPosition+", "+childPosition);
//        final int mGroupPosition = groupPosition;
//        final int mChildPosition = childPosition;

        //  I passed a text string into an activity holding a getter/setter
        //  which I passed in through "ExpListChildItems".
        //  Here is where I call the getter to get that text
       // childText = getChild(mGroupPosition, mChildPosition).getChildText();
        childTexts= getChild(groupPosition, childPosition).getChildTexts();
        ChildViewHolder childViewHolder;

        if (currentView == null) {

            LayoutInflater inflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
            Log.i("getChildView", "Inflated View for "+groupPosition+", "+childPosition);

            childViewHolder = new ChildViewHolder();

//            childViewHolder.mChildText1 = (TextView) convertView
//                    .findViewById(R.id.lblListItem1);
//            childViewHolder.mChildText2 = (TextView) convertView
//                    .findViewById(R.id.lblListItem2);
//            childViewHolder.mCheckBox = (CheckBox) convertView
//                    .findViewById(R.id.checkText);
            childViewHolder.outerLayout = (LinearLayout) convertView
                    .findViewById(R.id.outer_layout);


            LinearLayout innerLayout = childViewHolder.getNewLayout(convertView);

            int count = 0;
            Log.i("getChildView","childTexts: "+childTexts.size());

            for(String child: childTexts){

                // create a new textview
                TextView rowTextView = (TextView) convertView.inflate(convertView.getContext(), R.layout.preference_text, null);
                rowTextView.setText(child);

                rowTextView.setOnTouchListener(new PreferenceTouchListner());

                // add the textview to the linearlayout
                innerLayout.addView(rowTextView);

                count++;
                Log.i("getChildView","count: "+count);
                int remainder = count%2;
                if(remainder == 0){
                    Log.i("getChildView","count: Inside for new layout");

                    innerLayout = childViewHolder.getNewLayout(convertView);
                }


            }

            convertView.setTag(R.layout.list_item+groupPosition, convertView);
        }
        else {

            return currentView;
        }

        return convertView;
    }


    public String getSelectedChildren() {

//to be implemented
        return "";


    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {
        LinearLayout outerLayout;

        //CheckBox mCheckBox;

        public LinearLayout getNewLayout(View convertView){
            LinearLayout newLayout = new LinearLayout(convertView.getContext());
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            this.outerLayout.addView(newLayout);
            return newLayout;
        }
    }

    static final class PreferenceTouchListner implements View.OnTouchListener{
        Boolean toggle = false;

        /**
         * Called when a touch event is dispatched to a view. This allows listeners to
         * get a chance to respond before the target view.
         *
         * @param v     The view the touch event has been dispatched to.
         * @param event The MotionEvent object containing full information about
         *              the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            TextView rowTextView = (TextView) v.findViewById(R.id.pref_text_view);
            final int greyColor = v.getResources().getColor(R.color.grey);
            final int whiteColor = v.getResources().getColor(R.color.white);
            if(!toggle) {
                toggle = true;
                v.setBackgroundResource(R.drawable.pressed_preference);
                rowTextView.setTextColor(whiteColor);
            }else {
                toggle = false;
                v.setBackgroundResource(R.drawable.preference);
                rowTextView.setTextColor(greyColor);
            }
            return false;
        }
    }

}