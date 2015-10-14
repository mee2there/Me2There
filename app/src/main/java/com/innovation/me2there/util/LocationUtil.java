package com.innovation.me2there.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.innovation.me2there.model.LocationVO;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Vijilesh on 5/16/2015.
 */
public class LocationUtil {

    public static String getAddressFromLocation(final double latitude, final double longitude,
                                                final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                sb.append(address.getLocality()).append("\n");
                sb.append(address.getPostalCode()).append("\n");
                sb.append(address.getCountryName());
                result = sb.toString();

            }
        } catch (IOException e) {
            Log.e("LocationUtil", "Unable connect to Geocoder");
        } finally {


        }
        return result;
    }
    public static String getShortAddressFromLocation(final double latitude, final double longitude,
                                                final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
//                sb.append(address.getLocality()).append("\n");
                //sb.append(address.getPostalCode()).append("\n");
                //sb.append(address.getCountryName());
                result = sb.toString();

            }
        } catch (IOException e) {
            Log.e("LocationUtil", "Unable connect to Geocoder");
        } finally {


        }
        return result;
    }
    public static String getVeryShortAddressFromLocation(final double latitude, final double longitude,
                                                     final Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();



                for (int i = 1; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append("\n");
                }
                result = StringUtil.parseCSV(sb.toString())[0];


            }
        } catch (IOException e) {
            Log.e("LocationUtil", "Unable connect to Geocoder");
        } finally {


        }
        return result;
    }

    public static String getCityNameFromLocation(final double latitude, final double longitude,
                                                 final Context context) {
        Log.d("LocationUtil","getCityNameFromLocation");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            Log.d("LocationUtil","getCityNameFromLocation return size"+addressList.size());
            if (addressList != null && addressList.size() > 0) {

                Address address = addressList.get(0);
                Log.d("LocationUtil","getCityNameFromLocation address"+address);

                LocationVO location = new LocationVO(address.getAddressLine(0),
                                                     null,
                                                     address.getAddressLine(1),
                                                     address.getAdminArea(),
                                                     address.getPostalCode(),
                                                     address.getCountryName(),
                                                     address.getLatitude(),
                                                     address.getLongitude()
                                                    );

                StringBuilder sb = new StringBuilder();
                sb.append(address.getLocality()).append(",");
                sb.append(address.getCountryName());
                result = sb.toString();

            }
        } catch (IOException e) {
            Log.e("LocationUtil", "Unable connect to Geocoder");
        } finally {


        }
        return result;
    }

    public static LocationVO getLocation(final double latitude, final double longitude,
                                                 final Context context) {
        Log.d("LocationUtil","getCityNameFromLocation");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        LocationVO location = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            Log.d("LocationUtil","getCityNameFromLocation return size"+addressList.size());
            if (addressList != null && addressList.size() > 0) {

                Address address = addressList.get(0);
                Log.d("LocationUtil","getCityNameFromLocation address"+address);

                location = new LocationVO(address.getAddressLine(0),
                        null,
                        address.getAddressLine(1).split(",")[0],
                        address.getAdminArea(),
                        address.getPostalCode(),
                        address.getCountryName(),
                        address.getLatitude(),
                        address.getLongitude()
                );

;

            }
        } catch (IOException e) {
            Log.e("LocationUtil", "Unable connect to Geocoder");
        } finally {


        }
        return location;
    }
}
