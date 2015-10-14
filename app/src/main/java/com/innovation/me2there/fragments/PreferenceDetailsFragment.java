package com.innovation.me2there.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.innovation.me2there.R;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.UserVO;
import com.innovation.me2there.views.RangeBar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PreferenceDetailsFragment extends Mee2ThereCardFragmentBase {
    UserVO theUser = DataStore.getUser();
    @Bind(R.id.rangebar)RangeBar mRangeBar;
    @Bind(R.id.distance_text)TextView distanceText;
    @Bind(R.id.about_self) EditText aboutText;

    private Integer distanceWillingChanged;
    private String aboutChanged;

    public static PreferenceDetailsFragment newInstance(String param1, Integer param2) {
        Log.i("PreferenceDetailsFrag",param1 +" "+param2);

        PreferenceDetailsFragment fragment = new PreferenceDetailsFragment();
        fragment.distanceWillingChanged = param2;
        fragment.aboutChanged = param1;
        return fragment;
    }

    public String getAboutText(){
        return aboutText.getText().toString();
    }
    public Integer getDistanceWilling(){
        Log.i("get distance willing","distance  "+distanceWillingChanged);

        return distanceWillingChanged;
    }
    public PreferenceDetailsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_preference_details, container, false);
        ButterKnife.bind(this, rootView);
        Log.i("PreferenceDetailsFrag", aboutChanged + " " + distanceWillingChanged);

        if(aboutChanged != null) {
            aboutText.setText(aboutChanged);
        }
        if(distanceWillingChanged != null) {
            distanceText.setText(distanceWillingChanged+" Miles");
            mRangeBar.setRangePinsByValue(5,distanceWillingChanged);
        }
        mRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                Log.i("range Bar","right inx "+rightPinIndex + " right value "+rightPinValue);
                distanceWillingChanged = Integer.parseInt(rightPinValue);
                Log.i("range Bar","distance  "+distanceWillingChanged);

                distanceText.setText(rightPinValue + " Miles");

            }
        });

        return rootView;

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
