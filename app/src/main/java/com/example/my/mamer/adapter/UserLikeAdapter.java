package com.example.my.mamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.my.mamer.R;
import com.chinalwb.are.android.inner.Html;
import com.example.my.mamer.bean.TopicContent;

import java.util.ArrayList;

public class UserLikeAdapter extends BaseAdapter {

    private ArrayList<TopicContent> userArrayList =new ArrayList<>();

    //    布局解析器
    private LayoutInflater layoutInflater;
    private Context context;

    public UserLikeAdapter(Context context) {
        this.context = context;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public UserLikeAdapter(Context context, ArrayList<TopicContent> data){
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
            view=layoutInflater.inflate(R.layout.activity_user_self_like_list_item,null);
            listViewItem=new listItem();
            listViewItem.tvUserName=view.findViewById(R.id.user_like_title);
            listViewItem.tvUserIntro=view.findViewById(R.id.user_like_excerpt);
            view.setTag(listViewItem);
        }else {
            listViewItem= (listItem) view.getTag();
        }
        if (userArrayList.size()==0) return view;
        listViewItem.tvUserName.setText(userArrayList.get(i).getTopicTitle());
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        listViewItem.tvUserIntro.setText(Html.fromHtml(userArrayList.get(i).getTopicExcerpt(),Html.FROM_HTML_MODE_COMPACT));
//        }else {
//            listViewItem.tvUserContent.setText(Html.fromHtml(replyListData.get(i).getContent()));
//        }
        return view;
    }
    class listItem{
        private TextView tvUserName;
        private TextView tvUserIntro;
    }
    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<TopicContent> list){
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
