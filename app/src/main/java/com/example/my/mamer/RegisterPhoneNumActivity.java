package com.example.my.mamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;


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
//    已注册
    private TextView tvPhoneHad;

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
//设置
        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvClose.setBackground(tvClosePic);

        btnLogin.setText("密码登陆");
        SpannableString sHint=new SpannableString("手机号");
        AbsoluteSizeSpan tSize =new AbsoluteSizeSpan(15,true);
        sHint.setSpan(tSize,0,sHint.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhoneNum.setHint(sHint);
        btnPhoneNum.getBackground().setAlpha(111);

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
//        输入监听
        etPhoneNum.addTextChangedListener(etWatcher);


//        到图片验证码页面
        btnPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//函数返回的值将作为是否跳转下一个页面的标准，手机号正确就跳转，不正确就提示用户重新输入！！！


                Intent intent =new Intent(RegisterPhoneNumActivity.this,RegisterPicCode.class);
                startActivity(intent);
                RegisterPhoneNumActivity.this.finish();
            }
        });
//        已注册
        tvPhoneHad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
//实时监听输入
    TextWatcher etWatcher=new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            Log.e("Tag","有变化");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            getEditString();
            if (isPhoneNumberValid(phoneNum)){
                btnPhoneNum.getBackground().setAlpha(255);
                btnPhoneNum.setEnabled(true);

            }else {
                btnPhoneNum.getBackground().setAlpha(111);
                btnPhoneNum.setEnabled(false);
            }

        }
    };
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
//                将用户输入的值提交给服务器，服务器返回的值将在下面这个函数里面

//    ！！！！记住，还没有写完
}
