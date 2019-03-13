package com.example.my.mamer.config;

public class User {
    private static String userId;
    private static String userName;
    private static String userEmail;
    private static String userIntroduction;
    private static String userPassKey;
    private static String userImg;

    public User() {
    }

    public static String getUserId() {
        return userId;
    }

    public static void setUserId(String userId) {
        User.userId = userId;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        User.userName = userName;
    }

    public static String getUserEmail() {
        return userEmail;
    }

    public static void setUserEmail(String userEmail) {
        User.userEmail = userEmail;
    }

    public static String getUserIntroduction() {
        return userIntroduction;
    }

    public static void setUserIntroduction(String userIntroduction) {
        User.userIntroduction = userIntroduction;
    }

    public static String getUserPassKey() {
        return userPassKey;
    }

    public static void setUserPassKey(String userPassKey) {
        User.userPassKey = userPassKey;
    }

    public static String getUserImg() {
        return userImg;
    }

    public static void setUserImg(String userImg) {
        User.userImg = userImg;
    }
}
