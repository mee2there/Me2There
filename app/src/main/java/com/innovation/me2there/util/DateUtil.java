package com.innovation.me2there.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ashley on 8/23/15.
 */
public class DateUtil {


    public static Date parse( String input )  {
        try {
            //NOTE: SimpleDateFormat uses GMT[-+]hh:mm for the TZ which breaks
            //things a bit.  Before we go on we have to repair this.
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssz");
            //                                          3915-05-06 T 05:00:00.000GMT-00:00

            //this is zero time so we need to add that TZ indicator for
            if (input.endsWith("Z")) {
                input = input.substring(0, input.length() - 1) + "GMT-00:00";
            } else {
                int inset = 6;

                String s0 = input.substring(0, input.length() - inset);
                String s1 = input.substring(input.length() - inset, input.length());

                input = s0 + "GMT" + s1;
            }

            return df.parse(input);
        }catch(ParseException p){
            p.printStackTrace();
        }

        return null;

    }
    public static String getMonth(int month) {
        return new DateFormatSymbols().getShortMonths()[month];
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    public static String getTimeAsText(Date inDate) {
        return getTimeAsText(inDate.getHours(),inDate.getMinutes());
    }
    // Used to convert 24hr format to 12hr format with AM/PM values
    public static String getTimeAsText(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        return aTime;
    }

}
