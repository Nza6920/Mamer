package com.example.my.mamer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPhoneNumActivity extends AppCompatActivity {

//    注册验证的手机号
    private EditText etPhoneNum;
    private String  phoneNum;
//    注册验证按钮
    private Button btnPhoneNum;
//    返回按钮
    private TextView tvClose;
//    密码登陆按钮
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_num);
        init();
    }

    public void init(){
        tvClose=findViewById(R.id.title_tv_close);
        btnLogin=findViewById(R.id.title_btn_next);
        etPhoneNum=findViewById(R.id.register_phone_num);
        btnPhoneNum=findViewById(R.id.register_phone_num_btn);

        SpannableString sHint=new SpannableString("手机号");
        AbsoluteSizeSpan tSize =new AbsoluteSizeSpan(15,true);
        sHint.setSpan(tSize,0,sHint.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhoneNum.setHint(sHint);
//关闭页面
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        密码登陆
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        etPhoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getEditString();
                if (isPhoneNumberValid(phoneNum)){
                    btnPhoneNum.setEnabled(true);
                }

            }
        });
//        到图片验证码页面
        btnPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                测试跳转
                Intent intent =new Intent(RegisterPhoneNumActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
//得到输入内容
    private void getEditString(){
        phoneNum=etPhoneNum.getText().toString().trim();
    }

//libphonenumber判断手机号输入是否正确
    public boolean isPhoneNumberValid(String phoneNum){

        PhoneNumberUtil phoneNumUtil =PhoneNumberUtil.getInstance();
        String countryCode=Locale.getDefault().getCountry();

        try {
            Phonenumber.PhoneNumber numberProto=phoneNumUtil.parse(phoneNum,countryCode);
            return phoneNumUtil.isValidNumber(numberProto);
        }catch (NumberParseException e){
            System.err.println("NumberParseException was thrown: "+e.toString());
        }
        return false;
    }

//正则表达检查手机号输入的合法性
    private boolean isPhoneNum(String phoneNum){
        boolean flag=false;
        String regexPhone="^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,2,3,5,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
//        输入长度不为11位
        if (phoneNum.length()!=11){
            flag=true;
        }else {
            Pattern pattern=Pattern.compile(regexPhone);
            Matcher matcher=pattern.matcher(phoneNum);
            boolean isMatch=matcher.matches();
//            输入为11位，可内容不合法
            if (isMatch){
                flag=true;
            }
        }
        return flag;
    }


}
