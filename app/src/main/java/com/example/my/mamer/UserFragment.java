package com.example.my.mamer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.bean.RecommendResource;
import com.example.my.mamer.bean.User;
import com.example.my.mamer.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.UNLOGIN;
import static com.example.my.mamer.config.Config.USER_REPLY_COUNT;
import static com.example.my.mamer.config.Config.USER_TOPIC_COUNT;

public class UserFragment extends Fragment implements View.OnClickListener {

//    mamer能量值
    private TextView tvUserMamerEnergy;
    private LinearLayout userMamerEnergyLayout;
    private LinearLayout userUnloginLayout;
//    用户个人话题
    private LinearLayout layoutTopics;
    private TextView tvUserTopicCount;
    private int userTopicCount;
//    用户回复
    private LinearLayout layoutReply;
    private TextView tvUserReplyCount;
    private int userReplyCount;
//    用户收藏
    private LinearLayout layoutCollect;
    private TextView tvUserCollect;
//    活跃用户推荐,自定义recyclerView,适配器
    private ImageView imgUserRecommendAvatar;
    private TextView tvUserRecommendName;
    private TextView tvUserRecommendInfo;
    private Button btnAdd;
    private ArrayList<User> userRecommendDataList=new ArrayList<>();
    private LinearLayout userRecommendLayout;
    private LinearLayout userRecommendNoneLayout;
//    资源推荐
    private TextView tvRecommendResourceZ;
    private TextView tvRecommendResourceO;
    private TextView tvRecommendResourceT;
    private ArrayList<RecommendResource> recommendResourceList=new ArrayList<>();
//    更多资源推荐
    private TextView tvRecommendResourceMore;

    //    UI
    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){

                case HTTP_USER_NULL:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OK:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case UNLOGIN:
                    Toast.makeText(getActivity(),"登陆以体验更多",Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case USER_TOPIC_COUNT:
                    tvUserTopicCount.setText(String.valueOf(userTopicCount));
                    break;
                case USER_REPLY_COUNT:
                    tvUserReplyCount.setText(String.valueOf(userReplyCount));
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user,container,false);
//        初始化控件
        tvUserMamerEnergy=view.findViewById(R.id.user_mamer_energy);
        userMamerEnergyLayout=view.findViewById(R.id.user_mamer_energy_layout);
        userUnloginLayout=view.findViewById(R.id.user_un_login_layout);
        tvUserTopicCount=view.findViewById(R.id.user_my_topic_count);
        layoutTopics=view.findViewById(R.id.user_my_topic);
        tvUserReplyCount=view.findViewById(R.id.user_my_reply_count);
        layoutReply=view.findViewById(R.id.user_my_reply);
        tvUserCollect=view.findViewById(R.id.user_my_collect_count);
        layoutCollect=view.findViewById(R.id.user_my_collect);
        imgUserRecommendAvatar=view.findViewById(R.id.user_recommend_users_img);
        tvUserRecommendName=view.findViewById(R.id.user_recommend_users_name);
        tvUserRecommendInfo=view.findViewById(R.id.user_recommend_users_info);
        btnAdd=view.findViewById(R.id.user_recommend_users_add);
        userRecommendLayout=view.findViewById(R.id.user_recommend_users);
        userRecommendNoneLayout=view.findViewById(R.id.user_recommend_users_none);
        tvRecommendResourceZ=view.findViewById(R.id.user_recommend_resource_1);
        tvRecommendResourceO=view.findViewById(R.id.user_recommend_resource_2);
        tvRecommendResourceT=view.findViewById(R.id.user_recommend_resource_3);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        //        判断是否登录
        if (MyApplication.globalUserInfo.token ==null){
            userUnloginLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    跳到登录界面
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }
            });

        }else {
            userUnloginLayout.setVisibility(View.GONE);
            userMamerEnergyLayout.setVisibility(View.VISIBLE);

//            请求数据，个人话题数，个人回复数，个人收藏数
            getUserTopicsCount(1);
            getUserReply(1);


        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
 //        获取数据
        getUserRecommend();
        getRecommendResource();
//        点击事件
        layoutTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent=new Intent(getActivity(),UserSelfTopicListActivity.class);
                    startActivity(intent);
                }
            }
        });
        layoutReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent=new Intent(getActivity(),UserSelfReplyActivity.class);
                    startActivity(intent);
                }
            }
        });

        layoutCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
//                    Intent intent=new Intent(getActivity(),);
//                    startActivity(intent);
                }
            }
        });

        btnAdd.setOnClickListener(this);
        tvRecommendResourceZ.setOnClickListener(this);
        tvRecommendResourceO.setOnClickListener(this);
        tvRecommendResourceT.setOnClickListener(this);


    }
//    获取用户个人话题数
    private void  getUserTopicsCount(int pageCount){
        HttpUtil.sendOkHttpGetUserTopicList(MyApplication.globalUserInfo.user.getUserId(), pageCount, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp = null;
                try{
                    jresp = new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("meta")) {
                                jresp=jresp.getJSONObject("meta");
                                if (jresp.has("pagination")){
                                    jresp=jresp.getJSONObject("pagination");
                                    userTopicCount= Integer.parseInt(jresp.getString("count"));
                                }
                                final Runnable setUserCount=new Runnable() {
                                    @Override
                                    public void run() {
                                        Message msg3 = new Message();
                                        msg3.what = USER_TOPIC_COUNT;
                                        msgHandler.sendMessage(msg3);
                                    }};
                                new Thread(){
                                    public void run(){
                                        msgHandler.post(setUserCount);
                                    }
                                }.start();
                            }
                            break;
                        default:
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
//    获取用户个人回复数
    private void getUserReply(int pageCount){

        HttpUtil.sendOkHttpGetUserReplyList(MyApplication.globalUserInfo.user.getUserId(),pageCount, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp=null;

                try {
                    jresp=new JSONObject(response.body().toString());
                    if (jresp.has("meta")){
                        jresp=jresp.getJSONObject("meta");
                        if (jresp.has("pagination")){
                            jresp=jresp.getJSONObject("pagination");
                            userReplyCount= Integer.parseInt(jresp.getString("total"));
                        }
                        final Runnable setUserCount=new Runnable() {
                            @Override
                            public void run() {
                                Message msg1=new Message();
                                msg1.what=USER_REPLY_COUNT;
                                msgHandler.sendMessage(msg1);
                            }};
                        new Thread(){
                            public void run(){
                                msgHandler.post(setUserCount);
                            }
                        }.start();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
//    获取活跃用户信息
    private void getUserRecommend(){

        HttpUtil.sendOkHttpGetUserRecommend(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp = null;
                JSONArray jsonArray=null;
                try {
                    jresp = new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")) {
                                jsonArray=jresp.getJSONArray("data");
                                if (jsonArray!=null){
                                    for (int i=0;i<1;i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        User user=new User();
                                        user.setUserId(jsonObject.getString("id"));
                                        user.setUserName(jsonObject.getString("name"));
                                        user.setUserImg(jsonObject.getString("avatar"));
                                        user.setUserIntroduction(jsonObject.getString("introduction"));

                                        userRecommendDataList.add(user);
                                    }

                                    final Runnable setAvatarRunable=new Runnable() {
                                        @Override
                                        public void run() {
                                            RequestOptions options=new RequestOptions()
                                                    .error(R.mipmap.ic_image_error)
                                                    .placeholder(R.mipmap.ic_image_error);
                                            Glide.with(getContext())
                                                    .asBitmap()
                                                    .load(userRecommendDataList.get(0).getUserImg())
                                                    .apply(options)
                                                    .into(imgUserRecommendAvatar);
                                            tvUserRecommendName.setText(userRecommendDataList.get(0).getUserName());
                                            if (userRecommendDataList.get(0).getUserIntroduction().equals("null")){
                                                tvUserRecommendInfo.setText("没有留下信息哦~");
                                            }else {
                                                tvUserRecommendInfo.setText(userRecommendDataList.get(0).getUserIntroduction());
                                            }
                                            userRecommendNoneLayout.setVisibility(View.GONE);
                                            userRecommendLayout.setVisibility(View.VISIBLE);
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setAvatarRunable);
                                        }
                                    }.start();
                                }

                            }
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

//    资源推荐
    private void getRecommendResource(){
        HttpUtil.sendOkHttpGetRecommendResource(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp = null;
                JSONArray jsonArray=null;

                try {
                    jresp = new JSONObject(response.body().string());
                    switch (response.code()) {
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")) {
                                jsonArray = jresp.getJSONArray("data");
                                for (int i=0;i<3;i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    RecommendResource recommendResource=new RecommendResource();
                                    recommendResource.setId(jsonObject.getString("id"));
                                    recommendResource.setTitle(jsonObject.getString("title"));
                                    recommendResource.setLink(jsonObject.getString("link"));

                                    recommendResourceList.add(recommendResource);
                                }
                                final Runnable setAvatarRunable=new Runnable() {
                                    @Override
                                    public void run() {
                                        tvRecommendResourceZ.setText(recommendResourceList.get(0).getTitle());
                                        tvRecommendResourceO.setText(recommendResourceList.get(1).getTitle());
                                        tvRecommendResourceT.setText(recommendResourceList.get(2).getTitle());

                                    }};
                                new Thread(){
                                    public void run(){
                                        msgHandler.post(setAvatarRunable);
                                    }
                                }.start();
                            }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.user_recommend_users_add:
                break;
            case R.id.user_recommend_resource_1:
                openLink(recommendResourceList.get(0).getLink());
                break;
            case R.id.user_recommend_resource_2:
                openLink(recommendResourceList.get(1).getLink());
                break;
            case R.id.user_recommend_resource_3:
                openLink(recommendResourceList.get(2).getLink());
                break;
//                个人中心的功能，编辑个人信息，生成用户二维码
            case R.id.user_top_bar:
                break;
                default:
                    break;
        }
    }
    private void openLink(String str){
        Intent intent=new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(str));
        startActivity(intent);
    }
}
