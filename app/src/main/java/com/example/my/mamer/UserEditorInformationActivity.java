package com.example.my.mamer;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.config.User;
import com.example.my.mamer.util.CircleImageView;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;
import com.example.my.mamer.util.PhotoPopupWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_FORMAT_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MEDIA_TYPE_IMAGE;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.RESULT_LODA_IMAGE;
import static com.example.my.mamer.config.Config.USER_AVATAR_IMG;
import static com.example.my.mamer.config.Config.USER_INFORMATION;

public class UserEditorInformationActivity extends AppCompatActivity {
    private static final MediaType XWWW=MediaType.parse("application/x-www-form-urlencoded");
//    头像
    private CircleImageView imgUserInformationAvatar;
    private PhotoPopupWindow photoPopupWindow;
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
                case HTTP_USER_NULL:
                    Toast.makeText(UserEditorInformationActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_USER_FORMAT_ERROR:
                    Toast.makeText(UserEditorInformationActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OK:
                    Toast.makeText(UserEditorInformationActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();

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

//        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
//        tvBack.setBackground(tvClosePic);
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
//        tvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(UserEditorInformationActivity.this,UserHomePageActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        提交
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    loadingDraw.show();
                    patchInformation();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
//        修改头像
        tvUserEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoPopupWindow=new PhotoPopupWindow(UserEditorInformationActivity.this);
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
        getEditString();
        if (User.getUserImgId()!=null){
//            修改头像
            RequestBody requestBody=new FormBody.Builder()
                    .add("avatar_image_id",User.getUserImgId())
                    .add("name",userName)
                    .add("introduction",userInformation)
                    .build();


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

                                User.setUserId(jresp.getString("id"));
                                User.setUserName(jresp.getString("name"));
                                User.setUserImgAvatar(jresp.getString("avatar"));
                                User.setUserIntroduction(jresp.getString("introduction"));
                                User.setEmail_verified(jresp.getBoolean("email_verified"));
                                User.setUserBornDate(jresp.getString("bound_phone"));
                                Log.e("Tag","修改后"+String.valueOf(User.getUserImgAvatar()));

                                Intent intent=new Intent(UserEditorInformationActivity.this,UserHomePageActivity.class);
                                startActivity(intent);
                                finish();
                                break;
//                            422
                            case HTTP_USER_NULL:
                                Message msg4=new Message();
                                msg4.what=DISMISS_DIALOG;
                                msg4.obj=loadingDraw;
                                msgHandler.sendMessage(msg4);

                                if (jresp.has("errors")){
                                    JSONObject  errorStr=jresp.getJSONObject("errors");
                                    if (errorStr.has("name")){
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj=errorStr.getString("name");
                                        msgHandler.sendMessage(msg5);
                                    }else if (errorStr.has("image")){
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj="图片上传失败,图片不可过大或过小";
                                        msgHandler.sendMessage(msg5);
                                    }
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
        }else {
//            未修改头像
            RequestBody requestBody=new FormBody.Builder()
                    .add("name",userName)
                    .add("introduction",userInformation)
                    .build();
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

                                User.setUserId(jresp.getString("id"));
                                User.setUserName(jresp.getString("name"));
                                User.setUserImgAvatar(jresp.getString("avatar"));
                                User.setUserIntroduction(jresp.getString("introduction"));
                                User.setEmail_verified(jresp.getBoolean("email_verified"));
                                User.setUserBornDate(jresp.getString("bound_phone"));
                                Log.e("Tag","修改后"+String.valueOf(User.getUserImgAvatar()));

                                Intent intent=new Intent(UserEditorInformationActivity.this,UserHomePageActivity.class);
                                startActivity(intent);
                                finish();
                                break;
//                            422
                            case HTTP_USER_NULL:
                                Message msg4=new Message();
                                msg4.what=DISMISS_DIALOG;
                                msg4.obj=loadingDraw;
                                msgHandler.sendMessage(msg4);

                                if (jresp.has("errors")){
                                    JSONObject  errorStr=jresp.getJSONObject("errors");
                                    if (errorStr.has("name")){
                                        Message msg5=new Message();
                                        msg5.what=response.code();
                                        msg5.obj=errorStr.getString("name");
                                        msgHandler.sendMessage(msg5);
                                    }
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

//回调图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
//            从相册获取
            if (requestCode==RESULT_LODA_IMAGE&& null!=data){
//判断手机系统版本号
                try {
                    handleImageOnKitKat(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
//            else if (requestCode==RESULT_CAMERA_IMAGE){
////                实时拍照
//                Bitmap bitmap=null;
//
//
//            }
        }

    }
//     上传头像
    private void imgUpLoad(final String localPath) throws JSONException {
        loadingDraw.show();

        if (localPath==null){
            Message msg1=new Message();
            msg1.what=DISMISS_DIALOG;
            msg1.obj=loadingDraw;
            msgHandler.sendMessage(msg1);

            Message msg10=new Message();
            msg10.what=MESSAGE_ERROR;
            msg10.obj="上传失败";
            msgHandler.sendMessage(msg10);
        }else {


            MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            File file = new File(localPath);
            builder.addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE_IMAGE, file))
                    .addFormDataPart("type", "avatar");
            MultipartBody requestBody = builder.build();
            HttpUtil.sendOkHttpRequestAvatars(USER_AVATAR_IMG, requestBody, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg1 = new Message();
                    msg1.what = DISMISS_DIALOG;
                    msg1.obj = loadingDraw;
                    msgHandler.sendMessage(msg1);

                    Message msg2 = new Message();
                    msg2.what = MESSAGE_ERROR;
                    msg2.obj = "上传失败";
                    msgHandler.sendMessage(msg2);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        JSONObject jresp = new JSONObject(response.body().string());
                        switch (response.code()) {
//                        201
                            case HTTP_OK:
                                Message msg3 = new Message();
                                msg3.what = DISMISS_DIALOG;
                                msg3.obj = loadingDraw;
                                msgHandler.sendMessage(msg3);

                                User.setUserImgId(jresp.getString("id"));
                                User.setUserId(jresp.getString("user_id"));
                                User.setUserImgAvatar(jresp.getString("path"));
                                Log.e("Tag", "修改后，，" + String.valueOf(User.getUserImgAvatar()));
                                displayImage(localPath);
                                break;
//                            422
                            case HTTP_USER_NULL:
                                Message msg4 = new Message();
                                msg4.what = DISMISS_DIALOG;
                                msg4.obj = loadingDraw;
                                msgHandler.sendMessage(msg4);

                                if (jresp.has("errors")) {
                                    JSONObject errorStr = jresp.getJSONObject("errors");
                                    if (errorStr.has("type")) {
                                        Message msg5 = new Message();
                                        msg5.what = response.code();
                                        msg5.obj = errorStr.getString("type");
                                        msgHandler.sendMessage(msg5);
                                    } else if (errorStr.has("image")) {
                                        Message msg6 = new Message();
                                        msg6.what = response.code();
                                        msg6.obj = "图片上传失败，图片可能过大或过小";
                                        msgHandler.sendMessage(msg6);
                                    }
                                }


                                break;
//                            403
                            case HTTP_USER_FORMAT_ERROR:
                                Message msg7 = new Message();
                                msg7.what = DISMISS_DIALOG;
                                msg7.obj = loadingDraw;
                                msgHandler.sendMessage(msg7);

                                Message msg8 = new Message();
                                msg8.what = response.code();
                                msg8.obj = "格式不支持";
                                msgHandler.sendMessage(msg8);
                                break;
//                    401
                            case HTTP_USER_ERROR:
                                Message msg9 = new Message();
                                msg9.what = DISMISS_DIALOG;
                                msg9.obj = loadingDraw;
                                msgHandler.sendMessage(msg9);

                                Authenticator authenticator = new Authenticator() {
                                    @Override
                                    public Request authenticate(Route route, Response response) throws IOException {
//    刷新token
                                        return response.request().newBuilder().addHeader("Authorization", User.getUserPassKey_type() + User.getUserPassKey()).build();
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
    }
//    处理图片
    private void handleImageOnKitKat(Intent data) throws JSONException {
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
//                解析出数字格式id
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri=ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }

        imgUpLoad(imagePath);
    }
//    图片路径
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
//    显示头像
    private void displayImage(String imagePath){
        if (imagePath!=null){
           final Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            imgUserInformationAvatar.setBitmap(bitmap);
            imgUserInformationAvatar.setmWidth(bitmap.getWidth());
            imgUserInformationAvatar.setmHeight(bitmap.getHeight());
            User.setUserImgBitmap(bitmap);

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
        }else {
            Toast.makeText(this,"显示失败",Toast.LENGTH_SHORT).show();
        }
    }
}
