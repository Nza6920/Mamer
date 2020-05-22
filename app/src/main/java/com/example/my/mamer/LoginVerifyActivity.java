package com.example.my.mamer;

import android.Manifest;
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
import com.example.my.mamer.util.StringToDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.my.mamer.MyApplication.getContext;
import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OVERNUM;
import static com.example.my.mamer.config.Config.HTTP_OVERTIME;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;
import static com.example.my.mamer.config.Config.REQUEST_OK_TOKENREFRESH;
import static com.example.my.mamer.config.Config.USER_INFORMATION;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

/**
 * 首先进入校验是否登录（欢迎）的界面
 */
public class LoginVerifyActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private LoadingDraw loadingDraw;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private boolean permissionFlag=true;
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
                case REQUEST_OK_TOKENREFRESH:
                    JSONObject jrep=null;
                    try {
                        jrep=new JSONObject((String) msg.obj);
                        String key=jrep.getString("access_token");
                        String type=jrep.getString("token_type");
                        MyApplication.globalUserInfo.token=key;
                        MyApplication.globalUserInfo.tokenType=type;
                        editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                        editor.putString("key",key);
                        editor.putString("type",type);
                        editor.putBoolean("loginFlag",true);
                        editor.apply();
                        loginSuccess();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case USER_SET_INFORMATION:
                    JSONObject jrespInfo = null;
                    JSONArray jsonArray=null;
                    try {
                        jrespInfo = new JSONObject((String)msg.obj);
                        User user=new User();
                        MyApplication.globalUserInfo.user=user;
                        MyApplication.globalUserInfo.user.setUserId(jrespInfo.getString("id"));
                        MyApplication.globalUserInfo.user.setUserName(jrespInfo.getString("name"));
                        MyApplication.globalUserInfo.user.setUserEmail(jrespInfo.getString("email"));
                        MyApplication.globalUserInfo.user.setUserImg(jrespInfo.getString("avatar"));
                        MyApplication.globalUserInfo.user.setAvatarType(BaseUtils.reverseString(jrespInfo.getString("avatar")));
                        String introduction=jrespInfo.getString("introduction");
                        if (introduction.equals("null")){
                            MyApplication.globalUserInfo.user.setUserIntroduction("");
                        }else {
                            MyApplication.globalUserInfo.user.setUserIntroduction(introduction);
                        }
                        //                            是否绑定
                        MyApplication.globalUserInfo.user.setBoundPhone(jrespInfo.getBoolean("bound_phone"));
                        //                            是否验证邮箱
                        MyApplication.globalUserInfo.user.setEmail_verified(jrespInfo.getBoolean("email_verified"));
                        //                            注册
                        MyApplication.globalUserInfo.user.setUserBornDate(StringToDate.stringToShort(jrespInfo.getString("created_at")));

                        Intent intent=new Intent(LoginVerifyActivity.this,BottomNavigationBarActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        setContentView(R.layout.activity_login_verify);
        loadingDraw=new LoadingDraw(this);
        //        所需要申请的权限
        String[] perms={Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        checkPermission(perms);

        if (permissionFlag){
            prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
            loginFlag=prefs.getBoolean("loginFlag",false);
            if (loginFlag){
                refreshKey();
            }else {
                Intent intent=new Intent(LoginVerifyActivity.this,BottomNavigationBarActivity.class);
                startActivity(intent);
                finish();
            }
        }else {
            finish();
        }

        //        界面停留1.5秒
        msgHandler.sendEmptyMessageDelayed(0,1500);
    }

    //    刷新
    public  void  refreshKey(){
        loadingDraw.show();
        HttpUtil.sendOkHttpRefreshToken(REFRESH_TOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg = new Message();
                msg.what = DISMISS_DIALOG;
                msg.obj = loadingDraw;
                msgHandler.sendMessage(msg);

                Message msg1 = new Message();
                msg1.what = MESSAGE_ERROR;
                msg1.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg1);

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg = new Message();
                msg.what = DISMISS_DIALOG;
                msg.obj = loadingDraw;
                msgHandler.sendMessage(msg);
                try {
                switch (response.code()){
                    case HTTP_USER_GET_INFORMATION:
                        Message message=new Message();
                        message.what=REQUEST_OK_TOKENREFRESH;
                        message.obj=new JSONObject(response.body().string()).toString();
                        msgHandler.sendMessage(message);

                        break;
                    case HTTP_OVERNUM:
                        Message msg1 = new Message();
                        msg1.what = MESSAGE_ERROR;
                        msg1.obj = "当前信息已过期，请重新登录";
                        msgHandler.sendMessage(msg1);

                        Intent intent=new Intent(LoginVerifyActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
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
        loadingDraw.show();
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
                Message msg=new Message();
                msg.what=DISMISS_DIALOG;
                msg.obj=loadingDraw;
                msgHandler.sendMessage(msg);

                JSONObject jresp = null;
                JSONArray jsonArray=null;
                try {
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            Message msg1 = new Message();
                            msg1.what = USER_SET_INFORMATION;
                            msg1.obj = jresp.toString();
                            msgHandler.sendMessage(msg1);
                            //获取用户信息成功后返回首页
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


    private void checkPermission(String[] perms){
        //        检查自己是否得到该权限

        if (EasyPermissions.hasPermissions(this,perms)){
            Log.e("Tag","已获得权限:"+perms);

        } else {
            EasyPermissions.requestPermissions(this,"必要权限",0,perms);
            permissionFlag=false;
        }
    }


    //    请求权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//将结果转发到easyPermission
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
        Log.i("TAG:",requestCode+"~~~~~"+permissions+"~~~~~~~~"+grantResults);
    }


    @Override
    public void onPermissionsGranted(int requestCode,List<String> perms) {
        Log.i("TAG","获取成功的权限:"+perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode,List<String> perms) {
        Toast.makeText(this,"该功能需要授权方能使用",Toast.LENGTH_SHORT).show();
        Log.i("TAG","获取失败的权限:"+perms);
        finish();
    }
}
