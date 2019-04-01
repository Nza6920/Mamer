package com.example.my.mamer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;

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
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_topics_selected,"话题")
//               选中时的颜色
                .setActiveColor("#12A3E9")
//               未选中时的颜色
                .setInActiveColor("#999999")
//              未选中时的图片资源
                .setInactiveIconResource(R.mipmap.ic_topics_none))
            .addItem(new BottomNavigationItem(R.mipmap.ic_personal_change,"我的")
            .setActiveColor("#12A3E9")
            .setInActiveColor("#999999")
            .setInactiveIconResource(R.mipmap.ic_personal))
//                设置默认选中项
                .setFirstSelectedPosition(lastSelectionedPosition)
                .initialise();
//        设置默认开启项
        setDefaultFragment();
    }
//    设置默认开启的fragment
    private void setDefaultFragment(){
        mFragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=mFragmentManager.beginTransaction();
        topicsFragment=new TopicsFragment();
//        往activity添加fragment
        transaction.add(R.id.fragment_content,topicsFragment);
        transaction.commit();
    }
//    切换事件
    @Override
    public void onTabSelected(int position) {
//        每次点击赋值
        lastSelectionedPosition=position;
//        开启事务
        transaction=mFragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (position){
            case 0:
                if (topicsFragment==null){
                    topicsFragment=new TopicsFragment();
                    transaction.add(R.id.fragment_content,topicsFragment);
                }else {
                    transaction.show(topicsFragment);
                }
                break;
            case 1:
                if (userFragment==null){
                    userFragment=new UserFragment();
                    transaction.add(R.id.fragment_content,userFragment);
                }else {
                    transaction.show(userFragment);
                }
                break;
        }
//        事务提交
        transaction.commit();

    }
//隐藏当前fragment
    private void hideFragment(FragmentTransaction transaction){
        if(topicsFragment!=null){
            transaction.hide(topicsFragment);
        }
        if (userFragment!=null){
            transaction.hide(userFragment);
        }
    }
    private TextBadgeItem textBadgeItem;
    private ShapeBadgeItem shapeBadgeItem;
//    展示消息点
    private void showNumberAndShape(){
//        消息
        textBadgeItem=new TextBadgeItem()
//                显示的文本
                .setText("m")
//                文本颜色
        .setTextColor("#ffffff")
//                圆环宽度
        .setBorderWidth(5)
//                圆环颜色
        .setBorderColor(Color.parseColor("#000000"))
//                背景颜色
        .setBackgroundColor("#FF4081")
//                选中是否隐藏
        .setHideOnSelect(true)
//                隐藏与动画的过渡时间
        .setAnimationDuration(300)
//                位置，默认右上角
        .setGravity(Gravity.RIGHT|Gravity.TOP);

//        形状
        shapeBadgeItem=new ShapeBadgeItem()
//                也可以设置为常量,形状
        .setShape(ShapeBadgeItem.SHAPE_OVAL)
//                颜色
        .setShapeColor(Color.RED)
//                距离item的边距，dp
        .setEdgeMarginInDp(this,0)
//                高宽值
        .setSizeInDp(this,15,15)
//                隐藏和展示的动画速度mm，和setHideOnSelect一起用
        .setAnimationDuration(300)
//                当选中状态时消失
        .setHideOnSelect(true);
    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

}
