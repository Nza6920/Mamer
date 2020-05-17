package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.my.mamer.bean.User;
import com.example.my.mamer.util.BaseUtils;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.MyApplication.getContext;
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

/**
 * 首先进入校验是否登录（欢迎）的界面
 */
public class LoginVerifyActivity extends AppCompatActivity {

    private LoadingDraw loadingDraw;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private String prefsName;
    private String prefsPas;
    private boolean loginFlag;

    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(LoginVerifyActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_NULL:
                    Toast.makeText(LoginVerifyActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_ERROR:
                    Toast.makeText(LoginVerifyActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OVERTIME:
                    Toast.makeText(LoginVerifyActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_login_verify);
        loadingDraw=new LoadingDraw(this);

        prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        prefsName=prefs.getString("username","");
        prefsPas=prefs.getString("password","");
        loginFlag=prefs.getBoolean("loginFlag",false);
        if (loginFlag){
            postInformation(prefsName,prefsPas);
        }else {
//             MyApplication.globalUserInfo.token="";

            Intent intent=new Intent(LoginVerifyActivity.this,BottomNavigationBarActivity.class);
            startActivity(intent);
            finish();
        }
        //        界面停留1.5秒
        msgHandler.sendEmptyMessageDelayed(0,1500);
    }
    //    post
    private void postInformation(String name,String password)  {
        loadingDraw.show();
        JSONObject jsonParam=new JSONObject();
        try {
            jsonParam.put("username",name);
            jsonParam.put("password",password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                JSONObject jresp=null;
                try {
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_OK:
                            String key=jresp.getString("access_token");
                            String type=jresp.getString("token_type");

                            MyApplication.globalUserInfo.token=key;
                            MyApplication.globalUserInfo.tokenType=type;

                            editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString("key",key);
                            editor.putString("type",type);
                            editor.putBoolean("loginFlag",true);
                            editor.apply();

                            loginSuccess();
                            break;
//                            422
                        case HTTP_USER_NULL:
                            editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putBoolean("loginFlag",false);
                            editor.apply();

                            Intent i_ftt=new Intent(LoginVerifyActivity.this,LoginActivity.class);
                            startActivity(i_ftt);
                            finish();
                            break;
//                            401
                        case HTTP_USER_ERROR:
                            editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putBoolean("loginFlag",false);
                            editor.apply();

                            Intent i_fzo=new Intent(LoginVerifyActivity.this,LoginActivity.class);
                            startActivity(i_fzo);
                            finish();
                            break;
//                            429
                        case HTTP_OVERTIME:


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


    //    获取当前用户信息
    private void loginSuccess()  {
        HttpUtil.sendOkHttpRequestGet(USER_INFORMATION, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2=new Message();
                msg2.what=MESSAGE_ERROR;
                msg2.obj="服务器异常,请检查网络";
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
                            User user=new User();
                            MyApplication.globalUserInfo.user=user;
                            MyApplication.globalUserInfo.user.setUserId(jresp.getString("id"));
                            MyApplication.globalUserInfo.user.setUserName(jresp.getString("name"));
                            MyApplication.globalUserInfo.user.setUserEmail(jresp.getString("email"));
                            MyApplication.globalUserInfo.user.setUserImg(jresp.getString("avatar"));
                            MyApplication.globalUserInfo.user.setAvatarType(BaseUtils.reverseString(jresp.getString("avatar")));
                            String introduction=jresp.getString("introduction");
                            if (introduction.equals("null")){
                                MyApplication.globalUserInfo.user.setUserIntroduction("");
                            }else {
                                MyApplication.globalUserInfo.user.setUserIntroduction(introduction);
                            }
                            //                            是否绑定
                            MyApplication.globalUserInfo.user.setBoundPhone(jresp.getBoolean("bound_phone"));
                            //                            是否验证邮箱
                            MyApplication.globalUserInfo.user.setEmail_verified(jresp.getBoolean("email_verified"));
                            //                            注册
                            MyApplication.globalUserInfo.user.setUserBornDate(jresp.getString("created_at"));

                            Log.e("userImg","img:"+    MyApplication.globalUserInfo.user.getUserPassKey());

                            new Thread(){
                                public void run(){
                                    Message msg5=new Message();
                                    msg5.what=USER_SET_INFORMATION;
                                    msgHandler.sendMessage(msg5);
                                }
                            }.start();
                            //登录成功后，返回调用之前的界面
                            Intent intent=new Intent(LoginVerifyActivity.this,BottomNavigationBarActivity.class);
                            startActivity(intent);

                            finish();
                            break;
                        //                            令牌失效，重新请求
                        case HTTP_USER_ERROR:
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
