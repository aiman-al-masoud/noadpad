package com.luxlunaris.noadpadlight.services;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServices {


    /**
     * Converts the unix time in milliseconds
     * to a datetime string fit for locale settings.
     * @param unixTimeMillis
     * @return
     */
    public static String unixTimeToString(long unixTimeMillis, Context context){

        Date date = new Date(unixTimeMillis);

        java.text.DateFormat format = DateFormat.getDateFormat(context);
        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(context);
        SimpleDateFormat dayFormat = new SimpleDateFormat("E");

        String dayString = dayFormat.format(date);
        String dateString = format.format(date);
        String timeString = timeFormat.format(date);

        String overallDateTimeString = dayString+" "+dateString+" "+timeString;

        return overallDateTimeString;
    }




}
