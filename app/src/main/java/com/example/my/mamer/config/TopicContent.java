package com.example.my.mamer.config;

import android.graphics.Bitmap;

public class TopicContent {
//    话题Id,文章标题，作者id，作者头像，回复数，浏览数，创建时间，最新回复时间，话题分类
    private int topicId;
    private String topicTitle;
    private String topicAuthorId;
    private String topicAuthorName;
    private Bitmap topicAuthorPic;
    private int replyCount;
    private int viewCount;
    private String createTime;
    private String updateTime;
    private int categoryId;

    public TopicContent(int topicId, String topicTitle, String topicAuthorId,Bitmap topicAuthorPic, int replyCount, int viewCount, String createTime, String updateTime, int categoryId) {
        this.topicId = topicId;
        this.topicTitle = topicTitle;
        this.topicAuthorId = topicAuthorId;
        this.topicAuthorPic = topicAuthorPic;
        this.replyCount = replyCount;
        this.viewCount = viewCount;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.categoryId = categoryId;
    }

    public TopicContent() {
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicAuthorId() {
        return topicAuthorId;
    }

    public String getTopicAuthorName() {
        return topicAuthorName;
    }

    public void setTopicAuthorName(String topicAuthorName) {
        this.topicAuthorName = topicAuthorName;
    }

    public void setTopicAuthorId(String topicAuthorId) {
        this.topicAuthorId = topicAuthorId;
    }

    public Bitmap getTopicAuthorPic() {
        return topicAuthorPic;
    }

    public void setTopicAuthorPic(Bitmap topicAuthorPic) {
        this.topicAuthorPic = topicAuthorPic;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}

