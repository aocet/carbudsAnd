package com.ali.cs491.carbuds;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

public class Trip {
    private LatLng startPoint;
    private LatLng endPoint;
    private int userType;
    private Calendar date;
    public Trip(LatLng startPoint, LatLng endPoint, int userType, Calendar date){
        this.startPoint = startPoint;
        this.endPoint   = endPoint;
        this.userType   = userType;
        this.date       = date;
    }

    public LatLng getStartPoint() {
        return startPoint;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public int getUserType() {
        return userType;
    }
    public Calendar getDate(){
        return date;
    }
}