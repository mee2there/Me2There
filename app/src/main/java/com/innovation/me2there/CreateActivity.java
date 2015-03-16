package com.innovation.me2there;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 *
 */
public class CreateActivity extends Activity{
        private EditText title, desc, location, phone;
    private Button locationButton, createButton;
    private Context context;
    private LatLng activityLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        context = this.getApplicationContext();
        title = (EditText) findViewById(R.id.edit_title);
        desc = (EditText) findViewById(R.id.edit_desc);
        location = (EditText) findViewById(R.id.edit_location);
        phone = (EditText) findViewById(R.id.editPhone);
        phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast msg = Toast.makeText(getBaseContext(), "Only 10 numbers",
                        Toast.LENGTH_LONG);
                msg.show();
            }
        });



        locationButton = (Button) findViewById(R.id.pick_location);
        locationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = (LatLng)getIntent().getParcelableExtra("currentLocation");
                Intent myIntent = new Intent(context, MapActivity.class);
                myIntent.putExtra("currentLocation",currentLocation);
                //startActivity(myIntent);
                startActivityForResult(myIntent, 1);
            }
        });
        createButton = (Button) findViewById(R.id.create_btn);
        createButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EventDetailVO toCreate = new EventDetailVO(0,title.getText().toString(),desc.getText().toString(),new Date(),activityLocation);
                DataStore.insertActivity(toCreate);
                finish();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                activityLocation = data.getParcelableExtra("pick_location");
                if(activityLocation !=null){
                    locationButton.setVisibility(View.INVISIBLE);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
}