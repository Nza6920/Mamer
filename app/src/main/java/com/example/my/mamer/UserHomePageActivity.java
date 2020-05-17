package com.example.my.mamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.my.mamer.util.LoadingDraw;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OVERTIME;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;

public class UserHomePageActivity extends AppCompatActivity {
//头像
    private ImageView imgUserAvatar;
//    编辑按钮
    private LinearLayout tvUserEditor;
//    显示内容
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserIntroduction;
    private TextView tvUserCreateTime;
//返回
    private TextView tvBack;
    private LoadingDraw loadingDraw;

    //ui
    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_NULL:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_ERROR:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OVERTIME:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_user_home_page);

        loadingDraw=new LoadingDraw(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init(){
        tvBack=findViewById(R.id.title_tv_close);
        imgUserAvatar=findViewById(R.id.user_home_page_avatar);
        tvUserEditor=findViewById(R.id.user_home_page_editor);
        tvUserName=findViewById(R.id.user_home_page_name);
        tvUserEmail=findViewById(R.id.user_home_page_email);
        tvUserIntroduction=findViewById(R.id.user_home_page_introduction);
        tvUserCreateTime=findViewById(R.id.user_home_page_create_time);

        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvClosePic);
//填充数据
        final Runnable setAvatarRunable=new Runnable() {
            @Override
            public void run() {
                RequestOptions options=new RequestOptions()
                        .error(R.mipmap.ic_image_error)
                        .placeholder(R.mipmap.ic_image_error);
        if (MyApplication.globalUserInfo.user.avatarType.equals("fig")){
            Glide.with(getApplication())
                    .load(MyApplication.globalUserInfo.user.getUserImg())
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource instanceof GifDrawable){
                                ((GifDrawable) resource).setLoopCount(5);
                            }
                            return false;
                        }
                    })
                    .into(imgUserAvatar);
        }else {
            Glide.with(getApplication())
                    .asBitmap()
                    .load(MyApplication.globalUserInfo.user.getUserImg())
                    .apply(options)
                    .into(imgUserAvatar);
        }
            }
        };
        new Thread(){
            public void run(){
                msgHandler.post(setAvatarRunable);
            }
        }.start();
        tvUserName.setText(MyApplication.globalUserInfo.user.getUserName());
        tvUserEmail.setText(MyApplication.globalUserInfo.user.getUserEmail());
        tvUserIntroduction.setText(MyApplication.globalUserInfo.user.getUserIntroduction());
        tvUserCreateTime.setText(MyApplication.globalUserInfo.user.getUserBornDate());

//        监听
//        点击返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserHomePageActivity.this,BottomNavigationBarActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        点击编辑
        tvUserEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserHomePageActivity.this,UserEditorInformationActivity.class);
                startActivity(intent);

            }
        });
    }
}
