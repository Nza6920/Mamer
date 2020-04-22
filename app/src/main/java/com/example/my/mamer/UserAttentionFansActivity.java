package com.example.my.mamer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.my.mamer.adapter.TopicTabAdapter;
import com.example.my.mamer.fragment.UserAttention;
import com.example.my.mamer.fragment.UserFans;

import java.util.ArrayList;

public class UserAttentionFansActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    //    title
    private TabLayout titleInfoLayout;
    private ViewPager contentInfoView;
    //    fragment
    private UserAttention userAttention;
    private UserFans userFans;
    //    list
    private ArrayList<Fragment> listFragment;
    private ArrayList<String> listTitle;
    //    适配器
    private TopicTabAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_attention_fans);

        titleInfoLayout=findViewById(R.id.attention_info_title);
        contentInfoView=findViewById(R.id.attention_info_content);

        userAttention=new UserAttention();
        userFans=new UserFans();

        initView();
    }

    public void initView(){
//        装入fragment
        listFragment=new ArrayList<>();
        listFragment.add(userAttention);
        listFragment.add(userFans);
//        装入title的名字
        listTitle=new ArrayList<>();
        listTitle.add("我的关注");
        listTitle.add("我的粉丝");

        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(0)));
        titleInfoLayout.addTab(titleInfoLayout.newTab().setText(listTitle.get(1)));

        adapter=new TopicTabAdapter(getSupportFragmentManager(),listFragment,listTitle);
//        viewPager加载adapter
        contentInfoView.setAdapter(adapter);
//        tabLayout加载viewPager
        titleInfoLayout.setupWithViewPager(contentInfoView);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {
        Log.e("attention:","当前可见viewpager"+i);
    }

    @Override
    public void onPageSelected(int i) {

    }

    @Override
    public void onPageScrollStateChanged(int i) {
        Log.e("attention:","页面正在滑动");
    }
}
