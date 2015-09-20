package com.stakoun.geoconnect;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Peter on 2015-09-19.
 */
public class Waypoint
{
    private double latitude;
    private double longitude;
    private String title;
    private String address;
    private String info;
    private int radius;
    private Date lastNotif;

    private Date addDay(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    public void setLatitude(double lat) {
        latitude = lat;
    }

    public void setLongitude(double lng) {
        longitude = lng;
    }

    public void updateLastNotif() {
        lastNotif = new Date();
    }

    public boolean canSendNotif() {
        return lastNotif == null || (new Date()).after(addDay(lastNotif));
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public String getInfo() {
        return info;
    }

    public int getRadius() {
        return radius;
    }

    public String toString() {
        return new Gson().toJson(this);
    }

}
