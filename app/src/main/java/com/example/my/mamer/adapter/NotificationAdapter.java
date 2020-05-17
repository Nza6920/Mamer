package com.example.my.mamer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chinalwb.are.android.inner.Html;
import com.example.my.mamer.R;
import com.example.my.mamer.bean.NotificationUser;

import java.util.ArrayList;

import static com.example.my.mamer.MyApplication.getContext;

public class NotificationAdapter extends BaseAdapter {

    private ArrayList<NotificationUser> notificationData= new ArrayList<NotificationUser>();
    private LayoutInflater layoutInflater;
    private Context context;

    public NotificationAdapter(Context context) {
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public NotificationAdapter(Context context, ArrayList<NotificationUser> data){
        Log.e("Tag","进入接收数据");
        this.context=context;
        this.notificationData=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (null==notificationData)
            return 0;
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
            listViewItem= (listItem) view.getTag();
        }
        Log.e("Tag","设置数据显示");
        if (null==notificationData)return view;

        RequestOptions options=new RequestOptions()
                .error(R.mipmap.ic_image_error)
                .placeholder(R.mipmap.ic_image_error);
        if (notificationData.get(i).getUserImg().equals("fig")){
            Glide.with(getContext())
                    .load(notificationData.get(i).getUserImg())
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource instanceof GifDrawable){
                                ((GifDrawable) resource).setLoopCount(5);
                            }
                            return false;
                        }
                    }).into(listViewItem.userImg);
        }else {
            Glide.with(getContext())
                    .asBitmap()
                    .load(notificationData.get(i).getUserImg())
                    .apply(options)
                    .into(listViewItem.userImg);
        }

        listViewItem.tvUserName.setText(notificationData.get(i).getUserName());
        listViewItem.tvTopic.setText(notificationData.get(i).getTopicName());
        listViewItem.tvContent.setText(Html.fromHtml(notificationData.get(i).getUserContent(),Html.FROM_HTML_MODE_COMPACT));
        Log.e("Tag","设置信息界面数据填充完成");
        return view;
    }
    class listItem{
        private TextView tvUserName;
        private ImageView userImg;
        private TextView tvTopic;
        private TextView tvContent;
    }
    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<NotificationUser> list){
        Log.e("NotificationUser更新数据:","-----------------------");
        if (null==list)
            return;
        this.notificationData.clear();
        this.notificationData=list;
        notifyDataSetChanged();
        Log.e("NotificationUser更新视图:","-----------------------");
    }
}
