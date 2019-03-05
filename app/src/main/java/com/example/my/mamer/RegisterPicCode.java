package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterPicCode extends AppCompatActivity {

    private LoadingDraw loadingDraw;
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
    private static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");
    private String address="https://mamer.club/api/verificationCodes";

    //        UI
    Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 7:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case 9:
                    Toast.makeText(RegisterPicCode.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case 422:
                    Toast.makeText(RegisterPicCode.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

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
//        从配置里加载
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String picCode=prefs.getString("img","");
        loadPicCodeImg(picCode);
        picCodeImg.setImageBitmap(loadPicCodeImg(picCode));


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
                if (endTime()){
                    SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this);
                    String picCodekey=prefs.getString("next_key","");
                    postInformation(address,picCodekey);
                }else {
                    Intent intent=new Intent(RegisterPicCode.this,RegisterPhoneNumActivity.class);
                    intent.putExtra("next_key","");
                    intent.putExtra("end_time","");
                    intent.putExtra("img","");
                    startActivity(intent);
//                                Log.e("Tag","captcha_key:"+jresp.getString("captcha_key")+"expired_at: "+jresp.getJSONObject("expired_at").getString("date")+"captcha_image_content: "+jresp.getString("captcha_image_content"));
                    finish();
                }
            }
        });
//        验证按钮
        btnCodeStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDraw.show();
//      函数返回的值将作为是否跳转下一个页面的标准，验证码正确就跳转到注册页面，不正确就提示用户重新输入并且刷新验证码！！！
                SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this);
                String picCodekey=prefs.getString("next_key","");
                postInformation(address,picCodekey);


            }
        });
    }


//    加载验证图片
    private Bitmap loadPicCodeImg(String bicCodes){

        Bitmap bitmap=null;
        try {
            byte[] bitmapArray=Base64.decode(bicCodes.split(",")[1],Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
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
//    数据转换为json格式数据字符串
private String getJson(String codeStr)throws Exception{
    JSONObject jsonParam=new JSONObject();
    SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this);
    String picCodekey=prefs.getString("next_key","");
    jsonParam.put("captcha_key",picCodekey);
    jsonParam.put("captcha_code",codeStr);
    return jsonParam.toString();
}
    //    POST方式！！！！
    private void postInformation(String address,String codeStr){
        String jsonStr="";
        try {
            jsonStr=getJson(codeStr);
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(JSON,jsonStr);
        HttpUtil.sendOkHttpRequest(address, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=7;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2=new Message();
                msg2.what=9;
                msg2.obj="服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());

                    if (response.code()==201){
                        Message msg3=new Message();
                        msg3.what=7;
                        msg3.obj=loadingDraw;
                        msgHandler.sendMessage(msg3);


                        Intent intent=new Intent(RegisterPicCode.this,RegisterActivity.class);
                        intent.putExtra("next_key",jresp.getString("captcha_key"));
                        intent.putExtra("end_time",jresp.getJSONObject("expired_at").getString("date"));
                        startActivity(intent);
//                                Log.e("Tag","captcha_key:"+jresp.getString("captcha_key")+"expired_at: "+jresp.getJSONObject("expired_at").getString("date")+"captcha_image_content: "+jresp.getString("captcha_image_content"));
                        finish();
                    }else {
                        Message msg4=new Message();
                        msg4.what=7;
                        msg4.obj=loadingDraw;
                        msgHandler.sendMessage(msg4);

                        Message msg5=new Message();
                        msg5.what=response.code();
                        msg5.obj=jresp.getJSONObject("errors").getString("phone");
                        msgHandler.sendMessage(msg5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
//截至请求时间
    private Boolean endTime(){
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this);
        String picCodeEndTime=prefs.getString("end_time","");
        Date endDate=new Date(picCodeEndTime);
//        获取当前时间
        SimpleDateFormat simPletDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=new Date(System.currentTimeMillis());
        Long subDate=endDate.getTime()-date.getTime();
//        超过时间不可点击
        if (subDate<0||subDate==0){
            return false;
        }else {
            return true;
        }

    }

}
