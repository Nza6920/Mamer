package com.example.my.mamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import com.example.my.mamer.bean.User;
import com.example.my.mamer.config.GlobalUserInfo;
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

public class UserFragment extends Fragment {

//    mamer能量值
    private TextView tvUserMamerEnergy;
    private LinearLayout userMamerEnergyLayout;
    private LinearLayout userUnloginLayout;
//    用户个人话题
    private LinearLayout layoutTopics;
    private TextView tvUserTopics;
//    用户关注
    private TextView tvUserattention;
//    用户收藏
    private TextView tvUserCollect;
//    活跃用户推荐,自定义recyclerView,适配器
    private LinearLayout recommendUsersLayout;
    public RecyclerView mRecyclerView;
    private ArrayList<User> userRecommendDataList=new ArrayList<>();
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
                case UNLOGIN:
                    Toast.makeText(getActivity(),"登陆以体验更多",Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_ERROR:
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
        tvUserMamerEnergy=view.findViewById(R.id.user_mamer_energy);
        userMamerEnergyLayout=view.findViewById(R.id.user_mamer_energy_layout);
        userUnloginLayout=view.findViewById(R.id.user_un_login_layout);
        tvUserTopics=view.findViewById(R.id.user_my_topic_count);
        layoutTopics=view.findViewById(R.id.user_my_topic);
        tvUserattention=view.findViewById(R.id.user_my_attention);
        tvUserCollect=view.findViewById(R.id.user_my_collect);
        recommendUsersLayout=view.findViewById(R.id.user_recommend_users);
        recommendResourceLayout=view.findViewById(R.id.user_recommend_resource_layout);
        tvRecommendResourceMore=view.findViewById(R.id.user_recommend_resource);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        判断是否登录
        if (GlobalUserInfo.userInfo.token ==null){
            userUnloginLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getActivity(),RegisterPhoneNumActivity.class);
                    startActivity(intent);
                }
            });

        }else {
            userUnloginLayout.setVisibility(View.GONE);
            userMamerEnergyLayout.setVisibility(View.VISIBLE);
        }
//        点击事件
        layoutTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GlobalUserInfo.userInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent=new Intent(getActivity(),UserSelfTopicListActivity.class);
                    startActivity(intent);
                }
            }
        });
//        获取数据
        getUserRecommend();
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
                                int jsonSize=jsonArray.length();
                                for (int i=0;i<jsonSize;i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    User user=new User();
                                    user.setUserId(jsonObject.getString("id"));
                                    user.setUserName(jsonObject.getString("name"));
                                    user.setUserImg(jsonObject.getString("avatar"));
                                    user.setUserIntroduction(jsonObject.getString("introduction"));

                                    userRecommendDataList.add(user);
                                }
                                showUserRecommend(userRecommendDataList);
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
    //    处理并显示数据
    private void showUserRecommend(final ArrayList<User> dataList){
        final Runnable setAvatarRunable=new Runnable() {
            @Override
            public void run() {
                recommendUsersLayout.removeAllViews();
                for (User user:dataList){
                    View view=LayoutInflater.from(getContext()).inflate(R.layout.user_recommend_users_item,recommendUsersLayout,false);
                    ImageView imgUser=view.findViewById(R.id.user_recommend_users_img);
                    TextView tvUserName=view.findViewById(R.id.user_recommend_users_name);
                    TextView tvUserInfo=view.findViewById(R.id.user_recommend_users_info);
                    Button btnAdd=view.findViewById(R.id.user_recommend_users_add);
                    RequestOptions options=new RequestOptions()
                            .error(R.mipmap.ic_image_error)
                            .placeholder(R.mipmap.ic_image_error);
                    Glide.with(getContext())
                            .asBitmap()
                            .load(user.getUserImg())
                            .apply(options)
                            .into(imgUser);
                    tvUserName.setText(user.getUserName());
                    if (user.getUserIntroduction().equals("null")){
                        tvUserInfo.setText("这个人什么也没有留下");
                    }else {
                        tvUserInfo.setText(user.getUserIntroduction());
                    }
                    recommendUsersLayout.addView(view);
                    }
            }
        };
            new Thread(){
                public void run(){
                    msgHandler.post(setAvatarRunable);
                }
            }.start();

    }
}
