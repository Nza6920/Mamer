package com.example.my.mamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.my.mamer.util.CircleImageView;

public class UserMenuRightFragment extends Fragment {
    private DrawerLayout drawerLayout;
//    title
    private TextView tvClose;
//    用户头像
    private CircleImageView imgUserAvatar;
//    用户名
    private TextView tvUserName;
//    用户资料按钮
    private TextView tvUserInfo;
//    退出登录
    private TextView tvUserOut;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_menu_right,container,false);
        imgUserAvatar=view.findViewById(R.id.user_menu_avatar);
        tvUserName=view.findViewById(R.id.user_menu_name);
        tvUserInfo=view.findViewById(R.id.user_menu_info);
        tvUserOut=view.findViewById(R.id.user_menu_out);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tvUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().startActivity(new Intent(getActivity(),UserHomePageActivity.class));
                drawerLayout.closeDrawer(Gravity.END);
            }
        });
    }
//    暴露给Activity,用于传入DranerLayout,因为点击后想关掉DrawerLayout
    public void setDrawerLayout(DrawerLayout drawerLayout){
        this.drawerLayout=drawerLayout;
    }
}
