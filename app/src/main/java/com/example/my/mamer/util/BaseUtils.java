package com.example.my.mamer.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.chinalwb.are.AREditText;
import com.example.my.mamer.MyApplication;
import com.example.my.mamer.R;

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

    //    解析html
    public static void contentUtil(Context context, AREditText mEditText, String contentStr){
//        从String加载文档
        mEditText.setFocusable(false);
        mEditText.setFocusableInTouchMode(false);
        Drawable edit=ContextCompat.getDrawable(context,R.drawable.backgraoud_color);
        mEditText.setBackground(edit);
        mEditText.fromHtml(contentStr);
    }

//    显示键盘
    public static void keyboardUpUtil(Context context,View view){
        view.requestFocus();
        InputMethodManager inputMethodManager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager!=null){
            inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }

    }
//    隐藏键盘
    public static void keyboardDownUtil(Context context,View view){
        InputMethodManager inputMethodManager= (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager!=null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }
//    字符串反转，输出前三位
    public static String reverseString(String s){
        StringBuffer stringBuffer=new StringBuffer(s);
        String stringT=stringBuffer.reverse().toString().substring(0,3);
        return stringT;


    }
}
