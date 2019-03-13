package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_OVERNUM;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.JSON;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REGISTER;

public class RegisterActivity extends AppCompatActivity {
    private LoadingDraw loadingDraw;
    //    关闭按钮
    private TextView tvClose;
    //    用户名
    private EditText etUName;
    private TextView tvInputUser;
    private String uName;
    //    密码
    private EditText etPassWord;
    private TextView tvInputPas;
    private String pas;
    //    again
    private EditText etPassWordAgain;
    private String pasAgain;
    private TextView tvInputPasAgain;

    //    邮箱
    private EditText etEmil;
    private TextView tvInputEmil;
    private String emil;
    //    短信验证码
    private EditText etVerificationCodes;
    private String verificationCode;
    //    提交
    private Button btnRegister;
    //正则
    String regExUname = "^[a-zA-Z0-9\u4e00-\u9fa5]{2,15}";
    String regExPasCN = "[\u4e00-\u9fa5]";
    String regExEmil = "^[a-z0-9A-Z]+[-|a-z0-9A-Z._]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";
    String regExNull="\\s+";


    //        UI
    private final Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISMISS_DIALOG:
                    ((LoadingDraw) msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(RegisterActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_ERROR:
                    Toast.makeText(RegisterActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_NULL:
                    Toast.makeText(RegisterActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadingDraw = new LoadingDraw(this);
        init();
    }

    private void init() {
        tvClose = findViewById(R.id.title_tv_close);
        etUName = findViewById(R.id.register_username);
        tvInputUser = findViewById(R.id.register_input_user);
        etPassWord = findViewById(R.id.register_password);
        etPassWordAgain = findViewById(R.id.register_password_again);
        tvInputPas = findViewById(R.id.register_input_pas);
        tvInputPasAgain=findViewById(R.id.register_input_pas_again);
        etEmil = findViewById(R.id.register_emil);
        tvInputEmil = findViewById(R.id.register_input_emil);
        etVerificationCodes = findViewById(R.id.register_verification);
        btnRegister = findViewById(R.id.register_btn);

//        设置
        Drawable tvClosePic = ContextCompat.getDrawable(this, R.mipmap.ic_title_back);
        tvClose.setBackground(tvClosePic);

        String userName = "请输入姓名";
        final String passWord = "请输入密码";
        String pasWordAgain = "请再次输入密码";
        String emils = "请输入邮箱";
        final String verificationCode = "请输入验证码";
        setHintAll(etUName, userName);
        setHintAll(etPassWord, passWord);
        setHintAll(etPassWordAgain, pasWordAgain);
        setHintAll(etEmil, emils);
        setHintAll(etVerificationCodes, verificationCode);

//        转到手机号验证页面
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous();
            }
        });
//        姓名由汉字英文数字组成
        etUName.addTextChangedListener(etWatcher);
//        密码由英文数字符号组成
        etPassWord.addTextChangedListener(etWatcherPas);
//        2
        etPassWordAgain.addTextChangedListener(etWatcherPasAgain);
//         邮箱格式，在提交按钮事件中判断
        etEmil.addTextChangedListener(etWatcherEmil);
//        提交,判断各项均填写了
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDraw.show();
                if (isRegisterRight()) {
                    postInformation(REGISTER);
                } else {
                    Message msg1 = new Message();
                    msg1.what = DISMISS_DIALOG;
                    msg1.obj = loadingDraw;
                    msgHandler.sendMessage(msg1);

                    Toast.makeText(RegisterActivity.this, "存在不合法输入", Toast.LENGTH_SHORT).show();


                }

            }
        });
    }

    //    hint设置
    private void setHintAll(EditText editText, String s) {
        SpannableString sHint = new SpannableString(s);
        AbsoluteSizeSpan tSize = new AbsoluteSizeSpan(15, true);
        sHint.setSpan(tSize, 0, sHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editText.setHint(sHint);
    }

    //    获取用户输入的值
    private void getEditString() {
        uName = etUName.getText().toString().trim();
        pas = etPassWord.getText().toString().trim();
        pasAgain=etPassWordAgain.getText().toString().trim();
        emil = etEmil.getText().toString().trim();
        verificationCode = etVerificationCodes.getText().toString().trim();

    }

    //    输入监听，用户名
    TextWatcher etWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            getEditString();
            if (!isUserName(uName) || (!((calculatePlaces(uName) > 1) && (calculatePlaces(uName) <= 12)))) {
                tvInputUser.setTextColor(Color.RED);
            } else {
                tvInputUser.setTextColor(Color.WHITE);
            }
        }
    };

    //    密码
    TextWatcher etWatcherPas=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            getEditString();
            Pattern pattern = Pattern.compile(regExPasCN);
            Matcher matcher = pattern.matcher(pas);

            Pattern patternNull = Pattern.compile(regExNull);
            Matcher matcherNull = patternNull.matcher(pas);
            if (matcher.find() || matcherNull.find() || (!(calculatePlaces(pas) > 5))) {
                tvInputPas.setTextColor(Color.RED);
            } else {
                tvInputPas.setTextColor(Color.WHITE);
            }
        }

    };
//2
    TextWatcher etWatcherPasAgain=new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isPasSame()){
           tvInputPasAgain.setTextColor(Color.WHITE);
        }else {
            tvInputPasAgain.setTextColor(Color.RED);
        }
    }
};

    //    邮箱
    TextWatcher etWatcherEmil = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            getEditString();
            Pattern pattern = Pattern.compile(regExEmil);
            Matcher matcher = pattern.matcher(emil);
            if (matcher.matches()) {
                tvInputEmil.setTextColor(Color.WHITE);
            } else {
                tvInputEmil.setTextColor(Color.RED);
            }
        }
    };

    //    用户名输入合法判断
    private Boolean isUserName(String str) {
        if (null == str || "".equals(str)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(regExUname);
            Matcher matcher = pattern.matcher(str);
            if (!matcher.matches()) {
                return false;
            } else {
                return true;
            }
            }
        }


    //    计算位数
    private static int calculatePlaces(String str) {
        int m = 0;
        char arr[] = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
//            中文字符
            if ((c >= 0x0391 && c <= 0xFFE5)) {
                m = m + 2;
            } else if ((c >= 0x0000 && c <= 0x00FF)) {
                m = m + 1;
            }
        }
        return m;
    }

    //    密码输入合法判断
//    private Boolean isPasCN(String str) {
//        if ( null==str||"".equals(str)) {
//            return false;
//        }else{
//            Pattern pattern=Pattern.compile(regExPasCN);
//            Matcher matcher = pattern.matcher(str);
//            if (!matcher.matches()){
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//    }

//再次输入密码合法判断
    private Boolean isPasSame(){
        getEditString();
        if (pas.equals(pasAgain)){
            return true;
        }else {
            return false;
        }
    }
//提交入口
    private Boolean isRegisterRight(){
        getEditString();
        if (TextUtils.isEmpty(uName)||TextUtils.isEmpty(emil)||TextUtils.isEmpty(pas)||TextUtils.isEmpty(verificationCode)){
            return false;
        }else {
                return true;
            }
        }

//    数据转换为json格式数据字符串
    private String getJson(String verificationCode,String uName,String pas,String emil)throws Exception{
        JSONObject jsonParam=new JSONObject();
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String verification_key=prefs.getString("new_key","");
        jsonParam.put("verification_key",verification_key);
        jsonParam.put("verification_code",verificationCode);
        jsonParam.put("name",uName);
        jsonParam.put("password",pas);
        jsonParam.put("email",emil);
        return jsonParam.toString();
    }
//    POST方式！！！！
    private void postInformation(String address){
        getEditString();
        String jsonStr="";
        try {
            jsonStr=getJson(verificationCode,uName,pas,emil);
        }catch (Exception e){
            e.printStackTrace();
        }
        final RequestBody requestBody=RequestBody.create(JSON,jsonStr);
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
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
            public void onResponse(Call call, Response response) throws  IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());

                switch (response.code()){
                        case HTTP_OK:
                            Message msg3=new Message();
                            msg3.what=DISMISS_DIALOG;
                            msg3.obj=loadingDraw;
                            msgHandler.sendMessage(msg3);

                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this ).edit();
                            editor.clear().apply();
                            Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case HTTP_USER_NULL:
                                Message msg4=new Message();
                                msg4.what=DISMISS_DIALOG;
                                msg4.obj=loadingDraw;
                                msgHandler.sendMessage(msg4);

                                 if (jresp.has("errors")){
                                    JSONObject errors=jresp.getJSONObject("errors");
                                    if (errors.has("verification_code")){
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj=errors.getString("verification_code");
                                        msgHandler.sendMessage(msg5);
                                    }else if (errors.has("name")){
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj=errors.getString("name");
                                        msgHandler.sendMessage(msg5);
                                    }else if (errors.has("email")){
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj=errors.getString("email");
                                        msgHandler.sendMessage(msg5);
                                    }else if (errors.has("password")){
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj=errors.getString("password");
                                        msgHandler.sendMessage(msg5);
                                    }else {
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj="输入不合法";
                                        msgHandler.sendMessage(msg5);
                                    }

                                }

                            break;
//                                401
                    case HTTP_USER_ERROR:
                        Message msg8=new Message();
                        msg8.what=DISMISS_DIALOG;
                        msg8.obj=loadingDraw;
                        msgHandler.sendMessage(msg8);

                        String messageStr=jresp.getString("message");
                        if (messageStr.equals("验证码失效")){
                            Message msg9=new Message();
                            msg9.what=response.code();
                            msg9.obj=jresp.getString("message");
                            msgHandler.sendMessage(msg9);
                            previous();
                        }else {
                            Message msg9=new Message();
                            msg9.what=response.code();
                            msg9.obj=jresp.getString("message");
                            msgHandler.sendMessage(msg9);
                        }

                        break;
                        case HTTP_OVERNUM:
                            Message msg6=new Message();
                            msg6.what=DISMISS_DIALOG;
                            msg6.obj=loadingDraw;
                            msgHandler.sendMessage(msg6);

                            Message msg7=new Message();
                            msg7.what=response.code();
                            msg7.obj="请求次数过多,请稍后再试";
                            msgHandler.sendMessage(msg7);
                        default:
                            break;

                }} catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //    跳转手机验证
    private void previous(){
        Intent intent=new Intent(RegisterActivity.this,RegisterPhoneNumActivity.class);
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this ).edit();
        editor.clear().apply();
        startActivity(intent);
        finish();
    }




}
