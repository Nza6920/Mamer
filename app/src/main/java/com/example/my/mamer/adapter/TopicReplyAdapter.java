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
import com.example.my.mamer.bean.ReplyUser;

import java.util.ArrayList;

import static com.example.my.mamer.MyApplication.getContext;

public class TopicReplyAdapter extends BaseAdapter {

    private ArrayList<ReplyUser> replyListData;

    //    布局解析器
    private LayoutInflater layoutInflater;
    private Context context;

    public TopicReplyAdapter(Context context,ArrayList<ReplyUser> data){
        this.context=context;
        this.replyListData=data;
        this.layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return replyListData.size();
    }

    @Override
    public Object getItem(int i) {
        return replyListData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        listItem listViewItem=null;
        if(view==null){
            view=layoutInflater.inflate(R.layout.topic_reply_item,null);
            listViewItem=new listItem();
            listViewItem.imgReplyUserAvatar=view.findViewById(R.id.reply_user_img);
            listViewItem.tvUserName=view.findViewById(R.id.reply_user_name);
            listViewItem.tvUserContent=view.findViewById(R.id.reply_user_content);
            view.setTag(listViewItem);
        }else {
            listViewItem= (listItem) view.getTag();
        }

        RequestOptions options=new RequestOptions()
                .error(R.mipmap.ic_image_error)
                .placeholder(R.mipmap.ic_image_error);
        Glide.with(getContext())
                .asBitmap()
                .load(replyListData.get(i).getUserImg())
                .apply(options)
                .into(listViewItem.imgReplyUserAvatar);
        listViewItem.tvUserName.setText(replyListData.get(i).getUserName());
        listViewItem.tvUserContent.setText(replyListData.get(i).getContent());
        return view;
    }
    class listItem{
        private ImageView imgReplyUserAvatar;
        private TextView tvUserName;
        private TextView tvUserContent;
    }
}