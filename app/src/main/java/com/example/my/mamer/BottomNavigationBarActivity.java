package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.example.my.mamer.bean.TopicDIvid;
import com.example.my.mamer.config.GlobalTopicReply;
import com.example.my.mamer.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.MyApplication.getContext;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;

public class BottomNavigationBarActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {

    private FragmentManager mFragmentManager;
    private BottomNavigationBar bottomNavigationBar;
//首页推荐消息和我的
    private TopicsFragment topicsFragment;
    private UserFragment userFragment;
    private NotificationFragment notificationFragment;
    private RecommendFragment recommendFragment;

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
//    推荐
    private RelativeLayout recommendlayout;
//    消息,未读消息数
    private RelativeLayout notificationlayout;
    private String notificationNum;
//    我的user bar,用户名，跳转个人资料
    private RelativeLayout userlayout;
    private TextView tvUserName;
    private Button btnUserHomePage;

    private Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_ERROR:
                    Toast.makeText(BottomNavigationBarActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });


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
        recommendlayout=findViewById(R.id.recommend_top_bar);
        notificationlayout=findViewById(R.id.notification_top_bar);
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
                .addItem(new BottomNavigationItem(R.mipmap.ic_recommend,"推荐")
                        .setActiveColor("#12A3E9")
                        .setInActiveColor("#999999")
                        .setBadgeItem(textBadgeItem)
                        .setInactiveIconResource(R.mipmap.ic_recommend_none))
                .addItem(new BottomNavigationItem(R.mipmap.ic_notification,"信息")
                        .setActiveColor("#12A3E9")
                        .setInActiveColor("#999999")
                        .setBadgeItem(textBadgeItem)
                        .setInactiveIconResource(R.mipmap.ic_notification_none))
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
//       消息(右上角)-->新建话题
        btnTopicNewTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.globalUserInfo.token !=null){
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("tagId","2");
                    editor.apply();

                Intent intent=new Intent(BottomNavigationBarActivity.this,TopicActivity.class);
                startActivity(intent);
                }else {
                    Message msg1 = new Message();
                    msg1.what = MESSAGE_ERROR;
                    msg1.obj = "登陆后体验更多";
                    msgHandler.sendMessage(msg1);
                }
            }
        });
//        我的(右上角)-->个人信息界面
        btnUserHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.globalUserInfo.token!=null){
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
        recommendlayout.setVisibility(View.GONE);
        notificationlayout.setVisibility(View.GONE);

        mFragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=mFragmentManager.beginTransaction();
        topicsFragment=new TopicsFragment();
//        往activity添加fragment
        transaction.add(R.id.fragment_content,topicsFragment);
        transaction.commit();
    }

//    底部导航栏-->切换事件
    @Override
    public void onTabSelected(int position) {
//        每次点击赋值
        lastSelectionedPosition=position;
//        开启事务
        transaction=mFragmentManager.beginTransaction();
        hideFragment(transaction);
        switch (position){
            case 0:
                recommendlayout.setVisibility(View.GONE);
                notificationlayout.setVisibility(View.GONE);
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
                    if (recommendFragment !=null){
                        transaction.hide(recommendFragment);
                    }
                    if (notificationFragment !=null){
                        transaction.hide(notificationFragment);
                    }
                    if (userFragment!=null){
                        transaction.hide(userFragment);
                    }
//                    显示topicsFragment
                    transaction.show(topicsFragment);

                }
                break;
            case 1:
                topiclayout.setVisibility(View.GONE);
                notificationlayout.setVisibility(View.GONE);
                userlayout.setVisibility(View.GONE);
                recommendlayout.setVisibility(View.VISIBLE);
//                没有topics就创建topics
                if (recommendFragment==null){
                    recommendFragment=new RecommendFragment();
                }
                if (!recommendFragment.isAdded()) {
                    transaction.add(R.id.fragment_content,recommendFragment);
                }else {
                    if (topicsFragment !=null){
                        transaction.hide(topicsFragment);
                    }
                    if (notificationFragment !=null){
                        transaction.hide(notificationFragment);
                    }
                    if (userFragment!=null){
                        transaction.hide(userFragment);
                    }
                    transaction.show(recommendFragment);

                }
                break;
            case 2:
                topiclayout.setVisibility(View.GONE);
                userlayout.setVisibility(View.GONE);
                recommendlayout.setVisibility(View.GONE);
                notificationlayout.setVisibility(View.VISIBLE);
                if (notificationFragment ==null){
                    notificationFragment =new NotificationFragment();
                }
                if (!notificationFragment.isAdded()){
                    transaction.add(R.id.fragment_content, notificationFragment);
                }else {
                    if (topicsFragment!=null){
                        transaction.hide(topicsFragment);
                    }
                    if (recommendFragment !=null){
                        transaction.hide(recommendFragment);
                    }
                    if (userFragment!=null){
                        transaction.hide(userFragment);
                    }
                    transaction.show(notificationFragment);
                }
                break;
            case 3:
                topiclayout.setVisibility(View.GONE);
                notificationlayout.setVisibility(View.GONE);
                recommendlayout.setVisibility(View.GONE);
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
                    if (recommendFragment !=null){
                        transaction.hide(recommendFragment);
                    }
                    if (notificationFragment !=null){
                        transaction.hide(notificationFragment);
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
        if (recommendFragment !=null){
            transaction.hide(recommendFragment);
        }
        if (notificationFragment !=null){
            transaction.hide(notificationFragment);
        }
        if (userFragment!=null){
            transaction.hide(userFragment);
        }
    }

//    获取未读消息数，循环间隔发起
    @Override
    protected void onResume() {
//        Log.e("Tag",":onResume()");
        super.onResume();
//        getNotification();
    }

    //    底部导航-->消息界面
    private TextBadgeItem textBadgeItem;
//    展示消息点
    private void showNumberAndShape(){
        String num=getNotificationNum();
        if (num.equals(" ")&&num.equals("0")){
            //        消息
            textBadgeItem=new TextBadgeItem()
//                显示的文本
                    .setText(num)
//                文本颜色
                    .setTextColor("#ffffff")
//                圆环宽度
                    .setBorderWidth(5)
//               圆环颜色
//        .setBorderColor(Color.parseColor("#fff"))
//                背景颜色
                    .setBackgroundColor("#ff0008")
//                选中是否隐藏
                    .setHideOnSelect(false)
//                隐藏与动画的过渡时间
//        .setAnimationDuration(300)
//                位置，默认右上角
                    .setGravity(Gravity.RIGHT|Gravity.TOP);
        }
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

//    获取通知消息数
    private void getNotification(){
        Log.i("getNotification","进入获取,此处不需要做权限");

        if(MyApplication.globalUserInfo.token.equals(""))
            HttpUtil.sendOkHttpGetNotificationState(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg1 = new Message();
                    msg1.what = MESSAGE_ERROR;
                    msg1.obj = "服务器异常,请检查网络";
                    msgHandler.sendMessage(msg1);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    JSONObject jresp=null;
                    try {
                        jresp=new JSONObject(response.body().string());
                        switch (response.code()){
                            case HTTP_USER_GET_INFORMATION:
                                if (jresp.has("unread_count")){
                                    setNotificationNum(jresp.getString("unread_count"));
                                    showNumberAndShape();
                                }
                                break;
                                default:
                                    break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


    }

    public void setNotificationNum(String notificationNum) {
        this.notificationNum = notificationNum;
    }

    public String getNotificationNum() {
        return notificationNum;
    }

//    获取话题分类
    private void getTopicDivid(){
        HttpUtil.sendOkHttpGetTopicDivid(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1 = new Message();
                msg1.what = MESSAGE_ERROR;
                msg1.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp=null;
                JSONArray jsonArray=null;
                try {
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")){
                                jsonArray=jresp.getJSONArray("data");
                                int jsonSize=jsonArray.length();
                                for (int i = 0; i < jsonSize; i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    TopicDIvid topicDIvid=new TopicDIvid();
                                    topicDIvid.setCategoryId(jsonObject.getString("id"));
                                    topicDIvid.setCategoryName(jsonObject.getString("name"));
                                    topicDIvid.setCategoryDesc(jsonObject.getString("description"));
                                    GlobalTopicReply.reply.topicDivid.add(topicDIvid);
                                }
                            }
                            break;
                            default:
                                break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
