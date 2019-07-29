package com.android.ffbf.model;

import com.android.ffbf.enums.UserType;

import java.io.Serializable;

public class User implements Serializable {

    private String userId;
    private String userName;
    private String userEmail;
    private String userFirstName;
    private String userSurName;
    private String userPassword;
    private UserType userType;

    public User() {
    }

    public User(String userId, String userName, String userEmail, String userFirstName, String userSurName, String userPassword, UserType userType) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userFirstName = userFirstName;
        this.userSurName = userSurName;
        this.userPassword = userPassword;
        this.userType = userType;
    }

    public User(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserSurName() {
        return userSurName;
    }

    public void setUserSurName(String userSurName) {
        this.userSurName = userSurName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
