package com.innovation.me2there.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;
import com.innovation.me2there.R;
import com.innovation.me2there.db.DataStore;
import com.innovation.me2there.fragments.DatePickerFragment;
import com.innovation.me2there.fragments.TimePickerFragment;
import com.innovation.me2there.model.EventDetailVO;
import com.innovation.me2there.util.DateUtil;
import com.innovation.me2there.util.ImageFilePath;
import com.innovation.me2there.util.ImageUtil;
import com.innovation.me2there.util.LocationUtil;
import com.innovation.me2there.views.morphingbutton.MorphingButton;
import com.innovation.me2there.views.morphingbutton.impl.IndeterminateProgressButton;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 */
public class CreateActivity extends Mee2ThereActivity {
    private EditText title, desc;//, phone;
    @Bind(R.id.date_picker)   EditText dateButton;
    @Bind(R.id.time_picker)   EditText fromTimeButton;
    @Bind(R.id.end_time_picker)   EditText toTimeButton;

    private TextView locationButton, pickImage, locationText;
    @Bind(R.id.create_btn) IndeterminateProgressButton createButton;
    private LatLng activityLocation;
    @Bind(R.id.pick_photo_icons) LinearLayout imagePicker;
    @Bind(R.id.pick_location_icons) LinearLayout locationPicker;
    @Bind(R.id.activity_title) TextView activityTitle;

    private boolean locationViewAdded = false;
    private boolean imageViewAdded = false;

    static final int DATE_DIALOG_ID = 999;
    private Date eventFromDate,eventToDate;
    private static final int SELECT_PICTURE = 0;
    private static final int SELECT_LOCATION = 1;
    private Bitmap bitmap, thumbnailImage;
    @Bind(R.id.actImage) ImageView imageView;
    @Bind(R.id.actMap) ImageView mapView;
    @Bind(R.id.app_bar_button) TextView appBarButton;

    String titleString, descriptionString;
    private Uri uri;
    private String uriString;
    @Bind(R.id.type_spinner)Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);
        activityTitle.setText(R.string.title_activity_create);
        title = (EditText) findViewById(R.id.edit_title);
        desc = (EditText) findViewById(R.id.edit_desc);
        appBarButton.setVisibility(View.INVISIBLE);
        locationText = (TextView) findViewById(R.id.pick_location_text);
        validationText = (TextView) findViewById(R.id.validaitonText);
        validationText.setVisibility(View.INVISIBLE);

        //locationButton = (TextView) findViewById(R.id.pick_location);
        OnClickListener locationListner = new OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng currentLocation = (LatLng)getIntent().getParcelableExtra("currentLocation");
                Intent myIntent = new Intent(getApplicationContext(), MapActivity.class);
                myIntent.putExtra("currentLocation",currentLocation);
                //startActivity(myIntent);
                startActivityForResult(myIntent, SELECT_LOCATION);
            }
        };
        eventFromDate = new Date();
        eventToDate = new Date();

        OnClickListener dateListner = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                DialogFragment newFragment = new DatePickerFragment() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        eventFromDate.setDate(day);
                        eventFromDate.setMonth(month);
                        eventFromDate.setYear(year);

                        eventToDate.setDate(day);
                        eventToDate.setMonth(month);
                        eventToDate.setYear(year);

                        //eventFromDate = new Date(year,month,day);
                        dateButton.setText(new StringBuilder().append(day).append("/")
                                .append(month).append("/").append(year));
                    }
                };
                newFragment.show(getSupportFragmentManager(), "datePicker");

            }

        };
        dateButton.setOnClickListener(dateListner);
        OnClickListener timeListner = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText timeText = (EditText)v;
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        eventFromDate.setHours(hour);
                        eventFromDate.setMinutes(minute);
                        timeText.setText(DateUtil.getTimeAsText(hour, minute));
                    }
                };
                newFragment.show(getSupportFragmentManager(), "timePicker");

            }

        };
        fromTimeButton.setOnClickListener(timeListner);

        toTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText timeText = (EditText)v;
                if (v != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                DialogFragment newFragment = new TimePickerFragment() {
                    @Override
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        eventToDate.setHours(hour);
                        eventToDate.setMinutes(minute);
                        timeText.setText(DateUtil.getTimeAsText(hour, minute));
                    }
                };
                newFragment.show(getSupportFragmentManager(), "timePicker");

            }

        });

        //Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.event_type_array, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        locationText.setOnClickListener(locationListner);
        mapView.setOnClickListener(locationListner);

//
//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onMorphButton1Clicked(btnMorph1);
//            }
//        });

        morphToSuccess(createButton);
        createButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateCreate()) {
                    if (uriString != null) {
                    //    String imageId = DataStore.uploadImage(uriString, title.getText().toString());
                    }
                    simulateProgress(createButton);
                    EventDetailVO toCreate = new EventDetailVO(DataStore.getNextIndex(), title.getText().toString(), desc.getText().toString(), eventFromDate,eventToDate, activityLocation, uriString, spinner.toString());
                    DataStore.insertEvent(toCreate);
                    if(thumbnailImage != null) {
                        toCreate.setEventThumbNailBitmap(thumbnailImage);
                    }


                    finish();
                }
            }
        });
        removeView(imageView);
        removeView(mapView);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                uri = data.getData();
                bitmap = null;
                Log.i("Create Activity ", "Image URI "+ uri.toString());
                        //getRealPathFromURI(this, uri);
                uriString = ImageFilePath.getPath(this, uri);
//                uri.getPath();
                Log.i("Create Activity ", "Image URI String "+ uriString);

                //Bitmap bitmap = getPath(data.getData());
                try {
                    bitmap = getBitmapFromUri(data.getData());
                } catch (IOException i) {
                    Log.i("Create Activity", "Could not get bitmap from uri for image: " + i.getMessage());
                    i.printStackTrace();
                } catch (Exception e) {
                    Log.i("Create Activity", "Could not get bitmap from uri for image: " + e.getMessage());
                    e.printStackTrace();
                }


                Log.i("Create Activity", "Uri: " + uri.toString());
                if(!imageViewAdded) {
                    //imagePicker.setVisibility(View.INVISIBLE);
                    addView(imagePicker, imageView);
                    //removeView(imagePicker);
                    imageViewAdded = true;
                }
                thumbnailImage = ImageUtil.scaleBitmap(bitmap, 100, 100, MainActivity.densityMultiplier);
                imageView.setImageBitmap(thumbnailImage);
            }
        }
        if (requestCode == SELECT_LOCATION) {
            if(resultCode == RESULT_OK){
                activityLocation = data.getParcelableExtra("pick_location");
                if(activityLocation !=null){

                    if(!locationViewAdded) {
                        //locationPicker.setVisibility(View.INVISIBLE);
                        addView(locationPicker, mapView);
                        locationViewAdded = true;
                        //removeView(locationPicker);
                    }
                    locationText.setText(LocationUtil.getShortAddressFromLocation(activityLocation.latitude, activityLocation.longitude, this));

                    new StaticMapTask().execute(activityLocation.latitude, activityLocation.longitude);




//                    LocationVO locationDetails = LocationUtil.getLocation(activityLocation.latitude, activityLocation.longitude, this);
//                    locationText.setText(locationDetails.displayName());
//                    removeView(locationButton);
                }
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    public void pickPhoto(View view) {
        //TODO: launch the photo picker
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);

    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void uploadPhoto(View view) {
        try {
            //executeMultipartPost();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private Boolean validateCreate(){
        Boolean isValid = true;
        titleString = title.getText().toString();

        if(validationText.getVisibility() == View.INVISIBLE){
            validationText.setVisibility(View.VISIBLE);
        }

        if(titleString.length() == 0 ) {
            validationText.setText("Please enter an Title ");
            isValid = false;
        }
        if(activityLocation == null){
            validationText.setText("Need a location for the event ");
            isValid = false;

        }
        if(dateButton.getText() == null){
            validationText.setText("Need a date for the event ");
            isValid = false;

        }
        return isValid;
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private class StaticMapTask extends AsyncTask<Double, String, Bitmap> {

        @Override
        protected void onPostExecute(Bitmap bmp){
            Bitmap thumbnailImage = ImageUtil.scaleBitmap(bmp, 200, 200, MainActivity.densityMultiplier);
            mapView.setImageBitmap(thumbnailImage);
        }

        @Override
        protected Bitmap doInBackground(Double... params) {
            // TODO Auto-generated method stub
            Bitmap bm = ImageUtil.getGoogleMapThumbnail(params[0],params[1]);
            return bm;

        }

    }

    private void morphToSuccess(final IndeterminateProgressButton btnMorph) {
        MorphingButton.Params circle = MorphingButton.Params.create()
                //.duration(integer(R.integer.mb_animation))
                //.cornerRadius(dimen(R.dimen.mb_height_56))
                //.width(dimen(R.dimen.mb_height_56))
                .height(dimen(R.dimen.mb_height_26))
                .color(color(R.color.mb_red))
                .colorPressed(color(R.color.mb_red_dark));
                //.icon(R.drawable.ic_done);
        btnMorph.morph(circle);
    }

    private void simulateProgress(@NonNull final IndeterminateProgressButton button) {
        int progressColor1 = color(R.color.holo_blue_bright);
        int progressColor2 = color(R.color.holo_green_light);
        int progressColor3 = color(R.color.holo_orange_light);
        int progressColor4 = color(R.color.holo_red_light);
        int color = color(R.color.mb_gray);
        int progressCornerRadius = dimen(R.dimen.mb_corner_radius_4);
        int width = dimen(R.dimen.mb_width_200);
        int height = dimen(R.dimen.mb_height_8);
        int duration = integer(R.integer.mb_animation);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                morphToSuccess(button);
                button.unblockTouch();
            }
        }, 4000);

        button.blockTouch(); // prevent user from clicking while button is in progress
        button.morphToProgress(color, progressCornerRadius, width, height, duration, progressColor1, progressColor2,
                progressColor3, progressColor4);
    }


}