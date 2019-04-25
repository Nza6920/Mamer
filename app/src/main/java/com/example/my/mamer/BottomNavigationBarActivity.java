package com.example.my.mamer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.ShapeBadgeItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.example.my.mamer.config.GlobalUserInfo;
import com.example.my.mamer.util.LoadingDraw;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_FORMAT_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class BottomNavigationBarActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private FragmentManager mFragmentManager;
    private BottomNavigationBar bottomNavigationBar;
//首页话题和我的
    private TopicsFragment topicsFragment;
    private UserFragment userFragment;

//侧滑菜单
//    private DrawerLayout drawerLayout;
//    我的 ，侧滑菜单
//    private UserMenuRightFragment userMenuRightFragment;


//    默认话题为首页
    private int lastSelectionedPosition=0;
    private FragmentTransaction transaction;
//话题topic bar，分类，标题，新建话题
    private RelativeLayout topiclayout;
    private Button btnTopicClassify;
    private TextView tvTopicName;
    private Button btnTopicNewTopic;
//    我的user bar,用户名，跳转个人资料
    private RelativeLayout userlayout;
    private TextView tvUserName;
    private Button btnUserHomePage;

    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case USER_SET_INFORMATION:
                    Toast.makeText(BottomNavigationBarActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OK:
                    Toast.makeText(BottomNavigationBarActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_NULL:
                    Toast.makeText(BottomNavigationBarActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_FORMAT_ERROR:
                    Toast.makeText(BottomNavigationBarActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(BottomNavigationBarActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation_bar);
        mFragmentManager=getSupportFragmentManager();
        bottomNavigationBar=findViewById(R.id.bottom_navigation_bar);
        topiclayout=findViewById(R.id.topic_top_bar);
        btnTopicClassify=findViewById(R.id.topic_top_bar_classify);
        tvTopicName=findViewById(R.id.topic_top_bar_name);
        btnTopicNewTopic=findViewById(R.id.topic_top_bar_new);
        userlayout=findViewById(R.id.user_top_bar);
        tvUserName=findViewById(R.id.user_top_bar_name);
        btnUserHomePage=findViewById(R.id.user_top_bar_right);
//        userMenuRightFragment= (UserMenuRightFragment) mFragmentManager.findFragmentById(R.id.user_menu_right);

        init();
//        initView();
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

//        点击事件topic,user
        btnTopicClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btnTopicNewTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalUserInfo.userInfo.token !=null){
                Intent intent=new Intent(BottomNavigationBarActivity.this,TopicsNewTopicActivity.class);
                startActivity(intent);
                }else {
                    Message msg1 = new Message();
                    msg1.what = MESSAGE_ERROR;
                    msg1.obj = "登陆后体验更多";
                    msgHandler.sendMessage(msg1);
                }
            }
        });
        btnUserHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalUserInfo.userInfo.token!=null){
                    Intent intent=new Intent(BottomNavigationBarActivity.this,UserHomePageActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Message msg1 = new Message();
                    msg1.what = MESSAGE_ERROR;
                    msg1.obj = "登陆后体验更多";
                    msgHandler.sendMessage(msg1);
                }
            }
        });
    }
//    设置默认开启的fragment
    private void setDefaultFragment(){
//        隐藏user top bar
        userlayout.setVisibility(View.GONE);

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
                userlayout.setVisibility(View.GONE);
                topiclayout.setVisibility(View.VISIBLE);
//                没有topics就创建topics
                if (topicsFragment==null){
                    topicsFragment=new TopicsFragment();
                }
//                是否已加入布局
                if (!topicsFragment.isAdded()) {
                    transaction.add(R.id.fragment_content,topicsFragment);
                }else {
//                    如果userFragment不为空，则隐藏userFragment
                    if (userFragment!=null){
                        transaction.hide(userFragment);
                    }
//                    显示topicsFragment

                    transaction.show(topicsFragment);

                }
                break;
            case 1:
                topiclayout.setVisibility(View.GONE);
                userlayout.setVisibility(View.VISIBLE);

//                没有userFragment就创建
                if (userFragment==null) {
                    userFragment = new UserFragment();
                }
//                是否已加入布局
                if (!userFragment.isAdded()){
                    transaction.add(R.id.fragment_content,userFragment);
                }else {
//                    如果topicsFragment不为空，则隐藏
                    if (topicsFragment!=null){
                        transaction.hide(topicsFragment);
                    }
//                    显示userFragment
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


//    private  void  initView(){
//        drawerLayout=findViewById(R.id.drawer_layout);
//        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(View view, float v) {
//
//            }
//
//            @Override
//            public void onDrawerOpened( View view) {
//
//            }
//
//            @Override
//            public void onDrawerClosed(View view) {
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,Gravity.END);
//            }
//
//            @Override
//            public void onDrawerStateChanged(int i) {
//
//            }
//        });
//        userMenuRightFragment.setDrawerLayout(drawerLayout);
//    }
}
