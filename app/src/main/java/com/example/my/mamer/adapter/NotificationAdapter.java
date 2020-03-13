package com.example.my.mamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.R;
import com.example.my.mamer.bean.NotificationUser;

import java.util.ArrayList;

import static com.example.my.mamer.MyApplication.getContext;

public class NotificationAdapter extends BaseAdapter {

    private ArrayList<NotificationUser> notificationData;
    private LayoutInflater layoutInflater;
    private Context context;

    public NotificationAdapter(Context context,ArrayList<NotificationUser> data){
        Log.e("Tag","进入接收数据");
        this.context=context;
        this.notificationData=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notificationData.size();
    }

    @Override
    public Object getItem(int i) {
        return notificationData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.e("Tag","进入界面展示");
        listItem listViewItem=null;
        Log.e("Tag",view+"有无界面");
        if (view==null){
            view=layoutInflater.inflate(R.layout.fragment_notification_item,null);
            listViewItem=new listItem();
            Log.e("Tag","界面映射");
            listViewItem.tvUserName=view.findViewById(R.id.notification_user_name);
            listViewItem.userImg=view.findViewById(R.id.notification_user_img);
            listViewItem.tvTopic=view.findViewById(R.id.notification_user_topic);
            listViewItem.tvContent=view.findViewById(R.id.notification_user_content);

            Log.e("Tag","映射完毕");
            view.setTag(listViewItem);
        }else {
            listViewItem= (listItem) view.getTag(i);
        }
        Log.e("Tag","设置数据显示");
        RequestOptions options=new RequestOptions()
                .error(R.mipmap.ic_image_error)
                .placeholder(R.mipmap.ic_image_error);
        if (listViewItem.userImg != null) {

            Glide.with(getContext())
                    .asBitmap()
                    .load(notificationData.get(i).getUserImg())
                    .apply(options)
                    .into(listViewItem.userImg);
        }
        listViewItem.tvUserName.setText(notificationData.get(i).getUserName());
        listViewItem.tvTopic.setText(notificationData.get(i).getTopicName());
        listViewItem.tvContent.setText(notificationData.get(i).getUserContent());
        Log.e("Tag","设置数据填充完成");
        return view;
    }
    class listItem{
        private TextView tvUserName;
        private ImageView userImg;
        private TextView tvTopic;
        private TextView tvContent;
    }
}
