package com.example.my.mamer.util;

import android.content.Context;

import com.example.my.mamer.MyApplication;

public class BaseUtils {
    private  static  BaseUtils utils;
    private Context context;

    private BaseUtils(){
        this.context=MyApplication.getContext();
    }

    public static synchronized BaseUtils getInstance(){
        if (utils==null){
            utils=new BaseUtils();
        }
        return utils;
    }
//    dp转px
    public int dip2px(int dipValue){
        float reSize=context.getResources().getDisplayMetrics().density;
        return (int) ((dipValue*reSize)+0.5);
    }
//    根据手机分辨率从px转dp
    public int px2dip(float pxValue){
        final  float scale=context.getResources().getDisplayMetrics().density;
        return (int) (pxValue/scale+0.5f);
    }
//    将sp转px，保证文字大小不变
    public int sp2px(float spValue){
        final float fontSize=context.getResources().getDisplayMetrics().density;
        return (int) (spValue*fontSize+0.5f);
    }
}
