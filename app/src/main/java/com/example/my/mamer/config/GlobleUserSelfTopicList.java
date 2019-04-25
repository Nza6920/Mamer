package com.example.my.mamer.config;

import com.example.my.mamer.bean.TopicContent;

public class GlobleUserSelfTopicList {
    TopicContent title;
    TopicContent reply;
    TopicContent time;

    public GlobleUserSelfTopicList(TopicContent title, TopicContent reply, TopicContent time) {
        this.title = title;
        this.reply = reply;
        this.time = time;
    }

    public TopicContent getTitle() {
        return title;
    }

    public void setTitle(TopicContent title) {
        this.title = title;
    }

    public TopicContent getReply() {
        return reply;
    }

    public void setReply(TopicContent reply) {
        this.reply = reply;
    }

    public TopicContent getTime() {
        return time;
    }

    public void setTime(TopicContent time) {
        this.time = time;
    }
}
