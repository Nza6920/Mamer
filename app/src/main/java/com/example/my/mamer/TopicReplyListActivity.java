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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.adapter.TopicReplyAdapter;
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
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.MyApplication.getContext;
import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.KEYBOARD_DOWN;
import static com.example.my.mamer.config.Config.KEYBOARD_UP;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class TopicReplyListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<ReplyUser> listData=new ArrayList<>();
    private TopicReplyAdapter mAdapter;
    private TextView tvBack;
    private TextView tvTitle;
    private LoadingDraw loadingDraw;
    private TextView tvReplyCount;
    private LinearLayout toReplyLayout;
    private EditText tvReplyContent;
    private TextView tvReplySend;
    private String strReplyContent;
    private LinearLayout tvReplyContent_;
    private TextView tvTopicUserName;
    private LinearLayout layoutReply;
    private int current_page;
    private int total_pages;

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
                    String username=getIntent().getStringExtra("topicUserName");
                    if (username!=null){
                        tvTopicUserName.setText(username+"(作者)");
                    }
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
        setContentView(R.layout.activity_topic_reply_list);
        loadingDraw=new LoadingDraw(this);
        initUI();
        onDataLoad(1);

        mAdapter=new TopicReplyAdapter(getContext(),listData);
        listView.setAdapter(mAdapter);
        initEvent();

    }
    public ArrayList<ReplyUser> getListData() {
        return listData;
    }

    public void setListData(ArrayList<ReplyUser> listData) {
        this.listData = listData;
    }

    private void initUI(){
        listView=findViewById(R.id.topic_reply_list);
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        tvReplyCount=findViewById(R.id.topic_reply_count);
        toReplyLayout=findViewById(R.id.to_reply);
        tvReplyContent=findViewById(R.id.to_reply_content);
        tvReplySend=findViewById(R.id.to_reply_send);
        tvReplyContent_=findViewById(R.id.to_reply_content_);
        tvTopicUserName=findViewById(R.id.reply_topic_user_name);
        layoutReply=findViewById(R.id.reply_layout);

        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvBack.setBackground(tvBackPic);
        if (MyApplication.globalUserInfo.token==null){
            toReplyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Message message=new Message();
                        message.what=MESSAGE_ERROR;
                        message.obj="登陆后体验更多";
                        msgHandler.sendMessage(message);
                }
            });
            tvReplyContent_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message=new Message();
                    message.what=MESSAGE_ERROR;
                    message.obj="登陆后体验更多";
                    msgHandler.sendMessage(message);
                }
            });
            tvReplySend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message=new Message();
                    message.what=MESSAGE_ERROR;
                    message.obj="登陆后体验更多";
                    msgHandler.sendMessage(message);
                }
            });
        }else {
            Message msg=new Message();
            msg.what=USER_SET_INFORMATION;
            msgHandler.sendMessage(msg);

            tvReplyContent_.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvReplyContent_.setVisibility(View.GONE);
                    layoutReply.setVisibility(View.VISIBLE);
                    layoutReply.setFocusable(true);
                    layoutReply.setFocusableInTouchMode(true);

                }
            });
            tvReplyContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        Message msg=new Message();
                        msg.what=KEYBOARD_UP;
                        msg.obj=tvReplyContent;
                        msgHandler.sendMessage(msg);
                    } else {
                        layoutReply.setVisibility(View.GONE);
                        tvReplyContent_.setVisibility(View.VISIBLE);
                        Message msg=new Message();
                        msg.what=KEYBOARD_DOWN;
                        msg.obj=tvReplyContent;
                        msgHandler.sendMessage(msg);

                    }
                }
            });
            tvReplySend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getReplyContent()!=null){
                        postReply();
                    }else {
                        Message message=new Message();
                        message.what=MESSAGE_ERROR;
                        message.obj="内容不能为空噢";
                        msgHandler.sendMessage(message);
                    }
                }
            });
        }
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

//    获取回复列表
    private void onDataLoad(int page){
        HttpUtil.sendOkHttpGetTopicReplyList(MyApplication.globalTopicReply.reply.replyUser.getEssayId(),page, new Callback() {
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
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:

                            if (jresp.has("data")){
                                jsonArray=jresp.getJSONArray("data");
//                               有评论
                                if (jsonArray!=null){
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                        ReplyUser replyUser=new ReplyUser();
                                        replyUser.setReplyId(jsonObject.getString("id"));
                                        replyUser.setEssayId(jsonObject.getString("topic_id"));
                                        replyUser.setContent(jsonObject.getString("content"));
                                        if (jsonObject.has("user")){
                                            JSONObject userStr=jsonObject.getJSONObject("user");
                                            replyUser.setUserId(userStr.getString("id"));
                                            replyUser.setUserImg(userStr.getString("avatar"));
                                            replyUser.setUserName(userStr.getString("name"));
                                        }
                                        if (jsonObject.has("topic")){
                                            JSONObject topicStr=jsonObject.getJSONObject("topic");
                                            replyUser.setUserName(topicStr.getString("title"));
                                        }
                                        replyUser.setTagReplyRole("");
                                        listData.add(replyUser);
                                    }

                                    final Runnable setAvatarRunable=new Runnable() {
                                        @Override
                                        public void run() {
                                            mAdapter.notifyDataSetChanged();
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setAvatarRunable);
                                        }
                                    }.start();

                                }
                            }
                            if (jresp.has("meta")){
                                jresp=jresp.getJSONObject("meta");
                                if (jresp.has("pagination")){
                                    jresp=jresp.getJSONObject("pagination");
                                    final String count=jresp.getString("total");
                                    current_page=jresp.getInt("current_page");
                                    total_pages=jresp.getInt("total_pages");
                                    final Runnable setCountRunable=new Runnable() {
                                        @Override
                                        public void run() {
                                            tvTitle.setText("共"+count+"条评论");
                                            tvReplyCount.setText("评论("+count+")");
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setCountRunable);
                                        }
                                    }.start();
                                }
                            }
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

                Intent intent=new Intent(TopicReplyListActivity.this,ToUserActivity.class);
                intent.putExtra("userId",listData.get(position).getUserId());
                intent.putExtra("userAvatar",listData.get(position).getUserImg());
                intent.putExtra("userName",listData.get(position).getUserName());
                intent.putExtra("userIntro",listData.get(position).getUserInfo());
                Log.e("intent:", String.valueOf(intent));
                startActivity(intent);
            }
        });
    }

    private String  getReplyContent(){
        strReplyContent=tvReplyContent.getText().toString().trim();
        return strReplyContent;
    }
    //    发布回复
    private void postReply(){
        RequestBody requestBody=new FormBody.Builder()
                .add("content1", getReplyContent())
                .build();
        HttpUtil.sendOkHttpPostReply(MyApplication.globalTopicReply.reply.replyUser.getEssayId(), requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                switch (response.code()){
                    case HTTP_OK:
                        Message msg1=new Message();
                        msg1.what=DISMISS_DIALOG;
                        msg1.obj=loadingDraw;
                        msgHandler.sendMessage(msg1);

                        Message msg2 = new Message();
                        msg2.what = MESSAGE_ERROR;
                        msg2.obj = "回复成功";
                        msgHandler.sendMessage(msg2);

                        finish();
                        break;
                    case HTTP_USER_ERROR:
                        Message msg4=new Message();
                        msg4.what=DISMISS_DIALOG;
                        msg4.obj=loadingDraw;
                        msgHandler.sendMessage(msg4);

                        HttpUtil.sendOkHttpRefreshToken(REFRESH_TOKEN, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Message msg1 = new Message();
                                msg1.what = MESSAGE_ERROR;
                                msg1.obj = "登录已失效，请重新登录";
                                msgHandler.sendMessage(msg1);

                                finish();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    JSONObject jrep= new JSONObject(response.body().string());

                                    MyApplication.globalUserInfo.token=jrep.getString("access_token");
                                    MyApplication.globalUserInfo.tokenType=jrep.getString("token_type");

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
        });
    }
}
