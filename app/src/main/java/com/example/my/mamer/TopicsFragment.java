package com.example.my.mamer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my.mamer.adapter.TopicTabAdapter;
import com.example.my.mamer.fragment.TopicNotice;
import com.example.my.mamer.fragment.TopicQuestionAnswer;
import com.example.my.mamer.fragment.TopicShare;
import com.example.my.mamer.fragment.TopicTeach;

import java.util.ArrayList;

public class TopicsFragment extends Fragment {
//    title
    private TabLayout titleInfoLayout;
    private ViewPager contentInfoView;
//    fragment
    private TopicNotice topicNotice;
    private TopicQuestionAnswer topicQuestionAnswer;
    private TopicShare topicShare;
    private TopicTeach topicTeach;
//    list
    private ArrayList<Fragment> listFragment;
    private ArrayList<String> listTitle;
//    适配器
    private TopicTabAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_topics,container,false);
//        初始化控件
        titleInfoLayout=view.findViewById(R.id.topic_info_title);
        contentInfoView=view.findViewById(R.id.topic_info_content);
//        初始化各fragment
        topicShare=new TopicShare();
        topicTeach=new TopicTeach();
        topicQuestionAnswer=new TopicQuestionAnswer();
        topicNotice=new TopicNotice();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        装入fragment
        listFragment=new ArrayList<>();
        listFragment.add(topicShare);
        listFragment.add(topicTeach);
        listFragment.add(topicQuestionAnswer);
        listFragment.add(topicNotice);


//装入title名
        listTitle=new ArrayList<>();
        listTitle.add("分享");
        listTitle.add("教程");
        listTitle.add("问答");
        listTitle.add("公告");
//        设置tabLayout模式
//        titleInfoLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
//        为TabLayout添加tab名称
        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(0)));
        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(1)));
        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(2)));
        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(3)));
        adapter=new TopicTabAdapter(getChildFragmentManager(),listFragment,listTitle);

//        viewPager加载adapter
        contentInfoView.setAdapter(adapter);
//        tabLayout加载viewPager
        titleInfoLayout.setupWithViewPager(contentInfoView);

    }
}
