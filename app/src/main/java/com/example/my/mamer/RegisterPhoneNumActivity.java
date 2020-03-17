package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.JSON;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.PHONE_NUMBER;


public class RegisterPhoneNumActivity extends AppCompatActivity {

    private LoadingDraw loadingDraw;
//    注册验证的手机号
    private EditText etPhoneNum;
    private String  phoneNum;
//    注册验证按钮
    private Button btnPhoneNum;
//    返回按钮
    private TextView tvClose;
//    已注册
    private TextView tvPhoneHad;
//        UI
    Handler msgHandler=new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case DISMISS_DIALOG:
                ((LoadingDraw)msg.obj).dismiss();
                break;
            case HTTP_USER_NULL:
                Toast.makeText(RegisterPhoneNumActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_register_phone_num);
        loadingDraw =new LoadingDraw(this);
//        先清理
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(this ).edit();
        editor.clear().apply();

        init();
    }

    public void init(){
        tvClose=findViewById(R.id.title_tv_close);
        etPhoneNum=findViewById(R.id.register_phone_num);
        btnPhoneNum=findViewById(R.id.register_phone_num_btn);
        tvPhoneHad=findViewById(R.id.register_had);
//设置
        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvClose.setBackground(tvClosePic);

        SpannableString sHint=new SpannableString("手机号");
        AbsoluteSizeSpan tSize =new AbsoluteSizeSpan(15,true);
        sHint.setSpan(tSize,0,sHint.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhoneNum.setHint(sHint);
        btnPhoneNum.getBackground().setAlpha(111);

//      关闭页面，返回到调用页面-->login
        tvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        输入监听
        etPhoneNum.addTextChangedListener(etWatcher);
//        到图片验证码页面
        btnPhoneNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEditString();
                loadingDraw.show();
                postInformation(PHONE_NUMBER,phoneNum);
            }
        });
//        已注册，返回调用界面-->login
        tvPhoneHad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

//    数据转换为json格式数据字符串
    private String getJson(String phoneNumber)throws Exception{
        JSONObject jsonParam=new JSONObject();
        jsonParam.put("phone",phoneNumber);
        return jsonParam.toString();
    }

//    POST方式！！！！
     private void postInformation(String address, final String phoneNum){
        String jsonStr="";
        try {
            jsonStr=getJson(phoneNum);
        }catch (Exception e){
            e.printStackTrace();
        }
        RequestBody requestBody=RequestBody.create(JSON,jsonStr);
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
             public void onResponse(Call call, Response response) throws IOException {
                 try {
                     JSONObject jresp=new JSONObject(response.body().string());

                     switch (response.code()){
                         case HTTP_OK:
                         Message msg3=new Message();
                         msg3.what=DISMISS_DIALOG;
                         msg3.obj=loadingDraw;
                         msgHandler.sendMessage(msg3);

                         Intent intent=new Intent(RegisterPhoneNumActivity.this,RegisterPicCode.class);
                         SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(RegisterPhoneNumActivity.this ).edit();
                         editor.putString("next_key",jresp.getString("captcha_key"));
                         editor.putString("end_time",jresp.getJSONObject("expired_at").getString("date"));
                         editor.putString("img",jresp.getString("captcha_image_content"));
                         editor.putString("phoneNum",phoneNum);
                         editor.apply();
                         startActivity(intent);
//                                Log.e("Tag","captcha_key:"+jresp.getString("captcha_key")+"expired_at: "+jresp.getJSONObject("expired_at").getString("date")+"captcha_image_content: "+jresp.getString("captcha_image_content"));
                            finish();
                            break;

                         case HTTP_USER_NULL:
                             Message msg1=new Message();
                             msg1.what=DISMISS_DIALOG;
                             msg1.obj=loadingDraw;
                             msgHandler.sendMessage(msg1);

                             Message msg2=new Message();
                             msg2.what=HTTP_USER_NULL;
                             msg2.obj=jresp.getJSONObject("errors").getString("phone");
                             msgHandler.sendMessage(msg2);
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
