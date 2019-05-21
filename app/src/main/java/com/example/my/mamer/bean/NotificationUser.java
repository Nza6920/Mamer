package com.example.my.mamer.bean;

public class NotificationUser {
    private String userName;
    private String userId;
    private String userImg;
    private String topicId;
    private String topicName;
    private String userContent;
    private String time;

    public NotificationUser() {
    }

    public NotificationUser(String userName, String userId, String userImg, String topicId, String userContent, String time) {
        this.userName = userName;
        this.userId = userId;
        this.userImg = userImg;
        this.topicId = topicId;
        this.userContent = userContent;
        this.time = time;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getUserContent() {
        return userContent;
    }

    public void setUserContent(String userContent) {
        this.userContent = userContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
