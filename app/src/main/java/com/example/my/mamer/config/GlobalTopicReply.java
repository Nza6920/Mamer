package com.example.my.mamer.config;

import com.example.my.mamer.bean.ReplyUser;

public class GlobalTopicReply {
//    存文章id，用以显示回复列表
    public static GlobalTopicReply reply=new GlobalTopicReply();
    public ReplyUser replyUser;
    public String categoryId;
    public String tagId;
}
