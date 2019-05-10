package com.example.my.mamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.adapter.TopicReplyAdapter;
import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.config.GlobalUserInfo;
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

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_DEL_REPLY_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class UserSelfReplyActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<ReplyUser> listData=new ArrayList<>();
    private TopicReplyAdapter mAdapter;
    private TextView tvBack;
    private TextView tvTitle;
    private LoadingDraw loadingDraw;

    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(UserSelfReplyActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    mAdapter.notifyDataSetChanged();
                    Log.e("Tag","数据刷新完成");
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_self_reply_list);
        loadingDraw=new LoadingDraw(this);
        initUI();
        onDataLoad();
        mAdapter=new TopicReplyAdapter(getApplicationContext(),getListData());
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
        listView=findViewById(R.id.user_self_reply_list);
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);

        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvBackPic);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserSelfReplyActivity.this,BottomNavigationBarActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tvTitle.setText("我的回复");
        tvTitle.setTextSize(25);
    }

    private void onDataLoad(){
        loadingDraw.show();

        HttpUtil.sendOkHttpGetUserReplyList(GlobalUserInfo.userInfo.user.getUserId(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1 = new Message();
                msg1.what = DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

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
                            Message msg1 = new Message();
                            msg1.what = DISMISS_DIALOG;
                            msg1.obj=loadingDraw;
                            msgHandler.sendMessage(msg1);

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
                                            replyUser.setUserImg(userStr.getString("avatar"));
                                        }
                                        if (jsonObject.has("topic")){
                                            JSONObject topicStr=jsonObject.getJSONObject("topic");
                                            replyUser.setUserName(topicStr.getString("title"));
                                        }
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
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private void initEvent(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, long l) {
                AlertDialog.Builder builder=new AlertDialog.Builder(UserSelfReplyActivity.this);
                builder.setIcon(R.drawable.ic_delete_forever_black_24dp)
                        .setMessage("确定删除吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                view.setVisibility(View.GONE);
                                delReply( listData.get(position).getEssayId(), listData.get(position).getReplyId());

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//不做处理
                            }
                        });
                builder.show();
            }
        });
    }
    private void delReply(String essayId,String replyId){
        loadingDraw.show();

        HttpUtil.sendOkHttpDelReply(essayId, replyId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2=new Message();
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

                            Message msg3=new Message();
                            msg3.what=DISMISS_DIALOG;
                            msg3.obj=loadingDraw;
                            msgHandler.sendMessage(msg3);

                            Message msg4=new Message();
                            msg4.what=MESSAGE_ERROR;
                            msg4.obj="删除成功";
                            msgHandler.sendMessage(msg4);
                            break;
                        case HTTP_USER_ERROR:
                            Message msg5=new Message();
                            msg5.what=DISMISS_DIALOG;
                            msg5.obj=loadingDraw;
                            msgHandler.sendMessage(msg5);

                            HttpUtil.sendOkHttpRefreshToken(REFRESH_TOKEN, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message msg1 = new Message();
                                    msg1.what = MESSAGE_ERROR;
                                    msg1.obj = "登录已失效，请重新登录";
                                    msgHandler.sendMessage(msg1);

                                    Intent intent1=new Intent(UserSelfReplyActivity.this,LoginActivity.class);
                                    startActivity(intent1);
                                    finish();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        JSONObject jrep= new JSONObject(response.body().string());

                                        GlobalUserInfo.userInfo.token=jrep.getString("access_token");
                                        GlobalUserInfo.userInfo.tokenType=jrep.getString("token_type");

                                        Message msg1 = new Message();
                                        msg1.what = MESSAGE_ERROR;
                                        msg1.obj = "已刷新，请重试";
                                        msgHandler.sendMessage(msg1);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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
