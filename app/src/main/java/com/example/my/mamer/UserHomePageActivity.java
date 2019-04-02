package com.example.my.mamer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.config.User;
import com.example.my.mamer.util.CircleImageView;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OVERTIME;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.USER_INFORMATION;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class UserHomePageActivity extends AppCompatActivity {
//头像
    private CircleImageView imgUserAvatar;
//    编辑按钮
    private TextView tvUserEditor;
//    显示内容
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserIntroduction;
    private TextView tvUserCreateTime;
//返回
    private TextView tvBack;
    private LoadingDraw loadingDraw;

    //ui
    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
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
                case USER_SET_INFORMATION:
                    tvUserName.setText(User.getUserName());
                    tvUserEmail.setText(User.getUserEmail());
                    if (User.getUserIntroduction()==null){
                        tvUserIntroduction.setText("Tomorrow will be better！");
                    }else {
                        tvUserIntroduction.setText(User.getUserIntroduction());

                    }
                    tvUserCreateTime.setText(User.getUserBornDate());
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

        loadingDraw=new LoadingDraw(this);
        try {
                getInformationRequest();

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
//
        imgUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        点击编辑
        tvUserEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserHomePageActivity.this,UserEditorInformationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
//    GET方式
    private void getInformationRequest() throws JSONException {

        HttpUtil.sendOkHttpRequestGet(USER_INFORMATION, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2=new Message();
                msg2.what=MESSAGE_ERROR;
                msg2.obj="服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            Message msg3=new Message();
                            msg3.what=DISMISS_DIALOG;
                            msg3.obj=loadingDraw;
                            msgHandler.sendMessage(msg3);

                            User.setUserId(jresp.getString("id"));
                            User.setUserName(jresp.getString("name"));
                            User.setUserEmail(jresp.getString("email"));
//                            默认头像
                            if (jresp.has("avatar")){
                                try {
                                    JSONObject imgCode=jresp.getJSONObject("avatar");
                                    if (imgCode.has("encoded")){
                                        User.setUserImg(imgCode.getString("encoded"));
                                        new Thread(){
                                            public void run(){
                                                msgHandler.post(setImgRunable);

                                            }
                                        }.start();
                                    }
                                }catch (Exception e){
//                                    给的网址
                                    String userAvatar=jresp.getString("avatar");
                                    User.setUserImgAvatar(userAvatar);
                                    Log.e("Tag",userAvatar);
                                    getAvatarRequest();
                                }
                            }
                            User.setUserIntroduction(jresp.getString("introduction"));
                            User.setBoundPhone(jresp.getBoolean("bound_phone"));
                            User.setEmail_verified(jresp.getBoolean("email_verified"));
                            User.setUserBornDate(jresp.getString("created_at"));

                            new Thread(){
                                public void run(){
                                    Message msg5=new Message();
                                    msg5.what=USER_SET_INFORMATION;
                                    msgHandler.sendMessage(msg5);
                                }
                            }.start();



                            break;
//                            令牌失效，重新请求
                        case HTTP_USER_ERROR:
                            Message msg4=new Message();
                            msg4.what=DISMISS_DIALOG;
                            msg4.obj=loadingDraw;
                            msgHandler.sendMessage(msg4);

                            Authenticator authenticator=new Authenticator() {
                                @Override
                                public Request authenticate( Route route, Response response) throws IOException {
//    刷新token
                                    return response.request().newBuilder().addHeader("Authorization", User.getUserPassKey_type()+User.getUserPassKey()).build();
                                }
                            };
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
//    加载头像
    private Bitmap loadPicCodeImg(String bicCodes){

        Bitmap bitmap=null;
        try {
            byte[] bitmapArray=Base64.decode(bicCodes.split(",")[1],Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
            imgUserAvatar.setBitmap(bitmap);
            imgUserAvatar.setmWidth(bitmap.getWidth());
            imgUserAvatar.setmHeight(bitmap.getHeight());
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    Runnable setImgRunable=new Runnable() {
        @Override
        public void run() {
            imgUserAvatar.setImageBitmap(loadPicCodeImg(User.getUserImg()));
        }
    };
//    获取修改后的头像
    private void getAvatarRequest(){
        HttpUtil.sendOkHttpRequestAvatar(User.getUserImgAvatar(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2=new Message();
                msg2.what=MESSAGE_ERROR;
                msg2.obj="服务器异常,获取头像失败(请检查网络)";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg3=new Message();
                msg3.what=DISMISS_DIALOG;
                msg3.obj=loadingDraw;
                msgHandler.sendMessage(msg3);

                byte[] bytes=(byte[])response.body().bytes();
                final Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imgUserAvatar.setBitmap(bitmap);
                imgUserAvatar.setmWidth(bitmap.getWidth());
                imgUserAvatar.setmHeight(bitmap.getHeight());
                User.setUserImgBitmap(bitmap);
                Log.e("Tag", String.valueOf(User.getUserImgBitmap()));
                final Runnable setAvatarRunable=new Runnable() {
                    @Override
                    public void run() {
                        imgUserAvatar.setImageBitmap(bitmap);
                    }
                };
                new Thread(){
                    public void run(){
                        msgHandler.post(setAvatarRunable);
                    }
                }.start();

            }
        });
    }


}
