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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;
import com.example.my.mamer.util.OverTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

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
import static com.example.my.mamer.config.Config.PHONE_NUMBER;
import static com.example.my.mamer.config.Config.PIC_CODE;

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

    //        UI
   private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(RegisterPicCode.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_ERROR:
                    Toast.makeText(RegisterPicCode.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_NULL:
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
        loadingDraw =new LoadingDraw(this);
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
                loadingDraw.show();
                try {
                    requestAgain();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//      验证按钮
        btnCodeStr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDraw.show();
//      函数返回的值将作为是否跳转下一个页面的标准，验证码正确就跳转到注册页面，不正确就提示用户重新输入并且刷新验证码！！！
                postInformation(PIC_CODE);
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
//    得到输入内容
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
    SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
    String picCodekey=prefs.getString("next_key","");
    jsonParam.put("captcha_key",picCodekey);
    jsonParam.put("captcha_code",codeStr);
    return jsonParam.toString();
}
//    POST方式！！！！
    private void postInformation(String address){
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

                        Intent intent=new Intent(RegisterPicCode.this,RegisterActivity.class);
                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this ).edit();
                        editor.putString("new_key",jresp.getString("key"));
                        editor.putString("new_time",jresp.getString("expired_at"));
                        editor.putString("captcha_code",codeStr);
                        editor.apply();
                        startActivity(intent);
//                                Log.e("Tag","captcha_key:"+jresp.getString("captcha_key")+"expired_at: "+jresp.getJSONObject("expired_at").getString("date")+"captcha_image_content: "+jresp.getString("captcha_image_content"));
                        finish();
                        case HTTP_USER_ERROR:
                            Message msg4=new Message();
                            msg4.what=DISMISS_DIALOG;
                            msg4.obj=loadingDraw;
                            msgHandler.sendMessage(msg4);

                            Message msg5=new Message();
                            msg5.what=response.code();
                            msg5.obj=jresp.getString("message");
                            msgHandler.sendMessage(msg5);
                            break;
                        case HTTP_USER_NULL:
                            Message msg1=new Message();
                            msg1.what=DISMISS_DIALOG;
                            msg1.obj=loadingDraw;
                            msgHandler.sendMessage(msg1);

                            Message msg2=new Message();
                            msg2.what=response.code();
                            msg2.obj=jresp.getString("message");
                            msgHandler.sendMessage(msg2);
                            previous();
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
//    重新请求
    private void requestAgain() throws JSONException {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String picCodekey=prefs.getString("next_key","");
        String phoneNum=prefs.getString("phoneNum","");
        JSONObject jsonParam=new JSONObject();
        jsonParam.put("next_key",picCodekey);
        jsonParam.put("phone",phoneNum);
        String jsonStr=jsonParam.toString();

        RequestBody requestBody=RequestBody.create(JSON,jsonStr);
        HttpUtil.sendOkHttpRequest(PHONE_NUMBER, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this);
                String picCodeEndTime=prefs.getString("end_time","");
                try {
                    OverTime overTimeStr=new OverTime();
                    boolean overTime=overTimeStr.endTime(picCodeEndTime);
                    if (overTime){ Message msg1=new Message();
                        msg1.what=DISMISS_DIALOG;
                        msg1.obj=loadingDraw;
                        msgHandler.sendMessage(msg1);

                        Message msg2=new Message();
                        msg2.what=MESSAGE_ERROR;
                        msg2.obj="服务器异常,请检查网络";
                        msgHandler.sendMessage(msg2);
                    }else {
                        Message msg3=new Message();
                        msg3.what=DISMISS_DIALOG;
                        msg3.obj=loadingDraw;
                        msgHandler.sendMessage(msg3);

                        Message msg4=new Message();
                        msg4.what=HTTP_USER_NULL;
                        msg4.obj="当前验证码已失效，无法再次请求";
                        msgHandler.sendMessage(msg4);
                        previous();
                    }
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());
                    Message msg1=new Message();
                    msg1.what=DISMISS_DIALOG;
                    msg1.obj=loadingDraw;
                    msgHandler.sendMessage(msg1);

                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this ).edit();
                    editor.putString("next_key",jresp.getString("captcha_key"));
                    editor.putString("end_time",jresp.getJSONObject("expired_at").getString("date"));
                    editor.putString("img",jresp.getString("captcha_image_content"));
                    editor.apply();
//更新UI
                    new Thread(){
                        public void run(){

                            msgHandler.post(setImgRunable);
                        }
                    }.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }
//    跳转
    private void previous(){
        Intent intent=new Intent(RegisterPicCode.this,RegisterPhoneNumActivity.class);
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(this ).edit();
        editor.clear().apply();
        startActivity(intent);
        finish();
    }
//UI
    Runnable setImgRunable=new Runnable() {
    @Override
    public void run() {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(RegisterPicCode.this);
        String picCode=prefs.getString("img","");
        picCodeImg.setImageBitmap(loadPicCodeImg(picCode));
    }
};
}
