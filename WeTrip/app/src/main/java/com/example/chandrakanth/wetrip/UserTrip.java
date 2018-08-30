package com.example.chandrakanth.wetrip;

import java.io.Serializable;

/**
 * Created by Chandrakanth on 12/27/2017.
 */

public class UserTrip implements Serializable{
    String tripId,tripName,creatDate,tripPic,tripBy;

    public String getTripBy() {
        return tripBy;
    }

    public void setTripBy(String tripBy) {
        this.tripBy = tripBy;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getCreatDate() {
        return creatDate;
    }

    public void setCreatDate(String creatDate) {
        this.creatDate = creatDate;
    }

    public UserTrip(String tripId, String tripName, String creatDate, String tripPic, String tripBy) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.creatDate = creatDate;
        this.tripPic = tripPic;
        this.tripBy = tripBy;
    }

    public String getTripPic() {

        return tripPic;
    }

    public void setTripPic(String tripPic) {
        this.tripPic = tripPic;
    }

    public UserTrip() {
    }

}
