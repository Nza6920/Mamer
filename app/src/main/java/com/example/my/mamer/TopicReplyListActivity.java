package com.example.my.mamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import static com.example.my.mamer.config.Config.HTTP_DEL_REPLY_OK;
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
    private Intent intent=null;
    private AlertDialog.Builder delDialogBuilder;
    private SharedPreferences.Editor editor;
    private TextView tvDel;
    private String userId;
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
                    String username=intent.getStringExtra("topicUserName");
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
        intent=getIntent();
        initUI();
        onDataLoad(1);

        mAdapter=new TopicReplyAdapter(getContext(),listData);
        listView.setAdapter(mAdapter);
        initEvent();

        editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean("topicReplyListToTopicParticulars",false);
        editor.apply();

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
//               已登录：点击头像查看用户/点击内容进行删除|未登录不能进行操作
                if (MyApplication.globalUserInfo.token!=null){
//                点击头像
                    view.findViewById(R.id.reply_user_avatar_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (MyApplication.globalUserInfo.user.getUserId()!=listData.get(position).getUserId()){
//                        非当前用户评论的可以查看头像
                                Intent intent=new Intent(TopicReplyListActivity.this,ToUserActivity.class);
                                intent.putExtra("userId",listData.get(position).getUserId());
                                intent.putExtra("userAvatar",listData.get(position).getUserImg());
                                intent.putExtra("userName",listData.get(position).getUserName());
                                intent.putExtra("userIntro",listData.get(position).getUserInfo());
                                Log.e("intent:", String.valueOf(intent));
                                startActivity(intent);
                            }

                        }
                    });
//                点击内容
                    view.findViewById(R.id.reply_user_layout).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                    当前用户的帖子，点击整个contentview可以删除任意一条评论（包括自己的）||或者当前用户评论的，可以删除
                            if ((MyApplication.globalUserInfo.user.getUserId()==intent.getStringExtra("topicUserId"))||(MyApplication.globalUserInfo.user.getUserId()==listData.get(position).getUserId())){
//                        删除前进行提示
                                delDialogBuilder=new AlertDialog.Builder(TopicReplyListActivity.this)
                                        .setMessage("删除后无法恢复")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                delReply( listData.get(position).getEssayId(),listData.get(position).getReplyId());
                                            }
                                        })
                                        .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                delDialogBuilder.show();
                            }
                        }
                    });
                }
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
                        msg2.obj = "评论成功";
                        msgHandler.sendMessage(msg2);

                        editor.putBoolean("topicReplyListToTopicParticulars",true);
                        editor.apply();
                        finish();
                        break;
//                        401
                    case HTTP_USER_ERROR:
                        refreshKey();
                       break;
                       default:
                           break;

                }
            }
        });
    }
    //    删除回复
    private void delReply(String essayId, String replyId){
        HttpUtil.sendOkHttpDelReply(essayId, replyId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2=new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                if (responseStr.isEmpty()) {
                    try {
                        JSONObject jresp = new JSONObject(responseStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                switch (response.code()) {
                    case HTTP_DEL_REPLY_OK:
                        Message msg4 = new Message();
                        msg4.what = MESSAGE_ERROR;
                        msg4.obj = "删除成功";
                        msgHandler.sendMessage(msg4);

                        onDataLoad(1);
                        break;
                    case HTTP_USER_ERROR:
                        refreshKey();
                        break;
                    default:
                        break;
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

                Intent intent1 = new Intent(TopicReplyListActivity.this, LoginActivity.class);
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
