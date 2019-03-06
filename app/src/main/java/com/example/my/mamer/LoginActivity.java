package com.example.my.mamer;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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

        final String userName="请输入姓名";
        String passWord="请输入密码";
        setHintAll(etUName,userName);
        setHintAll(etPas,passWord);

        btnLogin.getBackground().setAlpha(111);
//        监听，点击事件
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

                }else if (pas.length()<5) {

                }else {
                    btnLogin.getBackground().setAlpha(255);
                    btnLogin.setEnabled(true);
                }

            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
}
