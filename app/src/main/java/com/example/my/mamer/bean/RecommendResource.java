package com.example.my.mamer.bean;

public class RecommendResource {
    private String id;
    private String title;
    private String link;
    private String recommendUserId;
    private String recommendUserName;
    private String recommendUserAvatar;
    private String recommendUserIntroduction;
    private String recommendUserAvatarType;


    public  RecommendResource(){}
    public RecommendResource(String id, String title, String link) {
        this.id = id;
        this.title = title;
        this.link = link;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getRecommendUserName() {
        return recommendUserName;
    }

    public void setRecommendUserName(String recommendUserName) {
        this.recommendUserName = recommendUserName;
    }

    public String getRecommendUserAvatar() {
        return recommendUserAvatar;
    }

    public void setRecommendUserAvatar(String recommendUserAvatar) {
        this.recommendUserAvatar = recommendUserAvatar;
    }

    public String getRecommendUserIntroduction() {
        return recommendUserIntroduction;
    }

    public void setRecommendUserIntroduction(String recommendUserIntroduction) {
        this.recommendUserIntroduction = recommendUserIntroduction;
    }

    public String getRecommendUserId() {
        return recommendUserId;
    }

    public void setRecommendUserId(String recommendUserId) {
        this.recommendUserId = recommendUserId;
    }

    public String getRecommendUserAvatarType() {
        return recommendUserAvatarType;
    }

    public void setRecommendUserAvatarType(String recommendUserAvatarType) {
        this.recommendUserAvatarType = recommendUserAvatarType;
    }
}
