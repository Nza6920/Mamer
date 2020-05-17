package com.example.my.mamer.bean;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
//    用户Id
    private  String userId;
//    用户名
    private  String userName;
//    用户邮箱
    private  String userEmail;
//    用户简介
    private  String userIntroduction;
//    用户密码
    private  String userPassKey;
//    默认头像
    private  String userImg;
//    头像id
    private  String userImgId;
//    修改后的头像网址
    private  String userImgAvatar;
//    修改后的头像Bitmap头像
    private  Bitmap userImgBitmap;
//    用户token类型
    private  String userPassKey_type;
//    是否绑定手机
    private  Boolean boundPhone;
//    注册时间
    private  String userBornDate;
//    邮箱是否验证
    private  Boolean email_verified;
//    暂存当前用户发帖图片image
public  HashMap<Integer,String> imagePaths=new HashMap<>();
//     暂存上传后返回的图片path
    public  ArrayList<String> imageContentPaths=new ArrayList<>();

//    头像类型
    public String avatarType;
    public User() {
    }

    public User(String userId, String userName, String userEmail, String userIntroduction, String userPassKey, String userImg, String userImgId, String userImgAvatar, Bitmap userImgBitmap, String userPassKey_type, Boolean boundPhone, String userBornDate, Boolean email_verified) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userIntroduction = userIntroduction;
        this.userPassKey = userPassKey;
        this.userImg = userImg;
        this.userImgId = userImgId;
        this.userImgBitmap = userImgBitmap;
        this.userPassKey_type = userPassKey_type;
        this.boundPhone = boundPhone;
        this.userBornDate = userBornDate;
        this.email_verified = email_verified;
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

    public String getUserIntroduction() {
        return userIntroduction;
    }

    public void setUserIntroduction(String userIntroduction) {
        this.userIntroduction = userIntroduction;
    }

    public String getUserPassKey() {
        return userPassKey;
    }

    public void setUserPassKey(String userPassKey) {
        this.userPassKey = userPassKey;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserImgId() {
        return userImgId;
    }

    public void setUserImgId(String userImgId) {
        this.userImgId = userImgId;
    }

    public Bitmap getUserImgBitmap() {
        return userImgBitmap;
    }

    public void setUserImgBitmap(Bitmap userImgBitmap) {
        this.userImgBitmap = userImgBitmap;
    }

    public String getUserPassKey_type() {
        return userPassKey_type;
    }

    public void setUserPassKey_type(String userPassKey_type) {
        this.userPassKey_type = userPassKey_type;
    }

    public Boolean getBoundPhone() {
        return boundPhone;
    }

    public void setBoundPhone(Boolean boundPhone) {
        this.boundPhone = boundPhone;
    }

    public String getUserBornDate() {
        return userBornDate;
    }

    public void setUserBornDate(String userBornDate) {
        this.userBornDate = userBornDate;
    }

    public Boolean getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(Boolean email_verified) {
        this.email_verified = email_verified;
    }

    public HashMap<Integer, String> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(HashMap<Integer, String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    public ArrayList<String> getImageContentPaths() {
        return imageContentPaths;
    }

    public void setImageContentPaths(ArrayList<String> imageContentPaths) {
        this.imageContentPaths = imageContentPaths;
    }

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    @Override
    public String toString() {
        return  userId+" "+ userName+" "+ userEmail+" "+ userIntroduction+" "+ userPassKey+" "+userImg+" "+userImgId+" "+userImgAvatar+" "+userImgBitmap+" "+userPassKey_type+" "+boundPhone+" "+userBornDate+" "+email_verified;
    }
}
