package com.example.chandrakanth.wetrip;

import java.io.Serializable;

/**
 * Created by Chandrakanth on 1/1/2018.
 */

public class UserTripPlaces implements Serializable {
    String placeId;
    String placeRefId;
    String tripId;
    String placeName;


    int position;
    double latitude;
    double longitude;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public UserTripPlaces() {
    }

    public UserTripPlaces(String placeId, String placeRefId, String tripId, String placeName, int position, double latitude, double longitude) {
        this.placeId = placeId;
        this.placeRefId = placeRefId;
        this.tripId = tripId;
        this.placeName = placeName;
        this.position = position;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceRefId() {
        return placeRefId;
    }

    public void setPlaceRefId(String placeRefId) {
        this.placeRefId = placeRefId;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
