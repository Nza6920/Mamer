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

public class TopicLikeAdapter extends BaseAdapter {
    private ArrayList<ReplyUser> userArrayList =new ArrayList<>();

    //    布局解析器
    private LayoutInflater layoutInflater;
    private Context context;

    public TopicLikeAdapter(Context context) {
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public TopicLikeAdapter(Context context, ArrayList<ReplyUser> data){
        this.context=context;
        this.userArrayList=data;
        this.layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if (null==userArrayList)
            return 0;
        return userArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return userArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        listItem listViewItem=null;
        if(view==null){
            view=layoutInflater.inflate(R.layout.topic_like_item,null);
            listViewItem=new TopicLikeAdapter.listItem();
            listViewItem.imgLikeUserAvatar=view.findViewById(R.id.like_list_avatar);
            listViewItem.tvUserName=view.findViewById(R.id.like_list_name);
            listViewItem.tvUserIntro=view.findViewById(R.id.like_list_intro);
            view.setTag(listViewItem);
        }else {
            listViewItem= (listItem) view.getTag();
        }
        if (userArrayList.size()==0) return view;
        RequestOptions options=new RequestOptions()
                .error(R.mipmap.ic_image_error)
                .placeholder(R.mipmap.ic_image_error);
        Glide.with(getContext())
                .asBitmap()
                .load(userArrayList.get(i).getUserImg())
                .apply(options)
                .into(listViewItem.imgLikeUserAvatar);
        listViewItem.tvUserName.setText(userArrayList.get(i).getUserName());
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        listViewItem.tvUserIntro.setText(Html.fromHtml(userArrayList.get(i).getUserInfo(),Html.FROM_HTML_MODE_COMPACT));
//        }else {
//            listViewItem.tvUserContent.setText(Html.fromHtml(replyListData.get(i).getContent()));
//        }
        return view;
    }
    class listItem{
        private ImageView imgLikeUserAvatar;
        private TextView tvUserName;
        private TextView tvUserIntro;
    }
    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<ReplyUser> list){
        Log.e("ReplyUser更新数据:","-----------------------");
        if (null==list)
            return;
        this.userArrayList.addAll(list);
        notifyDataSetChanged();
        Log.e("ReplyUser更新视图:","-----------------------");
    }
    public void clearData(){
        this.userArrayList.clear();
    }
}
