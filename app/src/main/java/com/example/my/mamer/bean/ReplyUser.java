package com.example.my.mamer.bean;

public class ReplyUser {
    private String userId;
    private String userImg;
    private String userName;
    private String replyId;
    private String essayId;
    private String content;
    private String title;

    public ReplyUser(){}
    public ReplyUser(String userId, String userImg, String userName, String replyId, String essayId, String content) {
        this.userId = userId;
        this.userImg = userImg;
        this.userName = userName;
        this.replyId = replyId;
        this.essayId = essayId;
        this.content = content;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getEssayId() {
        return essayId;
    }

    public void setEssayId(String essayId) {
        this.essayId = essayId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
