package com.example.my.mamer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;

public class UserFragment extends Fragment {

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


    }





    //    处理并显示数据
    private void showUserInfo(){

    }

}
