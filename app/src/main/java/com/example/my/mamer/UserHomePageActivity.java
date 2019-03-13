package com.example.my.mamer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.config.User;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OVERTIME;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;

public class UserHomePageActivity extends AppCompatActivity {
//头像
    private ImageView imgUserAvatar;
//    编辑按钮
    private TextView tvUserEditor;
//    显示内容
    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserIntroduction;
//返回
    private TextView tvBack;

    private LoadingDraw loadingDraw;
    //ui
    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_NULL:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_ERROR:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OVERTIME:
                    Toast.makeText(UserHomePageActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);

//        登录了
//        if (user.getUserPassKey()!=null){
//            Log.e("Tag","Login IN");
//            try {
//                getInformationRequest();
//                loadPicCodeImg(user.getUserImg());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }else {
////            未登录
//            Log.e("Tag","Login OFF");
//        }
        loadingDraw=new LoadingDraw(this);
        init();
        try {
            getInformationRequest();
            loadPicCodeImg(User.getUserImg());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        tvBack=findViewById(R.id.title_tv_close);
        imgUserAvatar=findViewById(R.id.user_home_page_avatar);
        tvUserEditor=findViewById(R.id.user_home_page_editor);
        tvUserName=findViewById(R.id.user_home_page_name);
        tvUserEmail=findViewById(R.id.user_home_page_email);
        tvUserIntroduction=findViewById(R.id.user_home_page_introduction);

        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvBack.setBackground(tvClosePic);

        SpannableString sHint=new SpannableString("Tomorrow will be better！");
        AbsoluteSizeSpan tSize =new AbsoluteSizeSpan(15,true);
        sHint.setSpan(tSize,0,sHint.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUserIntroduction.setHint(sHint);

        tvUserName.setText(User.getUserName());
        tvUserEmail.setText(User.getUserEmail());

//        监听
//        点击返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserHomePageActivity.this,CenterActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        点击头像上传照片
        imgUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        点击编辑
        tvUserEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserHomePageActivity.this,UserEditorInformationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
//    GET方式
    private void getInformationRequest() throws JSONException {
        String address="https://mamer.club/api/user?Authorization="+"Bearer "+User.getUserPassKey();
        HttpUtil.sendOkHttpRequestGet(address, new Callback() {
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
                        case HTTP_USER_GET_INFORMATION:
                            Message msg3=new Message();
                            msg3.what=DISMISS_DIALOG;
                            msg3.obj=loadingDraw;
                            msgHandler.sendMessage(msg3);

                             User.setUserId(jresp.getString("id"));
                             User.setUserName(jresp.getString("name"));
                             User.setUserEmail(jresp.getString("email"));
                            JSONObject imgCode=jresp.getJSONObject("avatar");
                            User.setUserImg(imgCode.getString("encoded"));
                            break;
//                            令牌失效，重新请求
                        case HTTP_USER_ERROR:
                            Message msg4=new Message();
                            msg4.what=DISMISS_DIALOG;
                            msg4.obj=loadingDraw;
                            msgHandler.sendMessage(msg4);

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
//加载头像
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
}
