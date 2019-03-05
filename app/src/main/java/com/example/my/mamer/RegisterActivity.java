package com.example.my.mamer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegisterActivity extends AppCompatActivity {
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
//    邮箱
    private EditText etEmil;
    private TextView tvInputEmil;
    private String emil;
//    短信验证码
    private EditText etVerificationCodes;
    private String verificationCode;
//    再次获得验证码
    private TextView tvVerificationAgain;
//    提交
    private Button btnRegister;
//正则
    String regExUname="^[a-zA-Z0-9\u4e00-\u9fa5]{2,15}";
    String regExPasCN="\u4e00-\u9fa5";
    String regExEmil="^[a-z0-9A-Z]+[-|a-z0-9A-Z._]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init(){
        tvClose=findViewById(R.id.title_tv_close);
        etUName=findViewById(R.id.register_username);
        tvInputUser=findViewById(R.id.register_input_user);
        etPassWord=findViewById(R.id.register_password);
        tvInputPas=findViewById(R.id.register_input_pas);
        etEmil=findViewById(R.id.register_emil);
        tvInputEmil=findViewById(R.id.register_input_emil);
        etVerificationCodes=findViewById(R.id.register_verification);
        tvVerificationAgain=findViewById(R.id.register_get_again);
        btnRegister=findViewById(R.id.register_btn);

//        设置
        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvClose.setBackground(tvClosePic);

        String userName="请输入姓名";
        final String passWord="请输入密码";
        String emils="请输入邮箱";
        final String verificationCode="请输入验证码";
        setHintAll(etUName,userName);
        setHintAll(etPassWord,passWord);
        setHintAll(etEmil,emils);
        setHintAll(etVerificationCodes,verificationCode);

//        转到手机号验证页面
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RegisterActivity.this,RegisterPhoneNumActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
        });
//        姓名由汉字英文数字组成
        etUName.addTextChangedListener(etWatcher);
//        密码由英文数字符号组成
        etPassWord.addTextChangedListener(etWatcherPas);
//         邮箱格式，在提交按钮事件中判断
        etEmil.addTextChangedListener(etWatcherEmil);
//        验证码判断
//        获取新的验证码,验证码失效前到规定时间后可以点击
        tvVerificationAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        提交,判断各项均填写了
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRegisterRight()){
                    Intent intent =new Intent(RegisterActivity.this,RegisterPhoneNumActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                }else {
                    etUName.setText("");
                    etPassWord.setText("");
                    etEmil.setText("");
                    etVerificationCodes.setText("");
                    Toast.makeText(RegisterActivity.this,"存在不合法输入",Toast.LENGTH_SHORT).show();
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
        pas=etPassWord.getText().toString().trim();
        emil=etEmil.getText().toString().trim();
        verificationCode=etVerificationCodes.getText().toString().trim();

    }
//    输入监听，用户名
    TextWatcher etWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            getEditString();
            if (!isUserName(uName)||(!((calculatePlaces(uName)>=3)&&(calculatePlaces(uName)<=16)))){
                tvInputUser.setTextColor(Color.RED);
            }else {
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
        if (!isPas(pas)||(!((calculatePlaces(pas)>=6)&&(calculatePlaces(pas)<=12)))){
            tvInputPas.setTextColor(Color.RED);
        }else {
            tvInputPas.setTextColor(Color.WHITE);
        }

    }
};
//    邮箱
    TextWatcher etWatcherEmil=new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        getEditString();
        if (isEmil(emil)){
            tvInputEmil.setTextColor(Color.WHITE);
        }else {
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
    private static int calculatePlaces(String str){
        int m=0;
        char arr[]=str.toCharArray();
        for (int i=0;i<arr.length;i++){
            char c =arr[i];
//            中文字符
            if ((c>=0x0391 && c<=0xFFE5)){
                m=m+2;
            }else if ((c>=0x0000 && c<=0x00FF)){
                m=m+1;
            }
        } return m;
    }
//    密码输入合法判断
    private Boolean isPas(String str) {
        if (null == str || "".equals(str)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(regExPasCN);
            Matcher matcher = pattern.matcher(str);
            if (!matcher.matches()) {
                return true;
            } else {
                return false;
            }
        }
    }

    //    emil输入合法判断
    private Boolean isEmil(String emil){
        if (null==emil || "".equals(emil)){
            return false;

        }else {
            Pattern pattern =Pattern.compile(regExEmil);
            Matcher matcher=pattern.matcher(emil);
            if (!matcher.matches()){
                return false;
            }else {
                return true;
            }
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



}
