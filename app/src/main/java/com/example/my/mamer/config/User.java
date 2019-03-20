package com.example.my.mamer.config;

import android.graphics.Bitmap;

public class User {
    private static String userId;
    private static String userName;
    private static String userEmail;
    private static String userIntroduction;
    private static String userPassKey;
//    默认头像
    private static String userImg;
//    头像id
    private static String userImgId;
//    修改后的头像网址
    private static String userImgAvatar;
//    修改后的头像Bitmap头像
    private static Bitmap userImgBitmap;
    private static String userPassKey_type;
    private static Boolean boundPhone;
    private static String userBornDate;
    private static Boolean email_verified;

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

    public static String getUserImgId() {
        return userImgId;
    }

    public static void setUserImgId(String userImgId) {
        User.userImgId = userImgId;
    }

    public static String getUserPassKey_type() {
        return userPassKey_type;
    }

    public static void setUserPassKey_type(String userPassKey_type) {
        User.userPassKey_type = userPassKey_type;
    }

    public static Boolean getBoundPhone() {
        return boundPhone;
    }

    public static void setBoundPhone(Boolean boundPhone) {
        User.boundPhone = boundPhone;
    }

    public static String getUserBornDate() {
        return userBornDate;
    }

    public static void setUserBornDate(String userBornDate) {
        User.userBornDate = userBornDate;
    }

    public static String getUserImgAvatar() {
        return userImgAvatar;
    }

    public static void setUserImgAvatar(String userImgAvatar) {
        User.userImgAvatar = userImgAvatar;
    }

    public static Bitmap getUserImgBitmap() {
        return userImgBitmap;
    }

    public static void setUserImgBitmap(Bitmap userImgBitmap) {
        User.userImgBitmap = userImgBitmap;
    }

    public static Boolean getEmail_verified() {
        return email_verified;
    }

    public static void setEmail_verified(Boolean email_verified) {
        User.email_verified = email_verified;
    }
}
