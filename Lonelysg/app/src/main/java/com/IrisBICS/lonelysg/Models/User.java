package com.IrisBICS.lonelysg.Models;

import android.net.Uri;

import java.util.ArrayList;

public class User {
    private String username;
    private String gender;
    private String age;
    private String occupation;
    private String interests;
    private String userID;
    private String password;
    private String email;
    private Uri profilePic;
    private ArrayList<Invitation> userInvitations;
    private ArrayList<Request> userRequests;
    private ArrayList<Message> userMessages;

    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) { this.gender = gender; }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public Uri getProfilePic() { return profilePic; }

    public void setProfilePic(Uri profilePic) { this.profilePic = profilePic; }

    public ArrayList<Invitation> getUserInvitations() {
        return userInvitations;
    }

    public void setUserInvitations(ArrayList<Invitation> userInvitations) {
        this.userInvitations = userInvitations;
    }

    public ArrayList<Request> getUserRequests() {
        return userRequests;
    }

    public void setUserRequests(ArrayList<Request> userRequests) {
        this.userRequests = userRequests;
    }

    public ArrayList<Message> getUserMessages() {
        return userMessages;
    }

    public void setUserMessages(ArrayList<Message> userMessages) {
        this.userMessages = userMessages;
    }
}
