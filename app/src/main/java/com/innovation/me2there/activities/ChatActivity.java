package com.innovation.me2there.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.innovation.me2there.R;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.model.EventDetailVO;


public class ChatActivity extends Mee2ThereActivity {
    EventDetailVO event;

    public EventDetailVO getEvent() {
        return event;
    }

    public void setEvent(EventDetailVO event) {
        this.event = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Integer eventId = getIntent().getIntExtra("eventId",-1);
        Log.i("ChatActivity",""+eventId);
        if(eventId != -1){
            event = DataStore.getEventById(eventId);

        }
        setContentView(R.layout.activity_chat);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
