package com.example.my.mamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.my.mamer.R;
import com.example.my.mamer.config.TopicContent;

import java.util.List;

public class TopicContentAdapter extends ArrayAdapter<TopicContent> {
    private final int resourceId;
    private LinearLayout topicContentLayout;
    private ImageView authorImage;
    private TextView topicContentTitle;
    private TextView topicAuthorNamePic;
    private TextView topicAuthorName;
    private TextView topicTimePic;
    private TextView topicTime;
    private TextView topicDiscussCount;

    public TopicContentAdapter(Context context, int textViewResourceId, List<TopicContent> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position,View convertView,  ViewGroup parent) {
//        获取当前项的TopicContent实例
        TopicContent topicContent= (TopicContent) getItem(position);
//        实例化一个对象
        View view=LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        topicContentLayout=view.findViewById(R.id.topic_part_content);
        authorImage=view.findViewById(R.id.topic_content_author_pic);
        topicContentTitle=view.findViewById(R.id.topic_content_title);
        topicAuthorNamePic=view.findViewById(R.id.topic_content_author_name_pic);
        topicAuthorName=view.findViewById(R.id.topic_content_author_name);
        topicTimePic=view.findViewById(R.id.topic_content_time_pic);
        topicTime=view.findViewById(R.id.topic_content_time);
        topicDiscussCount=view.findViewById(R.id.topic_discuss_count);
//        设置
        authorImage.setImageBitmap(topicContent.getTopicAuthorPic());
        topicContentTitle.setText(topicContent.getTopicTitle());
        topicAuthorName.setText(topicContent.getTopicAuthorName());
        topicTime.setText(topicContent.getCreateTime());
        topicDiscussCount.setText(topicContent.getReplyCount());
        return view;
    }
}
