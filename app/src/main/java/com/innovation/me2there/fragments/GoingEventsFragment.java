package com.innovation.me2there.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.innovation.me2there.R;
import com.innovation.me2there.activities.MainActivity;
import com.innovation.me2there.adapters.CardViewAdapter;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.EventDetailVO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GoingEventsFragment extends CardViewFragment {

    @Override
    public void fillCards() {
        Set<EventDetailVO> events = new HashSet<>();
        Set<EventDetailVO> allEvents = DataStore.getEvents();
        if(allEvents != null) {
            for (EventDetailVO anEvent : allEvents) {
                if (anEvent.isUserAParticipant()) {
                    events.add(anEvent);
                }
            }
        }
        mAdapter = new CardViewAdapter(events, R.layout.row_card, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        cardsLoaded = true;
   }

}
