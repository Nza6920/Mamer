package com.example.my.mamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserFragment extends Fragment {
//    标题栏
//    mamer
    private TextView tvMamer;
//    头像
    private Button btnUserImg;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user,container,false);
        return view;

    }
}
