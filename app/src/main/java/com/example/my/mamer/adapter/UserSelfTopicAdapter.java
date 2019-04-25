package com.example.my.mamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.my.mamer.R;
import com.example.my.mamer.bean.TopicContent;

import java.util.ArrayList;

public class UserSelfTopicAdapter extends BaseAdapter {
//存放的数组
    private ArrayList<TopicContent> data;
//    布局解析器
    private LayoutInflater layoutInflater;
    private Context context;

    public UserSelfTopicAdapter( Context context,ArrayList<TopicContent> data) {
        Log.e("Tag","进入接收数据");
        this.data=data;
        layoutInflater=LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
//        数据集的长度
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.e("Tag","进入界面展示");
        listItem listViewItem=null;
        Log.e("Tag",view+"有无界面");
        if (view==null){
            view=layoutInflater.inflate(R.layout.activity_user_self_topic_list_item,null);
            Log.e("Tag","界面映射");
            listViewItem=new listItem();
            listViewItem.userSelfTopicTitle=view.findViewById(R.id.user_self_topic_title);
            listViewItem.userSelfTopicReplyCount=view.findViewById(R.id.user_self_topic_reply_count);
            listViewItem.userSelfTopicTime=view.findViewById(R.id.user_self_topic_time);
            view.setTag(listViewItem);
            Log.e("Tag","映射完毕");
        }else {
            listViewItem= (listItem) view.getTag();
        }
        Log.e("Tag","设置数据显示");
        listViewItem.userSelfTopicReplyCount.setText(data.get(position).getReplyCount()+"回复");
        listViewItem.userSelfTopicTitle.setText(data.get(position).getTopicTitle());
        Log.e("Tag",data.get(position).getReplyCount());
        listViewItem.userSelfTopicTime.setText(data.get(position).getUpdateTime());
        Log.e("Tag","设置数据填充完成");

        return view;
    }
    //    组建对应listView中的控件
   class listItem{
        private TextView userSelfTopicTitle;
        private TextView userSelfTopicReplyCount;
        private TextView userSelfTopicTime;
    }

}
