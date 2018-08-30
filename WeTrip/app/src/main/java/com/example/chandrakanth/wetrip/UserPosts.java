package com.example.chandrakanth.wetrip;

import java.io.Serializable;

/**
 * Created by Chandrakanth on 12/21/2017.
 */

public class UserPosts implements Serializable{
    String imgUrl,postBy,imgDesc,imgDate,postId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImgDate() {
        return imgDate;
    }

    public void setImgDate(String imgDate) {
        this.imgDate = imgDate;
    }



    public UserPosts() {
    }

    public String getPostBy() {
        return postBy;
    }

    public void setPostBy(String postBy) {
        this.postBy = postBy;
    }

    @Override
    public String toString() {
        return "UserPosts{" +
                "imgUrl='" + imgUrl + '\'' +
                ", postBy='" + postBy + '\'' +
                ", imgDesc='" + imgDesc + '\'' +
                ", imgDate='" + imgDate + '\'' +
                ", postId='" + postId + '\'' +
                '}';
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgDesc() {
        return imgDesc;
    }

    public void setImgDesc(String imgDesc) {
        this.imgDesc = imgDesc;
    }

    public UserPosts(String imgUrl, String postBy, String imgDesc, String imgDate, String postId) {

        this.imgUrl = imgUrl;
        this.postBy = postBy;
        this.imgDesc = imgDesc;
        this.imgDate = imgDate;
        this.postId = postId;
    }
}
