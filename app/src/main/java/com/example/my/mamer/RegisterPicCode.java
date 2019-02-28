package com.example.my.mamer;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class RegisterPicCode extends AppCompatActivity {

//    关闭页面按钮
    private TextView tvClose;
//    图片
    private ImageView picCodeImg;
//    输入
    private EditText etCodeStr;
    private String codeStr;
//    change按钮
    private TextView tvChange;
//    验证按钮
    private Button btnCodeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pic_code);

        init();
    }

    private void init(){
//        初始化控件
        tvClose=findViewById(R.id.title_tv_close);
        picCodeImg=findViewById(R.id.register_pic_img);
        etCodeStr=findViewById(R.id.register_pic_code);
        tvChange=findViewById(R.id.register_pic_change);
        btnCodeStr=findViewById(R.id.register_pic_code_btn);

//        设置
        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvClose.setBackground(tvClosePic);

        SpannableString sHint=new SpannableString("请输入左侧验证码");
        AbsoluteSizeSpan tSize =new AbsoluteSizeSpan(15,true);
        sHint.setSpan(tSize,0,sHint.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etCodeStr.setHint(sHint);

        btnCodeStr.getBackground().setAlpha(111);
//        是否存在图片
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String picCode=prefs.getString("pic_code",null);
        if (picCode!=null){
            Glide.with(this).load(picCode).into(picCodeImg);
        }else {
            loadPicCodeImg();
        }
//监听事件
//        关闭页面
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        输入
        etCodeStr.addTextChangedListener(etWatcher);
//        change按钮
        tvChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPicCodeImg();
            }
        });
//        验证按钮
        btnCodeStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//      函数返回的值将作为是否跳转下一个页面的标准，验证码正确就跳转到注册页面，不正确就提示用户重新输入并且刷新验证码！！！
            }
        });
    }


//    加载图片验证
    private void loadPicCodeImg(){

    }
//    输入监听
//              得到输入内容
    private void getEditString(){
    codeStr=etCodeStr.getText().toString().trim();
    }
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
            if (TextUtils.isEmpty(codeStr)){
                btnCodeStr.getBackground().setAlpha(111);
                btnCodeStr.setEnabled(false);


            }else {
                btnCodeStr.getBackground().setAlpha(255);
                btnCodeStr.setEnabled(true);
            }
        }
    };

//    提交用户输入信息，并得到返回值！！！


}
