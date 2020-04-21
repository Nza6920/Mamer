package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.adapter.TopicLikeAdapter;
import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.util.BaseUtils;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.MyApplication.getContext;
import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.KEYBOARD_DOWN;
import static com.example.my.mamer.config.Config.KEYBOARD_UP;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;
import static com.example.my.mamer.config.Config.SET_TEXTVIEW;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class TopicLikeListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<ReplyUser> listData=new ArrayList<>();
    private TopicLikeAdapter mAdapter;
    private TextView tvBack;
    private TextView tvTitle;
    private LoadingDraw loadingDraw;
    private TextView tvLikeCount;
    private RelativeLayout rlLike;

    private Intent intent=null;


    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case KEYBOARD_UP:
                    BaseUtils.keyboardUpUtil(getContext(), (View) msg.obj);
                    break;
                case KEYBOARD_DOWN:
                    BaseUtils.keyboardDownUtil(getContext(), (View) msg.obj);
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(getContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    mAdapter.clearData();
                    mAdapter.updateData(listData);
                    break;
                case SET_TEXTVIEW:
                    if (msg.obj==null){
                       msg.obj=0;
                    }
                    tvTitle.setText("共"+String.valueOf(msg.obj)+"人点赞");
                    tvLikeCount.setText("点赞("+String.valueOf(msg.obj)+")");
                    break;
                case DISMISS_DIALOG:
                    loadingDraw.dismiss();
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
        setContentView(R.layout.activity_topic_like_list);
        loadingDraw=new LoadingDraw(this);
        intent=getIntent();
        initUI();
        onDataLoad();

        mAdapter=new TopicLikeAdapter(getContext());
        listView.setAdapter(mAdapter);
        initEvent();

    }

    private void initUI(){
        listView=findViewById(R.id.topic_like_list);
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        tvLikeCount=findViewById(R.id.topic_like_count);

        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvBack.setBackground(tvBackPic);


        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //    获取回复列表
    private void onDataLoad(){
        String essayId=intent.getStringExtra("essayId");
        listData.clear();
        loadingDraw.show();
        HttpUtil.sendOkHttpGetTopicParticulars(essayId,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg=new Message();
                msg.what=DISMISS_DIALOG;
                msgHandler.sendMessage(msg);

                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg=new Message();
                msg.what=DISMISS_DIALOG;
                msgHandler.sendMessage(msg);
                JSONObject jresp = null;
                JSONArray jsonArray=null;

                try {
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            Log.e("Tag","话题详情--获取具体数据");
                            ReplyUser replyUser=new ReplyUser();
                            if (jresp.has("voters")){
                                jresp=jresp.getJSONObject("voters");
                                jsonArray=jresp.getJSONArray("data");
                                int count=0;
                                if (jsonArray!=null){
                                    count=jsonArray.length();
                                }
                                if (count!=0){
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        replyUser.setUserId(jsonObject.getString("id"));
                                        replyUser.setUserName(jsonObject.getString("name"));
                                        replyUser.setUserImg(jsonObject.getString("avatar"));
                                        String info=jsonObject.getString("introduction");
                                        if (info.equals("null")){
                                            replyUser.setUserInfo("Ta什么也没留下");
                                        }else {
                                            replyUser.setUserInfo(info);
                                        }
                                        listData.add(replyUser);
                                    }
//处理内容
                                    Log.e("Tag","话题点赞--更新数据");
                                       Message msg1=new Message();
                                       msg1.what=USER_SET_INFORMATION;
                                       msgHandler.sendMessage(msg1);
                                }
                                    Message msg1=new Message();
                                    msg1.what=SET_TEXTVIEW;
                                    msg.obj=jsonArray.length();
                                    msgHandler.sendMessage(msg1);

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
    //    每个itemView的点击事件，点击可以跳到当前回复对象的个人首页
    private void initEvent(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, long l) {
//               已登录：点击头像查看用户/点击内容进行删除|未登录不能进行操作
                if (MyApplication.globalUserInfo.token!=null){
//                点击头像
                    view.findViewById(R.id.like_list_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (MyApplication.globalUserInfo.user.getUserId()!=listData.get(position).getUserId()){
//                        非当前用户评论的可以查看头像
                                Intent intent=new Intent(TopicLikeListActivity.this,ToUserActivity.class);
                                intent.putExtra("userId",listData.get(position).getUserId());
                                intent.putExtra("userAvatar",listData.get(position).getUserImg());
                                intent.putExtra("userName",listData.get(position).getUserName());
                                intent.putExtra("userIntro",listData.get(position).getUserInfo());
                                Log.e("intent:", String.valueOf(intent));
                                startActivity(intent);
                            }
                        }
                    });
                }else {
                    Message msg = new Message();
                    msg.what = MESSAGE_ERROR;
                    msg.obj = "登录以体验更多";
                    msgHandler.sendMessage(msg);
                }
            }
        });
    }

    //    刷新
    public  void  refreshKey(){
        HttpUtil.sendOkHttpRefreshToken(REFRESH_TOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1 = new Message();
                msg1.what = MESSAGE_ERROR;
                msg1.obj = "登录已失效，请重新登录";
                msgHandler.sendMessage(msg1);

                Intent intent1 = new Intent(TopicLikeListActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jrep= new JSONObject(response.body().string());
                    String key=jrep.getString("access_token");
                    String type=jrep.getString("token_type");
                    MyApplication.globalUserInfo.token=key;
                    MyApplication.globalUserInfo.tokenType=type;
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("key",key);
                    editor.putString("type",type);
                    editor.apply();

                    Message msg1 = new Message();
                    msg1.what = MESSAGE_ERROR;
                    msg1.obj = "已刷新，请重试";
                    msgHandler.sendMessage(msg1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
