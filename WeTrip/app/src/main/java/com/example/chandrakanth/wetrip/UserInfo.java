package com.example.chandrakanth.wetrip;

import java.io.Serializable;

/**
 * Created by Chandrakanth on 12/20/2017.
 */

public class UserInfo implements Serializable{
    String fname;
    String lname;
    String gender;
    String email;
    String pass;
    String imageUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String userId;

    @Override
    public String toString() {
        return "UserInfo{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", pass='" + pass + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }

    public UserInfo(String fname, String lname, String gender, String email, String pass, String imageUrl, String userId) {
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        this.email = email;
        this.pass = pass;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public UserInfo() {
    }

       public String getFname() {

        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
