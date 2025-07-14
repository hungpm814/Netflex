package com.example.netflex.model;

import java.util.Date;
import java.util.List;

public class User {
    private String id;
    private String userName;
    private String email;
    private String phoneNumber;

    // Constructors
    public User() {}

    public User(String id, String userName, String email, String phoneNumber) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Validation methods
    public boolean isValidEmail() {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPhoneNumber() {
        return phoneNumber == null || phoneNumber.isEmpty() || 
               android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public boolean isValidUserName() {
        return userName != null && !userName.trim().isEmpty() && userName.length() <= 100;
    }
}
