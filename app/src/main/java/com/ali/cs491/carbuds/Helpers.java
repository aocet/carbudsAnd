package com.ali.cs491.carbuds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Helpers {
    public static String timeFormatter(String time) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
        try {
            cal.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String hour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        String minute = String.valueOf(cal.get(Calendar.MINUTE));
        if (minute.length() == 1) {
            minute = "0"+minute;
        }
        String recieveTime = hour+":"+minute;
        return recieveTime;
    }
}
