package com.example.my.mamer.bean;

public class TopicContent {
//    话题Id,文章标题，作者id，作者头像，回复数，浏览数，创建时间，最新回复时间，话题分类,文章内容
    private   String topicId;
    private   String topicTitle;
    private   String topicAuthorId;
    private   String topicAuthorName;
    private   String topicAuthorPic;
    private   String replyCount;
    private   int viewCount;
    private   String createTime;
    private   String updateTime;
    private   String categoryId;
    private   String topicConten;
    private  String categoryName;

    public TopicContent() {
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
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

    public void setTopicAuthorId(String topicAuthorId) {
        this.topicAuthorId = topicAuthorId;
    }

    public String getTopicAuthorName() {
        return topicAuthorName;
    }

    public void setTopicAuthorName(String topicAuthorName) {
        this.topicAuthorName = topicAuthorName;
    }

    public String getTopicAuthorPic() {
        return topicAuthorPic;
    }

    public void setTopicAuthorPic(String topicAuthorPic) {
        this.topicAuthorPic = topicAuthorPic;
    }

    public String getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(String replyCount) {
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTopicConten() {
        return topicConten;
    }

    public void setTopicConten(String topicConten) {
        this.topicConten = topicConten;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

