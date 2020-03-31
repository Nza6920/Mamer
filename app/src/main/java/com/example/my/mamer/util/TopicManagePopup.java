package com.example.my.mamer.util;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.my.mamer.R;
import com.example.my.mamer.util.PopupItemStyle.PopupStyle;

import java.util.ArrayList;

/**
 * popupwindow工具类，
 * 可以添加操作控件，数量可改变
 * 1、需要一个添加item的接口，将需要的item的图片和文字加入
 *  1、需要在popupwindow画出imageView和TextView才能将图片和文字显示
 * 2、需要一个动作按钮的点击监听返回接口，当前页面需要对不同按钮做不同的响应
 * 3、可以暂时将popupwindow的高度设置为定值
 */
public  class TopicManagePopup  {

    private Context mContext;
    private int codeId;
    private PopupStyle popupStyle;
    private ClickListener mCallBack;

    public TopicManagePopup(Context context, SparseArray<LinearLayout> viewSparseArray, ArrayList<LinearLayout> views, ClickListener callback) {
        this.mContext=context;
        this.mCallBack=callback;
        setCallBack(callback);

        TopicManagePopupUtil  popupUtil=TopicManagePopupUtil.createView(context,viewSparseArray,views,mCallBack);
    }

    public static class TopicManagePopupUtil{
       private ClickListener mCallBack;
       private static PopupWindow popupWindow;
       private SparseArray<LinearLayout> viewSparseArray;
       private View item;

        private TopicManagePopupUtil(Context context,ClickListener mCallBack, SparseArray<LinearLayout> viewSparseArray) {
            this.viewSparseArray=viewSparseArray;
            this.mCallBack = mCallBack;
        }
        public static TopicManagePopupUtil createView(final Context context,SparseArray<LinearLayout> viewSparseArray,ArrayList<LinearLayout> views,ClickListener callback){

//        获取整个popupWindow样式
            LayoutInflater mInflater= (LayoutInflater) ((Activity)context).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popupView= mInflater.inflate(R.layout.popup_util,null);

            TopicManagePopupUtil popupUtil=new TopicManagePopupUtil(context,callback,viewSparseArray);
//        获取屏幕高宽
            int weight= context.getResources().getDisplayMetrics().widthPixels;
            final int height=context.getResources().getDisplayMetrics().heightPixels*1/6;
//            实例化popupwindow
            popupWindow=new PopupWindow(popupView,weight,height);
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
//            设置弹出窗体可点击
            popupWindow.setFocusable(true);
            popupWindow.setTouchable(true);
//        点击外部popupwindow消失
            popupWindow.setOutsideTouchable(true);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//            设置内部item

            for (int i = 0; i < views.size(); i++) {
                LinearLayout linearLayout=popupView.findViewById(R.id.popup_layout_content);
                linearLayout.addView(views.get(i));
            }
            TextView tvClose=popupView.findViewById(R.id.popup_close_);
            tvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
//            点击事件回调
            if (popupWindow!=null){
                callback.setUplistener(popupUtil);
            }
//
//        屏幕不透明
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams layoutParams=((Activity)context).getWindow().getAttributes();
                    layoutParams.alpha=1.0f;
                    ((Activity)context).getWindow().setAttributes(layoutParams);
                }
            });

            WindowManager.LayoutParams layoutParam=((Activity)context).getWindow().getAttributes();
            layoutParam.alpha=0.3f;
            ((Activity)context).getWindow().setAttributes(layoutParam);
            popupWindow.showAtLocation(popupView,Gravity.BOTTOM,0,10);
            return popupUtil;
        }
        //    得到视图
        public  LinearLayout  getView(int id){
            LinearLayout t=  viewSparseArray.get(id);
            if (t ==null){
                t=item.findViewById(id);
            }

            return t;
        }

        public void dismiss() {
                popupWindow.dismiss();

        }

    }

    public interface ClickListener{
        void setUplistener(TopicManagePopupUtil popupUtil);
    }

    private void setCallBack(ClickListener callback){
        this.mCallBack=callback;
    }

}