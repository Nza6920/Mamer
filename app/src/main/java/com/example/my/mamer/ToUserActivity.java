package com.example.my.mamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.adapter.ToUserTopicAdapter;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.HTTP_DEL_REPLY_OK;
import static com.example.my.mamer.config.Config.HTTP_NOT_FOUND;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.SET_TEXTVIEW;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class ToUserActivity extends AppCompatActivity {

    private TextView tvClose;
    private TextView tvTitle;
    private TextView tvUserName;
    private ImageView userAvatar;
    private TextView tvAttention;
    private TextView tvIntroduction;
    private Intent intent=null;
    private ListView listView;
    private ArrayList<TopicContent> listData=new ArrayList<>();
    private ToUserTopicAdapter mAdapter =null;
    private String userId;
    private LinearLayout layoutAttention;
    private boolean attentionFlag=false;




    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_ERROR:
                    Toast.makeText(ToUserActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    mAdapter.clearData();
                    mAdapter.updateData(listData);
                    break;
                case SET_TEXTVIEW:

                    if ((Boolean)msg.obj) {
                        tvAttention.setText("已关注");
                    }else {
                        tvAttention.setText("关注");
                    }
                default:
                    break;

            }
            return false;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("intent to user:", String.valueOf(getIntent()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_user);
        init();
        mAdapter=new ToUserTopicAdapter(getApplication());
        listView.setAdapter(mAdapter);
        intent=getIntent();
        userId=intent.getStringExtra("userId");
        onDataLoad(1);
        getAttention(userId);

    }

    private void init(){
        tvClose=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        listView=findViewById(R.id.to_user_topic_list);
        tvUserName=findViewById(R.id.to_user_name);
        userAvatar=findViewById(R.id.to_user_avatar);
        tvAttention=findViewById(R.id.to_user_attention);
        tvIntroduction=findViewById(R.id.to_user_introduction);
        layoutAttention=findViewById(R.id.to_user_attention_layout);

        final Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);

        final Runnable setUserInfoRunable=new Runnable() {
            @Override
            public void run() {
                RequestOptions options=new RequestOptions()
                        .error(R.mipmap.ic_image_error)
                        .placeholder(R.mipmap.ic_image_error);
                Glide.with(getApplication())
                        .asBitmap()
                        .load(intent.getStringExtra("userAvatar"))
                        .apply(options)
                        .into(userAvatar);
                tvClose.setBackground(tvBackPic);
                tvTitle.setText(intent.getStringExtra("userName"));
                tvUserName.setText(intent.getStringExtra("userName"));
                if (null==intent.getStringExtra("userIntro")){
                    tvIntroduction.setText("Ta什么也没留下");
                }else {
                    tvIntroduction.setText(intent.getStringExtra("userIntro"));
                }

            }};
        new Thread(){
            public void run(){
                msgHandler.post(setUserInfoRunable);
            }
        }.start();

        layoutAttention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("attention:layout",attentionFlag+"");
                    if (MyApplication.globalUserInfo.token==null){
                        Log.e("attention:layout",attentionFlag+"未登录");
                        Message msg2 = new Message();
                        msg2.what = MESSAGE_ERROR;
                        msg2.obj = "登录以体验更多";
                        msgHandler.sendMessage(msg2);
                    }

                    if (attentionFlag){
                        Log.e("attention:layout",attentionFlag+"已关注");
                        delAttention();
                    }else {
                        Log.e("attention:layout",attentionFlag+"未关注");
                        addAttention();
                    }

                }
            });

    }

    //    数据加载接口
    public void onDataLoad(int page) {
        Log.e("Tag","进入数据获取");
        HttpUtil.sendOkHttpGetUserTopicList(userId,page, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp = null;
                JSONArray jsonArray=null;
                try {
                    jresp = new JSONObject(response.body().string());

                    switch (response.code()) {
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")) {
                                jsonArray=jresp.getJSONArray("data");
                                if (jsonArray==null){
                                    Message msg2 = new Message();
                                    msg2.what = MESSAGE_ERROR;
                                    msg2.obj="您还没有发表过文章呢";
                                    msgHandler.sendMessage(msg2);
                                }else {
                                    int jsonSize=jsonArray.length();
                                    for (int i=0;i<jsonSize;i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        TopicContent topicContent = new TopicContent();
                                        topicContent.setTopicTitle(jsonObject.getString("title"));
                                        topicContent.setTopicExcerpt(jsonObject.getString("excerpt"));
                                        listData.add(topicContent);
                                    }
                                    if (listData!=null){
                                        Message msg3 = new Message();
                                        msg3.what = USER_SET_INFORMATION;
                                        msgHandler.sendMessage(msg3);
                                    }


                                    //       notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容
                                }
                            }
                            break;
                        case HTTP_NOT_FOUND:
                            Message msg5 = new Message();
                            msg5.what = MESSAGE_ERROR;
                            msg5.obj="对不起，我好像出现了一点问题";
                            msgHandler.sendMessage(msg5);
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
// getAttention
    private void getAttention(String userId){
        HttpUtil.sendOkHttpGetAttention(userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp = null;
                JSONArray jsonArray=null;
                try {
                    jresp = new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_DEL_REPLY_OK:
                            attentionFlag=jresp.getBoolean("followed");
                            Message msg3 = new Message();
                            msg3.what = SET_TEXTVIEW;
                            msg3.obj=attentionFlag;
                            msgHandler.sendMessage(msg3);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
//    关注用户
    private void addAttention(){
        RequestBody requestBody=new FormBody.Builder()
                .add("id", userId)
                .build();
        HttpUtil.sendOkHttpPostAddattention(requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_DEL_REPLY_OK:
                            attentionFlag=true;
                            Log.e("attention:",attentionFlag+"");
                            Message msg3 = new Message();
                            msg3.what = SET_TEXTVIEW;
                            msg3.obj=attentionFlag;
                            msgHandler.sendMessage(msg3);
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
//    取消关注
    private void delAttention(){
        RequestBody requestBody=new FormBody.Builder()
                .add("id", userId)
                .build();
        HttpUtil.sendOkHttpDelAttention(requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_DEL_REPLY_OK:
                            attentionFlag=false;
                            Log.e("attention:",attentionFlag+"");
                            Message msg3 = new Message();
                            msg3.what = SET_TEXTVIEW;
                            msg3.obj=attentionFlag;
                            msgHandler.sendMessage(msg3);

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
