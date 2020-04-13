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

public class ToUserTopicAdapter extends BaseAdapter {

    private ArrayList<TopicContent> listData =new ArrayList<>();

    //    布局解析器
    private LayoutInflater layoutInflater;
    private Context context;

    public ToUserTopicAdapter(Context context) {
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (null==listData)
            return 0;
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        listItem listViewItem=null;
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.activity_to_user_item,null);
            listViewItem=new listItem();
            listViewItem.tvTopicName=convertView.findViewById(R.id.to_user_topics_title);
            listViewItem.tvTopiCexcerpt=convertView.findViewById(R.id.to_user_topics_excerpt);
            convertView.setTag(listViewItem);
        }else {
            listViewItem= (listItem) convertView.getTag();
        }
        if (listData!=null){
            listViewItem.tvTopicName.setText(listData.get(position).getTopicTitle());
            listViewItem.tvTopiCexcerpt.setText(listData.get(position).getTopicExcerpt());
        }

        return convertView;
    }
    class listItem{
        private TextView tvTopicName;
        private TextView tvTopiCexcerpt;
    }


    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<TopicContent> list){
        Log.e("RecommendResource更新数据:","-----------------------");
        this.listData=list;
        notifyDataSetChanged();
        Log.e("RecommendResource更新视图:","-----------------------");
    }
    public void clearData(){
        this.listData.clear();
    }
}
