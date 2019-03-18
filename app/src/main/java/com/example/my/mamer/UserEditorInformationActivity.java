package com.example.my.mamer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.my.mamer.config.User;
import com.example.my.mamer.util.CircleImageView;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.RESULT_CAMERA_IMAGE;
import static com.example.my.mamer.config.Config.RESULT_LODA_IMAGE;
import static com.example.my.mamer.config.Config.USER_AVATAR_IMG;
import static com.example.my.mamer.config.Config.USER_INFORMATION;

public class UserEditorInformationActivity extends AppCompatActivity {
    private static final MediaType XWWW=MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
//    头像
    private CircleImageView imgUserInformationAvatar;
    private String userAvatar;
//    头像按钮
    private TextView tvUserEditor;
//    个人信息
    private EditText etUserName;
    private String userName;
    private TextView etUserEmail;
    private EditText etUserIntroduction;
    private String userInformation;
//    返回按钮
    private TextView tvBack;
    private Button btnFinish;

    private LoadingDraw loadingDraw;

//    UI
    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;

                default:
                    break;
            }
        }
};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edito_information);

        loadingDraw=new LoadingDraw(this);
        init();
    }

    private void init(){
        tvBack=findViewById(R.id.title_tv_close);
        btnFinish=findViewById(R.id.title_btn_next);
        imgUserInformationAvatar=findViewById(R.id.user_information_avatar);
        tvUserEditor=findViewById(R.id.user_information_editor);
        etUserName=findViewById(R.id.user_information_name);
        etUserEmail=findViewById(R.id.user_information_email);
        etUserIntroduction=findViewById(R.id.user_information_introduction);

        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvClosePic);
        btnFinish.setText("完成");

        if (User.getUserImgBitmap()!=null){
            Message msg3=new Message();
            msg3.what=DISMISS_DIALOG;
            msg3.obj=loadingDraw;
            msgHandler.sendMessage(msg3);

            new Thread(){
                public void run(){
                    msgHandler.post(setAvatarBitmapRunable);
                }
            }.start();
        }else {
            Message msg3=new Message();
            msg3.what=DISMISS_DIALOG;
            msg3.obj=loadingDraw;
            msgHandler.sendMessage(msg3);

            new Thread(){
                public void run(){
                    msgHandler.post(setImgRunable);
                }
            }.start();
        }


        etUserName.setText(User.getUserName());
        etUserEmail.setText(User.getUserEmail());
        etUserIntroduction.setText(User.getUserIntroduction());

//        点击事件
//        返回
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserEditorInformationActivity.this,UserHomePageActivity.class);
                startActivity(intent);
                finish();
            }
        });
//        提交
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    getEditString();
                    patchInformation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }
//    获取用户输入值
    private void getEditString(){
        userName=etUserName.getText().toString().trim();
        userInformation=etUserIntroduction.getText().toString().trim();
    }
//    PATC提交信息
    private void patchInformation() throws JSONException {
        JSONObject jsonParam=new JSONObject();
        jsonParam.put("name",userName);
        jsonParam.put("introduction",userInformation);
        String jsonStr=jsonParam.toString();

        RequestBody requestBody=RequestBody.create(XWWW,jsonStr);
        HttpUtil.sendOkHttpRequestPatch(USER_INFORMATION,requestBody, new Callback() {
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

                            Intent intent=new Intent(UserEditorInformationActivity.this,UserHomePageActivity.class);
                            User.setUserId(jresp.getString("id"));
                            User.setUserName(jresp.getString("name"));
                            User.setUserIntroduction(jresp.getString("introduction"));
                            User.setUserBornDate(jresp.getString("bound_phone"));
                            startActivity(intent);
                            finish();
//                            422
                        case HTTP_USER_NULL:
                            Message msg4=new Message();
                            msg4.what=DISMISS_DIALOG;
                            msg4.obj=loadingDraw;
                            msgHandler.sendMessage(msg4);

                            String jrespStr=jresp.getString("errors");
                            JSONObject  errorStr=jresp.getJSONObject(jrespStr);
                            if (errorStr.has("name")){
                                Message msg5=new Message();
                                msg5.what=response.code();
                                msg5.obj=errorStr.getString("name");
                                msgHandler.sendMessage(msg5);
                            }
                            break;
//                            401,令牌失效，重新请求
                        case HTTP_USER_ERROR:
                            Message msg6=new Message();
                            msg6.what=DISMISS_DIALOG;
                            msg6.obj=loadingDraw;
                            msgHandler.sendMessage(msg6);

                            Authenticator authenticator=new Authenticator() {
                                @Override
                                public Request authenticate(Route route, Response response) throws IOException {
//    刷新token
                                    return response.request().newBuilder().addHeader("Authorization", User.getUserPassKey_type()+User.getUserPassKey()).build();
                                }
                            };
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
            imgUserInformationAvatar.setBitmap(bitmap);
            imgUserInformationAvatar.setmWidth(bitmap.getWidth());
            imgUserInformationAvatar.setmHeight(bitmap.getHeight());
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
    Runnable setImgRunable=new Runnable() {
        @Override
        public void run() {
            imgUserInformationAvatar.setImageBitmap(loadPicCodeImg(User.getUserImg()));
        }
    };
    Runnable setAvatarBitmapRunable=new Runnable() {
        @Override
        public void run() {
            imgUserInformationAvatar.setImageBitmap(User.getUserImgBitmap());
            Log.e("Tag", String.valueOf(User.getUserImgBitmap()));
        }
    };

//得到拍摄的图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
//            从相册获取
            if (requestCode==RESULT_LODA_IMAGE&& null!=data){
                Uri selectedImg=data.getData();
                String[] filePaths={MediaStore.Images.Media.DATA};
                Cursor cursor=getContentResolver().query(selectedImg,filePaths,null,null,null);
                cursor.moveToFirst();

                int pathsIndex=cursor.getColumnIndex(filePaths[0]);
                final String photoPath=cursor.getString(pathsIndex);
                imgUpLoad(photoPath);
                cursor.close();

            }else if (requestCode==RESULT_CAMERA_IMAGE){
//                实时拍照
                Bitmap bitmap=null;
                

            }
        }

    }
//上传头像
    private void imgUpLoad(String localPath){
        File file=new File(localPath);
        MediaType MEDIA_TYPE=MediaType.parse("image/*");
        MultipartBody.Builder builder=new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("file",file.getName(),RequestBody.create(MEDIA_TYPE,file));
        final MultipartBody requestBody=builder.build();
        HttpUtil.sendOkHttpRequestAvatars(USER_AVATAR_IMG, requestBody, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2=new Message();
                msg2.what=MESSAGE_ERROR;
                msg2.obj="上传失败";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());
                    switch (response.code()){
//                        201
                        case HTTP_OK:
                            Message msg3=new Message();
                            msg3.what=DISMISS_DIALOG;
                            msg3.obj=loadingDraw;
                            msgHandler.sendMessage(msg3);

                            User.setUserImgId(jresp.getString("id"));
                            User.setUserId(jresp.getString("user_id"));
                            User.setUserPassKey_type(jresp.getString("type"));
                            User.setUserImgAvatar(jresp.getString("path"));
                            getAvatarRequest();
                            break;
//                    401
                        case HTTP_USER_ERROR:
                            Message msg6=new Message();
                            msg6.what=DISMISS_DIALOG;
                            msg6.obj=loadingDraw;
                            msgHandler.sendMessage(msg6);

                            Authenticator authenticator=new Authenticator() {
                                @Override
                                public Request authenticate(Route route, Response response) throws IOException {
//    刷新token
                                    return response.request().newBuilder().addHeader("Authorization", User.getUserPassKey_type()+User.getUserPassKey()).build();
                                }
                            };
                            default:
                                break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    //    获取修改后的头像
    private void getAvatarRequest() {
        //                            上传了头像后，下载头像
        HttpUtil.sendOkHttpRequestAvatar(User.getUserImgAvatar(), new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                            上传成功，将图片显示
                byte[] bytes = (byte[]) response.body().bytes();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgUserInformationAvatar.setBitmap(bitmap);
                imgUserInformationAvatar.setmWidth(bitmap.getWidth());
                imgUserInformationAvatar.setmHeight(bitmap.getHeight());
                User.setUserImgBitmap(bitmap);

                Log.e("Tag", String.valueOf(User.getUserImgBitmap()));
                final Runnable setAvatarRunable = new Runnable() {
                    @Override
                    public void run() {
                        imgUserInformationAvatar.setImageBitmap(bitmap);
                    }
                };
                new Thread() {
                    public void run() {
                        msgHandler.post(setAvatarRunable);
                    }
                }.start();
            }
        });
    }

}
