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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.USER_INFORMATION;

public class UserEditorInformationActivity extends AppCompatActivity {
    private static final MediaType XWWW=MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
//    头像
    private CircleImageView imgUserAvatar;
    private String userAvatar;
//    头像按钮
    private TextView tvUserEditor;
//    个人信息
    private EditText etUserName;
    private String userName;
    private TextView etUserEmail;
    private EditText etUserIntroduction;
    private String userInformation;
//    返回按钮
    private TextView tvBack;
    private Button btnFinish;

    private LoadingDraw loadingDraw;

//    UI
    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                default:
                    break;
            }
        }
};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edito_information);

        loadingDraw=new LoadingDraw(this);
        init();
    }

    private void init(){
        tvBack=findViewById(R.id.title_tv_close);
        btnFinish=findViewById(R.id.title_btn_next);
        imgUserAvatar=findViewById(R.id.user_information_avatar);
        tvUserEditor=findViewById(R.id.user_information_editor);
        etUserName=findViewById(R.id.user_information_name);
        etUserEmail=findViewById(R.id.user_information_email);
        etUserIntroduction=findViewById(R.id.user_information_introduction);

        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvBack.setBackground(tvClosePic);
        btnFinish.setText("完成");
        new Thread(){
            public void run(){
                msgHandler.post(setImgRunable);
            }
        }.start();
        etUserName.setText(User.getUserName());
        etUserEmail.setText(User.getUserEmail());
        etUserIntroduction.setText(User.getUserIntroduction());

//        点击事件
//        返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserEditorInformationActivity.this,UserHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        提交
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getEditString();
                    patchInformation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
//    获取用户输入值
    private void getEditString(){
        userName=etUserName.getText().toString().trim();
        userInformation=etUserIntroduction.getText().toString().trim();
    }
//    PATC提交信息
    private void patchInformation() throws JSONException {
        JSONObject jsonParam=new JSONObject();
        jsonParam.put("name",userName);
        jsonParam.put("introduction",userInformation);
        String jsonStr=jsonParam.toString();

        RequestBody requestBody=RequestBody.create(XWWW,jsonStr);
        HttpUtil.sendOkHttpRequestPatch(USER_INFORMATION,requestBody, new Callback() {
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

                            Intent intent=new Intent(UserEditorInformationActivity.this,UserHomePageActivity.class);
                            User.setUserId(jresp.getString("id"));
                            User.setUserName(jresp.getString("name"));
                            User.setUserIntroduction(jresp.getString("introduction"));
                            User.setUserBornDate(jresp.getString("bound_phone"));
                            startActivity(intent);
                            finish();
//                            422
                        case HTTP_USER_NULL:
                            Message msg4=new Message();
                            msg4.what=DISMISS_DIALOG;
                            msg4.obj=loadingDraw;
                            msgHandler.sendMessage(msg4);

                            String jrespStr=jresp.getString("errors");
                            JSONObject  errorStr=jresp.getJSONObject(jrespStr);
                            if (errorStr.has("name")){
                                Message msg5=new Message();
                                msg5.what=response.code();
                                msg5.obj=errorStr.getString("name");
                                msgHandler.sendMessage(msg5);
                            }
                            break;
//                            401,令牌失效，重新请求
                        case HTTP_USER_ERROR:
                            Message msg6=new Message();
                            msg6.what=DISMISS_DIALOG;
                            msg6.obj=loadingDraw;
                            msgHandler.sendMessage(msg6);

                            Authenticator authenticator=new Authenticator() {
                                @Override
                                public Request authenticate(Route route, Response response) throws IOException {
//    刷新token
                                    return response.request().newBuilder().addHeader("Authorization", User.getUserPassKey_type()+User.getUserPassKey()).build();
                                }
                            };


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    //加载头像
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
}
