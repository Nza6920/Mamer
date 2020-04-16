package com.example.my.mamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.R;
import com.example.my.mamer.bean.TopicContent;

import java.util.ArrayList;

import static com.example.my.mamer.MyApplication.getContext;

public class TopicContentAdapter extends BaseAdapter {

    private ArrayList<TopicContent> data=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public TopicContentAdapter(Context context) {
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public TopicContentAdapter(Context context, ArrayList<TopicContent> data) {
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }
//    组建对应listView中的控件
    public final class listItem{
    private ImageView authorImage;
    private TextView topicContentTitle;
    private TextView topicAuthorNamePic;
    private TextView topicAuthorName;
    private TextView topicTimePic;
    private TextView topicTime;
    private TextView topicDiscussCount;
}

    @Override
    public int getCount() {
        if (null==data)
            return 0;
        return data.size();
    }
//获得某一位置的数据
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }
//获得唯一标识
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position,View convertView,  ViewGroup parent) {
        listItem listViewItem=null;
        if (convertView==null){
            listViewItem=new listItem();
//            获得组件并实例化
            convertView=layoutInflater.inflate(R.layout.fragment_topic_cotent_item,null);
            listViewItem.authorImage=convertView.findViewById(R.id.topic_content_author_pic);
            listViewItem.topicContentTitle=convertView.findViewById(R.id.topic_content_title);
            listViewItem.topicAuthorNamePic=convertView.findViewById(R.id.topic_content_author_name_pic);
            listViewItem.topicAuthorName=convertView.findViewById(R.id.topic_content_author_name);
            listViewItem.topicTimePic=convertView.findViewById(R.id.topic_content_time_pic);
            listViewItem.topicTime=convertView.findViewById(R.id.topic_content_time);
            listViewItem.topicDiscussCount=convertView.findViewById(R.id.topic_discuss_count);
            convertView.setTag(listViewItem);
        }else {
            listViewItem= (listItem) convertView.getTag();
        }
        if (null==data) return convertView;
//        绑定数据
            RequestOptions options=new RequestOptions()
                    .error(R.mipmap.ic_image_error)
                    .placeholder(R.mipmap.ic_image_error);
            Glide.with(getContext())
                    .asBitmap()
                    .load(data.get(position).getTopicAuthorPic())
                    .apply(options)
                    .into(listViewItem.authorImage);
        listViewItem.topicContentTitle.setText(( data.get(position)).getTopicTitle());
        listViewItem.topicAuthorName.setText(( data.get(position)).getTopicAuthorName());
        listViewItem.topicTime.setText(( data.get(position)).getCreateTime());
        listViewItem.topicDiscussCount.setText((data.get(position)).getReplyCount());
        return convertView;
    }
    //    更新数据，并且清除之前的数据
//    public void updateData(ArrayList<TopicContent> list){
//        Log.e("更新数据:","-----------------------");
//        if (null==list)
//            return;
//        this.data=list;
//        notifyDataSetChanged();
//        Log.e("更新视图:","-----------------------");
//    }
    public void clearData(){
        this.data.clear();
    }

    public void updateAdd(ArrayList<TopicContent> list){
        this.data.addAll(list);
        notifyDataSetChanged();
    }
    public int getDataCount(){
        if (null==data){
            return 0;
        }
        else {
            return this.data.size();
        }
    }

}
