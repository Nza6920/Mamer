package com.example.my.mamer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.config.User;
import com.example.my.mamer.util.CircleImageView;
import com.example.my.mamer.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.USER_INFORMATION;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class UserFragment extends Fragment {
//    标题栏
//    mamer
    private TextView tvMamer;
//    头像
//    private ImageView imgUserAvatar;
    private CircleImageView imgUserAvatar;
//    mamer能量值
    private TextView tvUserMamerEnergy;
//    用户个人话题
    private TextView tvUserTopics;
//    用户关注
    private TextView tvUserattention;
//    用户收藏
    private TextView tvUserCollect;
//    活跃用户推荐
    private LinearLayout recommendUsersLayout;
//    更多活跃用户
    private TextView tvUserRecommendUsersMore;
//    资源推荐
    private LinearLayout recommendResourceLayout;
//    更多资源推荐
    private TextView tvRecommendResourceMore;

    //    UI
    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){

                case HTTP_USER_NULL:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OK:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();

                    break;
                default:
                    break;
                    }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user,container,false);
//        初始化控件
        imgUserAvatar=view.findViewById(R.id.user_avatar);
        tvUserMamerEnergy=view.findViewById(R.id.user_mamer_energy);
        tvUserTopics=view.findViewById(R.id.user_my_topic);
        tvUserattention=view.findViewById(R.id.user_my_attention);
        tvUserCollect=view.findViewById(R.id.user_my_collect);
        recommendUsersLayout=view.findViewById(R.id.user_recommend_users_layout);
        tvUserRecommendUsersMore=view.findViewById(R.id.user_recommend_users);
        recommendResourceLayout=view.findViewById(R.id.user_recommend_resource_layout);
        tvRecommendResourceMore=view.findViewById(R.id.user_recommend_resource);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        如果用户已登陆，则加载头像，没有登陆则显示未登录
        if (User.getUserPassKey()==null){
            new Thread(){
                @Override
                public void run() {
                    msgHandler.post(setNoLoginAvatar);
                }
            }.start();
        }else {
//            加载头像
            try {
                getInformationRequest();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


//        点击头像事件
        imgUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(),UserHomePageActivity.class);
                startActivity(intent);
            }
        });
//        点击推荐用户头像事件
//        显示出头像后，点击活跃用户头像，得到他的userId，来跳转到该用户的主页

    }



//    GET方式，只获取用户头像
    private void getInformationRequest() throws JSONException {
        HttpUtil.sendOkHttpRequestGet(USER_INFORMATION, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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
//       登陆时的默认头像
                            if (jresp.has("avatar")){
                                try {
                                    JSONObject imgCode=jresp.getJSONObject("avatar");
                                    if (imgCode.has("encoded")){
                                        User.setUserImg(imgCode.getString("encoded"));
                                        new Thread(){
                                            public void run(){
                                                msgHandler.post(setImgRunable);

                                            }
                                        }.start();
                                    }
                                }catch (Exception e){
//       登陆后修改后的头像
                                    String userAvatar=jresp.getString("avatar");
                                    User.setUserImgAvatar(userAvatar);
                                    Log.e("Tag",userAvatar);
                                    getAvatarRequest();
                                }
                            }
                            new Thread(){
                                public void run(){
                                    Message msg5=new Message();
                                    msg5.what=USER_SET_INFORMATION;
                                    msgHandler.sendMessage(msg5);
                                }
                            }.start();
                            break;
//                            令牌失效，重新请求
                        case HTTP_USER_ERROR:

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

//    登陆后的默认头像
    Runnable setImgRunable=new Runnable() {
        @Override
        public void run() {
            imgUserAvatar.setImageBitmap(loadPicCodeImg(User.getUserImg()));
        }
    };
//    登陆后的修改后的头像
    Runnable setAvatarBitmapRunable=new Runnable() {
        @Override
        public void run() {
            imgUserAvatar.setImageBitmap(User.getUserImgBitmap());
            Log.e("Tag", String.valueOf(User.getUserImgBitmap()));
        }
    };
//    未登陆时的默认头像
    Runnable setNoLoginAvatar =new Runnable() {
        @Override
        public void run() {
            Bitmap bitmap=BitmapFactory.decodeResource(getContext().getResources(),R.mipmap.ic_user_no_login);
            imgUserAvatar.setBitmap(bitmap);
        }
    };
    //  加载登陆后的默认头像
    private Bitmap loadPicCodeImg(String bicCodes){

        Bitmap bitmap=null;
        try {
            byte[] bitmapArray=Base64.decode(bicCodes.split(",")[1],Base64.DEFAULT);
            bitmap=BitmapFactory.decodeByteArray(bitmapArray,0,bitmapArray.length);
            imgUserAvatar.setBitmap(bitmap);
            imgUserAvatar.setmWidth(bitmap.getWidth());
            imgUserAvatar.setmHeight(bitmap.getHeight());
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    //    加载登陆后的修改后的头像
    private void getAvatarRequest(){
        HttpUtil.sendOkHttpRequestAvatar(User.getUserImgAvatar(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2=new Message();
                msg2.what=MESSAGE_ERROR;
                msg2.obj="服务器异常,获取头像失败(请检查网络)";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                byte[] bytes=(byte[])response.body().bytes();
                final Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imgUserAvatar.setBitmap(bitmap);
                imgUserAvatar.setmWidth(bitmap.getWidth());
                imgUserAvatar.setmHeight(bitmap.getHeight());
                User.setUserImgBitmap(bitmap);
                Log.e("Tag", String.valueOf(User.getUserImgBitmap()));
                final Runnable setAvatarRunable=new Runnable() {
                    @Override
                    public void run() {
                        imgUserAvatar.setImageBitmap(bitmap);
                    }
                };
                new Thread(){
                    public void run(){
                        msgHandler.post(setAvatarRunable);
                    }
                }.start();

            }
        });
    }





    //    处理并显示数据
    private void showUserInfo(){

    }

}
