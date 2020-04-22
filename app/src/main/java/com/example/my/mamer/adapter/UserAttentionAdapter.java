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

public class UserAttentionAdapter extends BaseAdapter {

    private ArrayList<ReplyUser> list =new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public UserAttentionAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list==null){
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        listItem listViewItem=null;
        if (convertView==null){
            listViewItem=new listItem();
            convertView=layoutInflater.inflate(R.layout.topic_like_item,null);
            listViewItem.userAvatar=convertView.findViewById(R.id.like_list_avatar);
            listViewItem.userName=convertView.findViewById(R.id.like_list_name);
            listViewItem.userIntroduction=convertView.findViewById(R.id.like_list_intro);
            convertView.setTag(listViewItem);
        }else {
            listViewItem= (listItem) convertView.getTag();
        }
        if (null==list) return convertView;
        //        绑定数据
        RequestOptions options=new RequestOptions()
                .error(R.mipmap.ic_image_error)
                .placeholder(R.mipmap.ic_image_error);
        Glide.with(getContext())
                .asBitmap()
                .load(list.get(position).getUserImg())
                .apply(options)
                .into(listViewItem.userAvatar);
        listViewItem.userName.setText(list.get(position).getUserName());

        listViewItem.userIntroduction.setText(Html.fromHtml(list.get(position).getUserInfo(),Html.FROM_HTML_MODE_COMPACT));
        return convertView;
    }

    //    组建对应listView中的控件
    public final class listItem{
        private ImageView userAvatar;
        private TextView userName;
        private TextView userIntroduction;
    }
    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<ReplyUser> arrayList){
        Log.e("attention更新数据:","-----------------------");
        this.list.addAll(arrayList);
        notifyDataSetChanged();
        Log.e("attention更新视图:","-----------------------");
    }

    public void clearData(){
        this.list.clear();
    }
}
