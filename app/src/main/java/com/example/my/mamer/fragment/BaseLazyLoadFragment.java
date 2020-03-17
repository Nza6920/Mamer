package com.example.my.mamer.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseLazyLoadFragment extends Fragment {
//    fragment的view加载完毕
    private boolean isViewCreated;
//    fragment是否对用可见
    private boolean isVisible;
//是否第一次加载
//    private boolean isFirstLoad=false;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view=initView(inflater,container);
        initEvent();
        return view;
    }

//    视图创建后请求内容
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated=true;
        onLazyLoad();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
//        当View创建完成时，用户可见的请求，且仅当是第一次对用户可见的时候请求自动数据
        if (isVisibleToUser){
            isVisible=true;
            isCanLoadData();

        }else {
            isVisible=false;
        }
    }
    private void isCanLoadData(){
        if (isViewCreated&&isVisible){
            onLazyLoad();
            isViewCreated=false;
            isVisible=false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated=false;
        isVisible=false;

    }

    //    数据加载接口
    public abstract void onLazyLoad();
//    初始化视图
    public  abstract View initView(LayoutInflater inflater,ViewGroup container);
//    初始化事件接口
    public  abstract void initEvent();

}
