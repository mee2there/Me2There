package com.innovation.me2there.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innovation.me2there.R;
import com.innovation.me2there.activities.MainActivity;
import com.innovation.me2there.adapters.CardViewAdapter;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.EventDetailVO;

import java.util.List;
import java.util.Set;

/**
 * Created by ashley on 9/10/15.
 */
public class CardViewFragment  extends Mee2ThereCardFragmentBase{
    protected boolean cardsLoaded = false;
    protected RecyclerView mRecyclerView;
    protected CardViewAdapter mAdapter;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        mRecyclerView = null;
        mAdapter = null;
        //activity = null;


    }

    public void fillCards() {
        Log.i("CardViewFragment", "Fill cards Loaded? " + cardsLoaded);
        //if(! cardsLoaded) {
        mAdapter = new CardViewAdapter(DataStore.getEvents(), R.layout.row_card, getActivity());
        if(mRecyclerView!=null) {
            mRecyclerView.setAdapter(mAdapter);
        }
        cardsLoaded = true;
        loading = true;
         //}//else{
        //addNewEvents(_newEvents);
        // }
    }

    protected void addNewEvents(List<EventDetailVO> _newEvents){
        Log.i("Mee2ThereFragmentBase","add Events?");

        for(EventDetailVO event:_newEvents){
            mAdapter.addItem(event);
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_discover, container, false);
        //activity = (MainActivity) getActivity();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.custom_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final LinearLayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                //Log.v("...", "visibleItemCount" + visibleItemCount + ", " + totalItemCount + ", " + pastVisiblesItems);

                if (loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        Log.v("Mee2ThereCardFragment", "Fetching More !!!");

                        ((MainActivity)getActivity()).fetchMoreEvents();
                        //loading = true;
                    }
                }
            }
        });

        return rootView;

    }


    /**
     * Called when the fragment is visible to the user and actively running.
     * This is generally
     * Activity's lifecycle.
     */
    @Override
    public void onResume() {
        super.onResume();
         fillCards();
    }

}
