package com.innovation.me2there;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.LatLng;


public class ManageFragment extends Fragment {
    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        activity = (MainActivity) getActivity();

        final Button createButton = (Button) rootView.findViewById(R.id.action_bar_new);

        createButton.setTypeface(activity.font);
        createButton.setText("\ue807");
        final LinearLayout createLayout = (LinearLayout) rootView.findViewById(R.id.create_new_layout);
        createButton.setKeyListener(null);
        createLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = new LatLng(activity.getLatitute(), activity.getLongitude());
                Intent intent = new Intent(activity.getApplicationContext(), CreateActivity.class);
                intent.putExtra("currentLocation", currentLocation);
                startActivity(intent);
            }
        });

        return rootView;
    }

}
