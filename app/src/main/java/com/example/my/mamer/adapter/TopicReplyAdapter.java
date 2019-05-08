package com.example.my.mamer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.my.mamer.bean.ReplyUser;

import java.util.ArrayList;

public class TopicReplyAdapter extends BaseAdapter {

    private ArrayList<ReplyUser> replyListData;
    
    //    布局解析器
    private LayoutInflater layoutInflater;
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
