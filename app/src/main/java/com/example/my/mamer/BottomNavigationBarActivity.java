package com.example.my.mamer;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

public class BottomNavigationBarActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private FragmentManager mFragmentManager;
    private BottomNavigationBar bottomNavigationBar;

    private TopicsFragment topicsFragment;
    private UserFragment userFragment;

//    默认话题为首页
    private int lastSelectionedPosition=0;
    private FragmentTransaction transaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);

        bottomNavigationBar=findViewById(R.id.bottom_navigation_bar);
        init();
    }
//初始底部导航栏
    private void init(){
//        item个数《=3
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
//        监听切换点击事件
        bottomNavigationBar.setTabSelectedListener(this);
//        点击无水波纹效果
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
//        添加item数
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_topics_selected,"话题"))
                .setActiveColor("#12A3E9")
                .setActiveColor("#999999");
    }
    @Override
    public void onTabSelected(int position) {

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }
}
