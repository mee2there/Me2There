package com.innovation.me2there.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;


/**
 * Created by ashley on 3/23/15.
 */
public class ImageUtil {

    public static Bitmap scaleBitmap(Bitmap photo, int newHeight, float densityMultiplier) {
        Log.i("ImageUtil", "Photo scale " + photo.getWidth() + "," + photo.getHeight());
        Log.i("ImageUtil", "Multiplier " + densityMultiplier);
        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));
        Log.i("ImageUtil", "to scale to " + w + "," + h);
        photo = Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public static Bitmap scaleBitmap(Bitmap photo, int newHeight, int newWidth, float densityMultiplier) {
        //Log.i("ImageUtil", "Photo scale " + newHeight + "," + newWidth);
        //Log.i("ImageUtil", "Multiplier " + densityMultiplier);

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (newWidth * densityMultiplier);
        //Log.i("ImageUtil", "to scale to " + w + "," + h);
        photo = Bitmap.createScaledBitmap(photo, w, h, false);

        return photo;
    }

    public static Bitmap tranformByFactor(Bitmap source, int factor) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / factor;
        int y = (source.getHeight() - size) / factor;
        Bitmap result = Bitmap.createBitmap(source, x, y, size, size);
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    public static Bitmap getGoogleMapThumbnail(double latitude, double longitude){

        String URL = "http://maps.google.com/maps/api/staticmap?center=" +latitude + "," + longitude + "&zoom=15&size=600x300&sensor=false&maptype=roadmap&markers=color:red%7Clabel:S%7C"+latitude+","+longitude;
        Bitmap bmp = null;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet request = new HttpGet(URL);

        InputStream in = null;
        try {
            in = httpclient.execute(request).getEntity().getContent();
            bmp = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bmp;
    }



}
