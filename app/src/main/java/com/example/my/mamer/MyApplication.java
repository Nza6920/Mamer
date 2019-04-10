package com.example.my.mamer;

import android.app.Application;

public class MyApplication extends Application {
    private static  MyApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
    }

//    获取全局Context对象
    public  static  MyApplication getContext(){
        return context;
    }
}
