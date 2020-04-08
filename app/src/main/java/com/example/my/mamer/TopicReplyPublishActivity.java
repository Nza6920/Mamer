package com.example.my.mamer;

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

import com.example.my.mamer.util.LoadingDraw;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;

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


            }
        });
    }


}
