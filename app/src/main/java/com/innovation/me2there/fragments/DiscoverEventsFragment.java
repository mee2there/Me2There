package com.innovation.me2there.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.innovation.me2there.R;
import com.innovation.me2there.model.EventDetailVO;

import java.util.List;
import java.util.Set;

public class DiscoverEventsFragment extends CardViewFragment  {

    private ProgressBar bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater,container,savedInstanceState);
        if(!cardsLoaded) {
            bar = (ProgressBar) rootView.findViewById(R.id.progressBar);
            setProgressBarVisibile();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public void setProgressBarVisibile(){
        if(bar != null) {
            bar.setVisibility(View.VISIBLE);
        }
    }
    public void setProgressBarInVisibile(){
        if(bar != null) {
            bar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bar = null;

    }

    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
    }
}
