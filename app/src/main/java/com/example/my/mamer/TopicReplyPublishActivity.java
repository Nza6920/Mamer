package com.example.my.mamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;

public class TopicReplyPublishActivity extends AppCompatActivity {
    private Button btnSure;
    private TextView tvTitle;
    private TextView tvBack;
    private EditText editReplyContent;
    private String strContent;
    private LoadingDraw loadingDraw;

    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    loadingDraw.dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(TopicReplyPublishActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_topic_reply_publish);

        loadingDraw=new LoadingDraw(this);
        init();
    }

    private void init(){
        btnSure=findViewById(R.id.title_btn_next);
        tvTitle=findViewById(R.id.title_tv_name);
        tvBack=findViewById(R.id.title_tv_close);
        editReplyContent=findViewById(R.id.reply_content);

        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvBackPic);
        btnSure.setText("确定");
        tvTitle.setText("评论");

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getReplyContent();
                if (strContent!=null){
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

    private void getReplyContent(){
        strContent=editReplyContent.getText().toString().trim();
    }
//    发布回复
    private void postReply(){
        loadingDraw.show();
        getReplyContent();

        RequestBody requestBody=new FormBody.Builder()
                .add("content1",strContent)
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

                                Intent intent1=new Intent(TopicReplyPublishActivity.this,LoginActivity.class);
                                startActivity(intent1);
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
