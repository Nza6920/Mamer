package com.example.my.mamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.bean.User;
import com.example.my.mamer.config.GlobalUserInfo;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;
import com.example.my.mamer.util.NCopyPaste;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_OVERTIME;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.JSON;
import static com.example.my.mamer.config.Config.LOGIN;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.USER_INFORMATION;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class LoginActivity extends AppCompatActivity {
    private LoadingDraw loadingDraw;
//    返回按钮
    private TextView tvClose;
//    用户名
    private EditText etUName;
    private String uName;
//    密码
    private EditText etPas;
    private String pas;
//    登录按钮
    private Button btnLogin;
//ui
    private final Handler msgHandler=new Handler(){
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case DISMISS_DIALOG:
                ((LoadingDraw)msg.obj).dismiss();
                break;
            case MESSAGE_ERROR:
                Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                break;
            case HTTP_USER_NULL:
                Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                break;
            case HTTP_USER_ERROR:
                Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                break;
            case HTTP_OVERTIME:
                Toast.makeText(LoginActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingDraw=new LoadingDraw(this);
        init();
    }
    private void init(){
        tvClose=findViewById(R.id.title_tv_close);
        etUName=findViewById(R.id.login_username);
        etPas=findViewById(R.id.login_password);
        btnLogin=findViewById(R.id.login_btn);

//        设置
        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvClose.setBackground(tvClosePic);

        final String userName="请输入手机号或邮箱";
        String passWord="请输入密码";
        setHintAll(etUName,userName);
        setHintAll(etPas,passWord);

        etUName.setCustomSelectionActionModeCallback(new NCopyPaste());
        etUName.setLongClickable(false);
        etPas.setTextIsSelectable(false);
        etPas.setCustomSelectionActionModeCallback(new NCopyPaste());
        etPas.setLongClickable(false);

        btnLogin.getBackground().setAlpha(111);
//        监听，点击事件
//        返回按钮
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        密码
        etPas.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getEditString();
                if (TextUtils.isEmpty(userName)) {
                    btnLogin.getBackground().setAlpha(111);
                    btnLogin.setEnabled(false);
                }else if (pas.length()<5) {
                    btnLogin.getBackground().setAlpha(111);
                    btnLogin.setEnabled(false);
                }else {
                    btnLogin.getBackground().setAlpha(255);
                    btnLogin.setEnabled(true);
                }

            }
        });
//        登录按钮
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDraw.show();
                try {
                    getEditString();
                    postInformation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
//    hint设置
    private void setHintAll(EditText editText,String s){
        SpannableString sHint=new SpannableString(s);
        AbsoluteSizeSpan tSize =new AbsoluteSizeSpan(15,true);
        sHint.setSpan(tSize,0,sHint.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(sHint);
    }
//    获取用户输入的值
    private void getEditString(){
        uName=etUName.getText().toString().trim();
        pas=etPas.getText().toString().trim();
    }
//    post
    private void postInformation() throws JSONException {
        loadingDraw.show();
        JSONObject jsonParam=new JSONObject();
        jsonParam.put("username",uName);
        jsonParam.put("password",pas);
        String jsonStr=jsonParam.toString();

        RequestBody requestBody=RequestBody.create(JSON,jsonStr);
        HttpUtil.sendOkHttpRequest(LOGIN, requestBody, new Callback() {
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
                        case HTTP_OK:
                            Message msg3=new Message();
                            msg3.what=DISMISS_DIALOG;
                            msg3.obj=loadingDraw;
                            msgHandler.sendMessage(msg3);
//登陆成功
                            loginSuccess(jresp);

                            break;
//                            422
                        case HTTP_USER_NULL:
                            Message msg4=new Message();
                            msg4.what=DISMISS_DIALOG;
                            msg4.obj=loadingDraw;
                            msgHandler.sendMessage(msg4);

                            if (jresp.has("errors")){
                                JSONObject  errorStr=jresp.getJSONObject("errors");
                                if (errorStr.has("username")){
                                    Message msg5=new Message();
                                    msg5.what=response.code();
                                    msg5.obj=errorStr.getString("username");
                                    msgHandler.sendMessage(msg5);
                                }else {
                                    Message msg5=new Message();
                                    msg5.what=response.code();
                                    msg5.obj=errorStr.getString("password");
                                    msgHandler.sendMessage(msg5);
                                }
                            }



                            break;
//                            401
                        case HTTP_USER_ERROR:
                            Message msg1=new Message();
                            msg1.what=DISMISS_DIALOG;
                            msg1.obj=loadingDraw;
                            msgHandler.sendMessage(msg1);

                            Message msg2=new Message();
                            msg2.what=response.code();
                            msg2.obj=jresp.getString("message");
                            msgHandler.sendMessage(msg2);
                            break;
//                            500
                        case HTTP_OVERTIME:
                            Message msg6=new Message();
                            msg6.what=DISMISS_DIALOG;
                            msg6.obj=loadingDraw;
                            msgHandler.sendMessage(msg6);

                            Message msg7=new Message();
                            msg7.what=response.code();
                            msg7.obj="请求次数过多,请稍后再试";
                            msgHandler.sendMessage(msg7);
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
    private void loginSuccess(JSONObject jresp) throws JSONException {
        loadingDraw.show();
        User user=new User();
        GlobalUserInfo.userInfo.user=user;
        GlobalUserInfo.userInfo.token=jresp.getString("access_token");
        GlobalUserInfo.userInfo.tokenType=jresp.getString("token_type");

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

                            GlobalUserInfo.userInfo.user.setUserId(jresp.getString("id"));
                            GlobalUserInfo.userInfo.user.setUserName(jresp.getString("name"));
                            GlobalUserInfo.userInfo.user.setUserEmail(jresp.getString("email"));
                            GlobalUserInfo.userInfo.user.setUserImg(jresp.getString("avatar"));
                            GlobalUserInfo.userInfo.user.setUserIntroduction(jresp.getString("introduction"));
//                            是否绑定
                            GlobalUserInfo.userInfo.user.setBoundPhone(jresp.getBoolean("bound_phone"));
//                            是否验证邮箱
                            GlobalUserInfo.userInfo.user.setEmail_verified(jresp.getBoolean("email_verified"));
//                            注册
                            GlobalUserInfo.userInfo.user.setUserBornDate(jresp.getString("created_at"));

                            new Thread(){
                                public void run(){
                                    Message msg5=new Message();
                                    msg5.what=USER_SET_INFORMATION;
                                    msgHandler.sendMessage(msg5);
                                }
                            }.start();

                            Intent intent=new Intent(LoginActivity.this,UserHomePageActivity.class);
                            startActivity(intent);
                            finish();
                            break;
//                            令牌失效，重新请求
//                        case HTTP_USER_ERROR:
//                            Message msg4=new Message();
//                            msg4.what=DISMISS_DIALOG;
//                            msg4.obj=loadingDraw;
//                            msgHandler.sendMessage(msg4);
//
//                            Authenticator authenticator=new Authenticator() {
//                                @Override
//                                public Request authenticate(Route route, Response response) throws IOException {
////    刷新token
//                                    return response.request().newBuilder().addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token).build();
//                                }
//                            };
//                            break;
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
