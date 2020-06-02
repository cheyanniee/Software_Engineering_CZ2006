package com.IrisBICS.lonelysg.Models;

import android.net.Uri;

public class Invitation {

    private String category, date, desc, host, startTime, endTime, title, invitationID, latitude, longitude, locationName;
       private Uri invPic;

    public Invitation() {
    }

    public Invitation(String category, String date, String desc, String host, String startTime, String endTime, String title, String invitationID, String latitude, String longitude, String locationName, Uri imageUri) {
        this.category = category;
        this.date = date;
        this.desc = desc;
        this.host = host;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.invitationID = invitationID;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.invPic = imageUri;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(String invitationID) {
        this.invitationID = invitationID;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Uri getInvPic() { return invPic; }

    public void setInvPic(Uri invPic) { this.invPic = invPic; }
}
