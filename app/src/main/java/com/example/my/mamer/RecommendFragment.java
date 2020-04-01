package com.example.my.mamer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.my.mamer.adapter.TopicTabAdapter;
import com.example.my.mamer.fragment.RecommendActive;
import com.example.my.mamer.fragment.RecommendArticle;

import java.util.ArrayList;

public class RecommendFragment extends Fragment {
//    title
    private TabLayout titleInfoLayout;
    private ViewPager contentInfoView;
//    fragment
    private RecommendActive recommendActive;
    private RecommendArticle recommendArticle;
//    list
    private ArrayList<Fragment> listFragment;
    private ArrayList<String> listTitle;
//    适配器
    private TopicTabAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_recommend, container, false);
//        初始化控件
        titleInfoLayout=view.findViewById(R.id.recommend_info_title);
        contentInfoView=view.findViewById(R.id.recommend_info_content);
//        初始化fragment
        recommendActive=new RecommendActive();
        recommendArticle=new RecommendArticle();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        装入fragment
        listFragment=new ArrayList<>();
        listFragment.add(recommendActive);
        listFragment.add(recommendArticle);
//        装入title的名字
        listTitle=new ArrayList<>();
        listTitle.add("活跃用户");
        listTitle.add("资源推荐");

        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(0)));
        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(1)));

        adapter=new TopicTabAdapter(getChildFragmentManager(),listFragment,listTitle);
//        viewPager加载adapter
        contentInfoView.setAdapter(adapter);
//        tabLayout加载viewPager
        titleInfoLayout.setupWithViewPager(contentInfoView);
    }



}
