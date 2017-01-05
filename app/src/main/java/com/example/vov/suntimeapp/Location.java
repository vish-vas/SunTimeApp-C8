package com.example.vov.suntimeapp;

/**
 * Created by Vov on 11/10/16.
 */

public class Location {
    private double lat;
    private double lng;
    private String timeZone;

    public Location(double lat, double lng, String timeZone)
    {
        this.lat = lat;
        this.lng = lng;
        this.timeZone = timeZone;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}