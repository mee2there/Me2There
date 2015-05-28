package com.innovation.me2there;

import android.graphics.Bitmap;
import android.util.Log;


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
        Log.i("ImageUtil", "Photo scale " + newHeight + "," + newWidth);
        Log.i("ImageUtil", "Multiplier " + densityMultiplier);

        int h = (int) (newHeight * densityMultiplier);
        int w = (int) (newWidth * densityMultiplier);
        Log.i("ImageUtil", "to scale to " + w + "," + h);
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


}
