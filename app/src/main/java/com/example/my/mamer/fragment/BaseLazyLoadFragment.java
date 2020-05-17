package com.example.my.mamer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 加载视图时，不加载数据，数据加载在每个fragment内
 */
public abstract class BaseLazyLoadFragment extends Fragment {
//    fragment的view加载完毕
    private boolean isViewCreated;
//    fragment是否对用可见
    private boolean isVisible;
//是否第一次加载
    private boolean isFirstLoad=true;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=initView(inflater,container);
        initEvent();
        Log.e("BaseLazy: onCreateView",view+"====");
        return view;
    }

//    视图创建后请求内容
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    //    数据加载接口
    public abstract void onLazyLoad(int page);
//    初始化视图
    public  abstract View initView(LayoutInflater inflater,ViewGroup container);
//    初始化事件接口
    public  abstract void initEvent();


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
