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
import com.chinalwb.are.android.inner.Html;
import com.example.my.mamer.R;
import com.example.my.mamer.bean.ReplyUser;

import java.util.ArrayList;

import static com.example.my.mamer.MyApplication.getContext;

public class TopicReplyAdapter extends BaseAdapter {

    private ArrayList<ReplyUser> replyListData =new ArrayList<>();

    //    布局解析器
    private LayoutInflater layoutInflater;
    private Context context;

    public TopicReplyAdapter(Context context) {
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public TopicReplyAdapter(Context context, ArrayList<ReplyUser> data){
        this.context=context;
        this.replyListData=data;
        this.layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if (null==replyListData)
            return 0;
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
            listViewItem.tvDel=view.findViewById(R.id.reply_user_del);
            view.setTag(listViewItem);
        }else {
            listViewItem= (listItem) view.getTag();
        }
        if (replyListData.size()==0)
            return view;
        if (!replyListData.get(i).getTagReplyRole().equals("localUser")){
            RequestOptions options=new RequestOptions()
                    .error(R.mipmap.ic_image_error)
                    .placeholder(R.mipmap.ic_image_error);
            Glide.with(getContext())
                    .asBitmap()
                    .load(replyListData.get(i).getUserImg())
                    .apply(options)
                    .into(listViewItem.imgReplyUserAvatar);
        }else {
            listViewItem.imgReplyUserAvatar.setVisibility(View.GONE);
            listViewItem.tvDel.setVisibility(View.VISIBLE);
        }
        listViewItem.tvUserName.setText(replyListData.get(i).getUserName());
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            listViewItem.tvUserContent.setText(Html.fromHtml(replyListData.get(i).getContent(),Html.FROM_HTML_MODE_COMPACT));
//        }else {
//            listViewItem.tvUserContent.setText(Html.fromHtml(replyListData.get(i).getContent()));
//        }
        return view;
    }
    class listItem{
        private ImageView imgReplyUserAvatar;
        private TextView tvUserName;
        private TextView tvUserContent;
        private TextView tvDel;
    }
    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<ReplyUser> list){
        Log.e("ReplyUser更新数据:","-----------------------");
        if (null==list)
            return;
        this.replyListData.addAll(list);
        notifyDataSetChanged();
        Log.e("ReplyUser更新视图:","-----------------------");
    }
    public void clearData(){
        this.replyListData.clear();
    }
}
