package com.stakoun.geoconnect;

import com.google.gson.Gson;

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

    public Waypoint(double latitude, double longitude, String title, String address, String info)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.address = address;
        this.info = info;
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

    public String toString() {
        return new Gson().toJson(this);
    }

}
