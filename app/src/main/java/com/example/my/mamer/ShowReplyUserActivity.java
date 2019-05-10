package com.example.my.mamer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ShowReplyUserActivity extends AppCompatActivity {

    //头像
    private ImageView imgUserAvatar;
    //    显示内容
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserIntroduction;
    private LinearLayout layoutTime;


    //ui
    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        init();

    }

    private void init(){
        imgUserAvatar=findViewById(R.id.user_home_page_avatar);
        tvUserName=findViewById(R.id.user_home_page_name);
        tvUserEmail=findViewById(R.id.user_home_page_email);
        tvUserIntroduction=findViewById(R.id.user_home_page_introduction);
        layoutTime=findViewById(R.id.user_home_page_time_layout);

        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
//填充数据
        final Runnable setAvatarRunable=new Runnable() {
            @Override
            public void run() {
                RequestOptions options=new RequestOptions()
                        .error(R.mipmap.ic_image_error)
                        .placeholder(R.mipmap.ic_image_error);
                Glide.with(getApplication())
                        .asBitmap()
                        .load(prefs.getString("userImg",null))
                        .apply(options)
                        .into(imgUserAvatar);
                tvUserName.setText(prefs.getString("userName",null));
                tvUserEmail.setText(prefs.getString("email",null));
                tvUserIntroduction.setText(prefs.getString("userInfo",null));
                layoutTime.setVisibility(View.GONE);

            }
        };
        new Thread(){
            public void run(){
                msgHandler.post(setAvatarRunable);
            }
        }.start();



    }
}
