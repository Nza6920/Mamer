package com.example.my.mamer.config;

import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.bean.TopicDIvid;

import java.util.ArrayList;

public class GlobalTopicReply {
//    存文章id，用以显示回复列表
    public static GlobalTopicReply reply=new GlobalTopicReply();
    public ReplyUser replyUser;
    public String categoryId;
    public String tagId;
    public ArrayList<TopicDIvid> topicDivid=new ArrayList<>();
}
