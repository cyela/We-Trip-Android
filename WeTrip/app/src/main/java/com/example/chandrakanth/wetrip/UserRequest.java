package com.example.chandrakanth.wetrip;

import java.io.Serializable;

/**
 * Created by Chandrakanth on 12/27/2017.
 */

public class UserRequest implements Serializable {
    String id;
    String status;

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    String reqId;

    @Override
    public String toString() {
        return "userRequest{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", reqId='" + reqId + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserRequest(String id, String status, String reqId) {
        this.id = id;
        this.status = status;
        this.reqId = reqId;
    }

    public UserRequest() {
    }
}

