package com.example.my.mamer;

import android.app.Application;

import com.example.my.mamer.config.GlobalUserInfo;

public class MyApplication extends Application {
    private static  MyApplication context;
//    将globalUserInfo的信息,存在全局，不易丢失
    public static GlobalUserInfo globalUserInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        globalUserInfo =GlobalUserInfo.userInfo;
    }

//    获取全局Context对象
    public  static  MyApplication getContext(){
        return context;
    }
}
