package com.example.my.mamer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.my.mamer.R;
import com.example.my.mamer.bean.RecommendResource;

import java.util.ArrayList;

public class RecommendArticleAdapter extends BaseAdapter {

    private ArrayList<RecommendResource> data= new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public RecommendArticleAdapter(Context context) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public RecommendArticleAdapter(ArrayList<RecommendResource> data, Context context) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public final class listItem{
        private TextView linkName;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecommendArticleAdapter.listItem listViewItem=null;
        if (convertView==null){
            listViewItem=new RecommendArticleAdapter.listItem();
//            获得组件并实例化
            convertView=layoutInflater.inflate(R.layout.fragment_recommend_article_item,null);
            listViewItem.linkName=convertView.findViewById(R.id.recommend_article_name);
            convertView.setTag(listViewItem);
        }else {
            listViewItem= (RecommendArticleAdapter.listItem) convertView.getTag();
        }
//        绑定数据
        if (null==data) return convertView;
        listViewItem.linkName.setText(data.get(position).getTitle());
        return convertView;
    }

    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<RecommendResource> list){
        this.data=list;
        notifyDataSetChanged();
        Log.e("RecommendResource更新视图:","-----------------------");
    }

    public void clearData(){
        data.clear();
    }
}
